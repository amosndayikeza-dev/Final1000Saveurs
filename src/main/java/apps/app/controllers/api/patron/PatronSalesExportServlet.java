package apps.app.controllers.api.patron;

import apps.app.dao.SaleDAO;
import apps.app.models.Sale;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/patron/reports/sales/export")
public class PatronSalesExportServlet extends HttpServlet {
    private SaleDAO saleDAO = new SaleDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String deptIdStr = req.getParameter("departement_id");
        String startDateStr = req.getParameter("start_date");
        String endDateStr = req.getParameter("end_date");

        try {
            // Récupération des données filtrées
            List<Sale> sales;
            if (deptIdStr != null && !deptIdStr.isEmpty()) {
                int deptId = Integer.parseInt(deptIdStr);
                if (startDateStr != null && !startDateStr.isEmpty() && endDateStr != null && !endDateStr.isEmpty()) {
                    java.sql.Date start = java.sql.Date.valueOf(startDateStr);
                    java.sql.Date end = java.sql.Date.valueOf(endDateStr);
                    sales = saleDAO.findByDepartementAndDate(deptId, start, end);
                } else {
                    sales = saleDAO.findByDepartement(deptId);
                }
            } else {
                if (startDateStr != null && !startDateStr.isEmpty() && endDateStr != null && !endDateStr.isEmpty()) {
                    java.sql.Date start = java.sql.Date.valueOf(startDateStr);
                    java.sql.Date end = java.sql.Date.valueOf(endDateStr);
                    sales = saleDAO.findByDateRange(start, end);
                } else {
                    sales = saleDAO.findAll();
                }
            }

            // Création du classeur
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Ventes");

            // Styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            // En-tête
            String[] headers = {"ID", "Date", "Département", "Montant total", "Créé par", "Notes"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Remplissage des données
            int rowNum = 1;
            for (Sale sale : sales) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(sale.getId());
                row.createCell(1).setCellValue(sale.getSoldAt());
                row.getCell(1).setCellStyle(dateStyle);
                row.createCell(2).setCellValue(sale.getDepartementId());
                row.createCell(3).setCellValue(sale.getTotalAmount());
                row.getCell(3).setCellStyle(currencyStyle);
                row.createCell(4).setCellValue(sale.getCreatedBy());
                row.createCell(5).setCellValue(sale.getNotes() != null ? sale.getNotes() : "");
            }

            // Ligne de total
            Row totalRow = sheet.createRow(rowNum);
            Cell totalLabel = totalRow.createCell(2);
            totalLabel.setCellValue("Total général :");
            Cell totalValue = totalRow.createCell(3);
            totalValue.setCellFormula("SUM(D2:D" + rowNum + ")");
            totalLabel.setCellStyle(headerStyle);
            totalValue.setCellStyle(currencyStyle);

            // Ajustement des colonnes
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Envoi de la réponse
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment; filename=ventes_" + System.currentTimeMillis() + ".xlsx");
            try (OutputStream out = resp.getOutputStream()) {
                workbook.write(out);
            }
            workbook.close();
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
        return style;
    }
}