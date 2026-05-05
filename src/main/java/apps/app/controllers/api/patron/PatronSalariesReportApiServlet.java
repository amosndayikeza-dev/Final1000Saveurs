package apps.app.controllers.api.patron;

import apps.app.dao.DepartementDAO;
import apps.app.dao.SalaryReportDAO;
import apps.app.dao.UserDAO;
import apps.app.models.Departement;
import apps.app.models.SalaryReport;
import apps.app.models.Users;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/patron/reports/salaries")
public class PatronSalariesReportApiServlet extends HttpServlet {

    private SalaryReportDAO salaryReportDAO = new SalaryReportDAO();
    private DepartementDAO departementDAO = new DepartementDAO();
    private UserDAO userDAO = new UserDAO();
    private Gson gson = new Gson();

    public void init() throws ServletException{
        //formater la date pour une meuilleur lisibilite
        gson = new GsonBuilder().setDateFormat("yyy-MM-dd").create();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //recuperer les paramettre de filtrage
        String deptIdStr = req.getParameter("departement_id");
        String status = req.getParameter("status");
        String yearStr = req.getParameter("year");
        String monthStr = req.getParameter("month");
        resp.setContentType("application/json");

        try{
            List<SalaryReport> reports = fetchReports(deptIdStr,status,yearStr,monthStr);

            // Enrichirer chaque rapport avec les noms du departement
            List<Map<String,Object>> enrichedRepports = new ArrayList<>();

            for (SalaryReport report : reports){
                Map<String,Object> enriched = new HashMap<>();
                enriched.put("id",report.getId());
                enriched.put("month",report.getMonth());
                enriched.put("year",report.getYear());
                enriched.put("total_salary",report.getTotalSalary());
                enriched.put("status",report.getStatus());
                enriched.put("submitted_at",report.getSubmittedAt());
                enriched.put("approved_at",report.getApprovedAt());

                //nom du departement
                String deptName = "Inconnu";
                try{
                    Departement dept = departementDAO.findById(report.getDepartementId());
                    if (dept != null) deptName = dept.getName();
                } catch (Exception e) {
                    //throw new RuntimeException(e);
                }
                enriched.put("departement_name",deptName);

                //Nom du manager
                String managerName = "Inconnu";
                try{
                    Users manager = userDAO.findById(report.getManagerId());
                    if (manager != null) {

                        managerName = manager.getFirstName() + " " + manager.getLastName();
                    }
                } catch (Exception e) {
                   // throw new RuntimeException(e);
                }
                enriched.put("manager_name",managerName);
                enrichedRepports.add(enriched);
            }

            //Calculer les indicateurs statistiques(somme des salaires en attente /approuves)

            double totalPending = 0.0;
            double totalApproved = 0.0;
            for (SalaryReport report : reports){
                if ("pending".equals(report.getStatus())){
                    totalPending += report.getTotalSalary();
                }else if("approved".equals(report.getStatus())){
                    totalApproved += report.getTotalSalary();
                }
            }

            //Construire la reponse finale
            Map<String,Object> responseData = new HashMap<>();
            responseData.put("reports",enrichedRepports);
            responseData.put("total_pending",totalPending);
            responseData.put("total_approved",totalApproved);
            responseData.put("count",enrichedRepports.size());

            resp.getWriter().write(gson.toJson(reports));
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (SQLException e) {
            //throw new RuntimeException(e);
        }

    }

    /**
     * Récupère les rapports en appliquant les filtres (département, statut, année, mois).
     */
    private List<SalaryReport> fetchReports(String deptIdStr,String status,String yearStr,String monthStr) throws SQLException{
        List<SalaryReport> reports;

        //Filtrage par departement
        if (deptIdStr !=null && !deptIdStr.isEmpty()){
            int deptId = Integer.parseInt(deptIdStr);
            if (status != null && !status.isEmpty()){
                reports = salaryReportDAO.findByStatusAndDepartment(status,deptId);
            }else {
                reports = salaryReportDAO.findByDepartement(deptId);
            }
        }else{
            if (status != null && !status.isEmpty()){
                reports = salaryReportDAO.findByStatus(status);
            }else {
                reports = salaryReportDAO.findAll();
            }
        }

        //Filtrage par annee et mois
        if (yearStr != null && !yearStr.isEmpty()){
            int year = Integer.parseInt(yearStr);
            reports.removeIf(r -> r.getYear() != year);
        }
        if (monthStr != null && !monthStr.isEmpty()){
            int month = Integer.parseInt(monthStr);
            reports.removeIf(r -> r.getMonth() != month);
        }

        return reports;
    }
}