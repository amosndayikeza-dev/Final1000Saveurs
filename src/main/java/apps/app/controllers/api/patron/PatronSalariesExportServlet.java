package apps.app.controllers.api.patron;

import apps.app.dao.DepartementDAO;
import apps.app.dao.SalaryReportDAO;
import apps.app.dao.UserDAO;
import apps.app.models.Departement;
import apps.app.models.SalaryReport;
import apps.app.models.Users;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/patron/reports/salaries/export")
public class PatronSalariesExportServlet {

    private SalaryReportDAO salaryReportDAO = new SalaryReportDAO();
    private DepartementDAO departementDAO = new DepartementDAO();
    private UserDAO userDAO = new UserDAO();


    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 1. Récupérer les paramètres de filtrage (optionnels)
        String deptIdStr = req.getParameter("departement_id");
        String status = req.getParameter("status");
        String yearStr = req.getParameter("year");
        String monthStr = req.getParameter("month");

        try {
            // 2. Récupérer les rapports selon les filtres
            List<SalaryReport> reports = fetchReports(deptIdStr, status, yearStr, monthStr);

            // 3. Créer le classeur Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Rapports salaires");

            // 4. Styles (en-tête)
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            // 5. En-têtes des colonnes
            String[] headers = {"ID", "Département", "Manager", "Mois", "Année",
                    "Total salaires (FCFA)", "Statut", "Date soumission", "Date approbation"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 6. Remplir les données
            int rowNum = 1;
            for (SalaryReport report : reports) {
                Row row = sheet.createRow(rowNum++);

                // ID
                row.createCell(0).setCellValue(report.getId());

                // Nom du département
                String deptName = "Inconnu";
                Departement dept = departementDAO.findById(report.getDepartementId());
                if (dept != null) deptName = dept.getName();
                row.createCell(1).setCellValue(deptName);

                // Nom du manager
                String managerName = "Inconnu";
                Users manager = userDAO.findById(report.getManagerId());
                if (manager != null) {
                    managerName = manager.getFirstName() + " " + manager.getLastName();
                }
                row.createCell(2).setCellValue(managerName);

                // Mois et année
                row.createCell(3).setCellValue(report.getMonth());
                row.createCell(4).setCellValue(report.getYear());

                // Total salaires (format monétaire)
                Cell totalCell = row.createCell(5);
                totalCell.setCellValue(report.getTotalSalary());
                totalCell.setCellStyle(currencyStyle);

                // Statut (affichage lisible)
                String statusText = "pending".equals(report.getStatus()) ? "En attente" : "Approuvé";
                row.createCell(6).setCellValue(statusText);

                // Date de soumission
                Cell submittedCell = row.createCell(7);
                if (report.getSubmittedAt() != null) {
                    submittedCell.setCellValue(report.getSubmittedAt());
                    submittedCell.setCellStyle(dateStyle);
                } else {
                    submittedCell.setCellValue("");
                }

                // Date d'approbation (peut être null)
                Cell approvedCell = row.createCell(8);
                if (report.getApprovedAt() != null) {
                    approvedCell.setCellValue(report.getApprovedAt());
                    approvedCell.setCellStyle(dateStyle);
                } else {
                    approvedCell.setCellValue("");
                }
            }

            // 7. Ajuster la largeur des colonnes
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 8. Configurer la réponse HTTP
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment; filename=rapport_salaires_" + System.currentTimeMillis() + ".xlsx");

            // 9. Écrire le classeur dans le flux de sortie
            try (OutputStream out = resp.getOutputStream()) {
                workbook.write(out);
            }
            workbook.close();

        } catch (SQLException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // Méthode utilitaire pour récupérer les rapports filtrés (identique à celle du JSON)
    private List<SalaryReport> fetchReports(String deptIdStr, String status, String yearStr, String monthStr)
            throws SQLException {
        List<SalaryReport> reports;
        if (deptIdStr != null && !deptIdStr.isEmpty()) {
            int deptId = Integer.parseInt(deptIdStr);
            if (status != null && !status.isEmpty()) {
                reports = salaryReportDAO.findByStatusAndDepartment(status, deptId);
            } else {
                reports = salaryReportDAO.findByDepartement(deptId);
            }
        } else {
            if (status != null && !status.isEmpty()) {
                reports = salaryReportDAO.findByStatus(status);
            } else {
                reports = salaryReportDAO.findAll();
            }
        }
        // Filtrage additionnel année/mois (si les méthodes DAO ne les incluent pas)
        if (yearStr != null && !yearStr.isEmpty()) {
            int year = Integer.parseInt(yearStr);
            reports.removeIf(r -> r.getYear() != year);
        }
        if (monthStr != null && !monthStr.isEmpty()) {
            int month = Integer.parseInt(monthStr);
            reports.removeIf(r -> r.getMonth() != month);
        }
        return reports;
    }

    // Styles
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
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
        return style;
    }
}
