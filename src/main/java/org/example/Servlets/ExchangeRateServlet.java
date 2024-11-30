package org.example.Servlets;

import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.DTO.ExchangeRateDTO;
import org.example.DTO.ExchangeRateDtoToUpdate;
import org.example.Exceptions.*;
import org.example.Repository.ExchangeRateDAO;
import org.example.Repository.Interfaces.DAOwithUpdate;
import org.example.Service.ExchangeRateService;
import org.example.Service.interfaces.CreatableAndReadableService;
import org.example.Service.interfaces.UpdatableService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates/*")
public class ExchangeRateServlet extends HttpServlet {

    CreatableAndReadableService<String, ExchangeRateDTO> service = new ExchangeRateService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH")) {
            this.doPatch(req, resp);
            return;
        }
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var writer = resp.getWriter();
        resp.setContentType("json");

        try {

            if(req.getPathInfo() != null) {
                if (req.getPathInfo().substring(1).isBlank()) throw new MissingDataRequestException("There are no currency codes in the request!");
                if (req.getPathInfo().substring(1).length() != 6) throw new IncorrectDataException("Error in specifying the currency pair!");

                Optional<ExchangeRateDTO> returnedExchangeRateDTO = service.read(req.getPathInfo().substring(1));
                if (returnedExchangeRateDTO.isEmpty()) throw new NoDataFoundException("The exchange rate was not found!");
                returnedExchangeRateDTO.ifPresent(writer::print);
                return;
            }

            List<ExchangeRateDTO> exchangeRates = service.readAll();

            var stringBuilder = new StringBuilder("[\n");
            for (ExchangeRateDTO dto: exchangeRates) {
                stringBuilder.append(dto).append(",").append("\n");
            }
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1).append("]");

            writer.print(stringBuilder);
        } catch (DatabaseUnavailableException e) {
            writer.print(e);
            resp.setStatus(500);
        } catch (MissingDataRequestException | IncorrectDataException e) {
            writer.print(e);
            resp.setStatus(400);
        } catch (NoDataFoundException e) {
            writer.print(e);
            resp.setStatus(404);
        } finally {
            writer.close();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var writer = resp.getWriter();
        resp.setContentType("json");

        try {
            String baseCurrencyCode = req.getParameter("baseCurrencyCode");
            String targetCurrencyCode = req.getParameter("targetCurrencyCode");
            String rateStr = req.getParameter("rate");


            if (rateStr == null || baseCurrencyCode == null
                    || targetCurrencyCode == null) throw new MissingDataRequestException("Required fields are missing in the request!");

            if (rateStr.isBlank() || Double.parseDouble(rateStr) <= 0
                    || baseCurrencyCode.isBlank() || baseCurrencyCode.length() != 3
                    || targetCurrencyCode.isBlank() || targetCurrencyCode.length() != 3) throw new IncorrectDataException("Invalid field value!");

            ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
            exchangeRateDTO.setBaseCurrencyCode(baseCurrencyCode);
            exchangeRateDTO.setTargetCurrencyCode(targetCurrencyCode);
            exchangeRateDTO.setRate(Double.parseDouble(rateStr.replace(",", ".")));
            service.create(exchangeRateDTO);

            Optional<ExchangeRateDTO> returnedExchangeRate = service.read(baseCurrencyCode + targetCurrencyCode);

            returnedExchangeRate.ifPresent(writer::print);
            resp.setStatus(201);
        } catch (DatabaseUnavailableException e) {
            writer.print(e);
            resp.setStatus(500);
        } catch (DataDuplicationException e) {
            writer.print(e);
            resp.setStatus(409);
        } catch (IncorrectDataException | MissingDataRequestException e) {
            writer.print(e);
            resp.setStatus(400);
        } catch (NoDataFoundException e) {
            writer.print(e);
            resp.setStatus(404);
        }
        finally {
            writer.close();
        }
    }


    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UpdatableService<ExchangeRateDtoToUpdate> serviceUpdatable = new ExchangeRateService();
        var writer = resp.getWriter();
        resp.setContentType("json");

        try {
            String rate = req.getParameter("rate");

            if(rate == null) throw new MissingDataRequestException("The required request field is missing!");
            if(rate.isBlank() || Double.parseDouble(rate) <= 0) throw new IncorrectDataException("Invalid field value!");
            if(req.getPathInfo().substring(1).isBlank()) throw new MissingDataRequestException("There are no currency codes in the request!");
            if(req.getPathInfo().substring(1).length() != 6) throw new IncorrectDataException("Error in specifying the currency pair!");

            ExchangeRateDtoToUpdate exchangeRateDtoToUpdate = new ExchangeRateDtoToUpdate();
            exchangeRateDtoToUpdate.setBaseCode(req.getPathInfo().substring(1, 4));
            exchangeRateDtoToUpdate.setTargetCode(req.getPathInfo().substring(4, 7));
            exchangeRateDtoToUpdate.setRate(Double.parseDouble(rate));
            serviceUpdatable.update(exchangeRateDtoToUpdate);

            Optional<ExchangeRateDTO> returnedExchangeRate = service.read(req.getPathInfo().substring(1, 4) + req.getPathInfo().substring(4, 7));

            if (returnedExchangeRate.isEmpty()) throw new NoDataFoundException("The exchange rate was not found!");
            returnedExchangeRate.ifPresent(writer::print);

        } catch (MissingDataRequestException | IncorrectDataException e) {
            writer.print(e);
            resp.setStatus(400);
        } catch (DatabaseUnavailableException e) {
            writer.print(e);
            resp.setStatus(500);
        } catch (NoDataFoundException e) {
            writer.print(e);
            resp.setStatus(404);
        } finally {
            writer.close();
        }
    }
}
