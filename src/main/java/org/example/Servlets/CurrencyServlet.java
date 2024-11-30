package org.example.Servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.DTO.CurrencyDTO;
import org.example.Exceptions.*;
import org.example.Service.interfaces.CreatableAndReadableService;
import org.example.Service.CurrencyService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/currencies/*")
public class CurrencyServlet extends HttpServlet  {

    private final CreatableAndReadableService<String, CurrencyDTO> service = new CurrencyService();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var writer = resp.getWriter();
        resp.setContentType("json");

        try {

            if (req.getPathInfo() != null) {
                if (req.getPathInfo().substring(1).isEmpty()) throw new MissingDataRequestException("The currency code is missing in the request!");

                Optional<CurrencyDTO> returnedCurrencyDTO = service.read(req.getPathInfo().substring(1));
                if (returnedCurrencyDTO.isEmpty()) throw new NoDataFoundException("The currency was not found!");
                returnedCurrencyDTO.ifPresent(writer::print);
                return;
            }

            List<CurrencyDTO> currenciesDTO = service.readAll();

            var stringBuilder = new StringBuilder("[\n");
            for (CurrencyDTO currencyDTO : currenciesDTO) {
                stringBuilder.append(currencyDTO).append(",").append("\n");
            }
            stringBuilder.delete(stringBuilder.length() - 3, stringBuilder.length() - 2).append("]");

            writer.print(stringBuilder);
        } catch (DatabaseUnavailableException e) {
            writer.print(e);
            resp.setStatus(500);
        } catch (MissingDataRequestException e) {
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

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var writer = resp.getWriter();
        resp.setContentType("json");

        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        try {
            if (code == null || name == null || sign == null) throw new MissingDataRequestException("Required field is missing in the request!");
            if (code.isBlank() || code.length() != 3 || name.isBlank() || sign.length() > 2) throw new IncorrectDataException("Invalid field values!");

            service.create(new CurrencyDTO(code, name, sign));

            Optional<CurrencyDTO> returnedCurrencyDTO = service.read(req.getParameter("code"));
            returnedCurrencyDTO.ifPresent(writer::print);

            resp.setStatus(201);
        } catch (DatabaseUnavailableException e) {
            writer.print(e);
            resp.setStatus(500);
        } catch (DataDuplicationException e) {
            writer.print(e);
            resp.setStatus(409);
        } catch (MissingDataRequestException | IncorrectDataException ex) {
            writer.print(ex);
            resp.setStatus(400);
        } catch (NoDataFoundException e) {
            writer.print(e);
            resp.setStatus(404);
        }
        finally {
            writer.close();
        }
    }


}
