package apps.app.controllers.api.patron;

import apps.app.dao.DebtDAO;
import apps.app.dao.DepartementDAO;
import apps.app.dao.EmployeeDAO;
import apps.app.dao.UserDAO;
import apps.app.models.Debt;
import apps.app.models.Employee;
import apps.app.models.Users;
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
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/api/patron/reports/debts/export/excel")
public class PatronDebtsExportExcelServlet extends HttpServlet {

    private DebtDAO debtDAO = new DebtDAO();
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Récupérer les filtres (identique à votre API JSON)
        String status = req.getParameter("status");
        String deptIdStr = req.getParameter("departement_id");

        // 2. Charger les dettes selon les filtres
        List<Debt> debts = List.of();
        try {
            if (deptIdStr != null && !deptIdStr.isEmpty()) {
                int deptId = Integer.parseInt(deptIdStr);
                debts = debtDAO.findByDepartementAndStatus(deptId, status);
            } else {
                if (status != null && !status.isEmpty()) {
                     debtDAO.findByStatus(status);
                } else {
                    debts = debtDAO.findAll();
                }
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Erreur lors du chargement des dettes : " + e.getMessage());
            return;
        }

        // 3. Créer le fichier Excel
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Rapport Dettes");

            // Styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // En-têtes
            String[] columns = {
                    "ID Dette", "Type débiteur", "Nom débiteur", "Département",
                    "Montant (€)", "Date échéance", "Statut", "Date paiement",
                    "Montant payé (€)", "Employé associé"
            };
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Remplir les lignes
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int rowNum = 1;
            for (Debt debt : debts) {
                Row row = sheet.createRow(rowNum++);

                // ID
                row.createCell(0).setCellValue(debt.getId());
                // Type débiteur
                row.createCell(1).setCellValue(debt.getDebtorType());
                // Nom débiteur
                row.createCell(2).setCellValue(debt.getDebtorName() != null ? debt.getDebtorName() : "");
                // Département
                String deptName = debtDAO.findDepartementNameByDebtId(debt.getId());
                row.createCell(3).setCellValue(deptName != null ? deptName : "Inconnu");
                // Montant
                row.createCell(4).setCellValue(debt.getAmount());
                // Date échéance
                row.createCell(5).setCellValue(debt.getDueDate() != null ? sdf.format(debt.getDueDate()) : "");
                // Statut
                row.createCell(6).setCellValue(debt.getStatus());
                // Date paiement
                row.createCell(7).setCellValue(debt.getPaidAt() != null ? sdf.format(debt.getPaidAt()) : "");
                // Montant payé
                double paid = (debt.getPaidAmount() != null) ? debt.getPaidAmount() : 0;
                row.createCell(8).setCellValue(paid);
                // Employé associé (si dette employé)
                String employeeName = "";
                if ("employee".equals(debt.getDebtorType()) && debt.getEmployeeId() != null) {
                    Employee emp = employeeDAO.findById(debt.getEmployeeId());
                    if (emp != null && emp.getUserId() != 0) {
                        Users user = userDAO.findById(emp.getUserId());
                        if (user != null) {
                            employeeName = user.getFirstName() + " " + user.getLastName();
                        }
                    }
                }
                row.createCell(9).setCellValue(employeeName);
            }

            // Ajuster la largeur des colonnes après remplissage
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 4. Envoyer la réponse pour téléchargement
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment; filename=\"rapport_dettes_" + System.currentTimeMillis() + ".xlsx\"");
            try (OutputStream out = resp.getOutputStream()) {
                workbook.write(out);
                out.flush();
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Erreur lors de la génération Excel : " + e.getMessage());
        }
    }
}