package apps.app.controllers.api.patron;

import apps.app.dao.SaleDAO;
import apps.app.dao.DepartementDAO;
import apps.app.models.Sale;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/patron/reports/sales")
public class PatronSalesReportApiServlet extends HttpServlet {

    private SaleDAO saleDAO = new SaleDAO();
    private DepartementDAO departementDAO = new DepartementDAO();
    private Gson gson = new Gson();

    //configurer json pour formater les dates en "YYY-MM-DD"
    public void init() throws ServletException{
        gson = new GsonBuilder().setDateFormat("yyy-MM-dd").create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String deptIdStr = req.getParameter("departement_id");
        String startDateStr = req.getParameter("start_date");
        String endDateStr = req.getParameter("end_date");

        resp.setContentType("application/json");
        try {
             //recuper les ventes selon le filtre
            List<Sale> sales = fetchSales(deptIdStr,startDateStr,endDateStr);

            //calculer le chiffre d'ffaire total
            double totalCA =0.0;
            for (Sale sale : sales){
                totalCA += sale.getTotalAmount();
            }

            //Enrichir chaque vente avec le nom du departement
            List<Map<String,Object>> enrichedSales = new ArrayList<>();
            for (Sale sale : sales){
                Map<String,Object> enriched = new HashMap<>();
                enriched.put("id",sale.getId());
                enriched.put("sold_at",sale.getSoldAt());
                enriched.put("total_amount", sale.getTotalAmount());
                enriched.put("departement_id", sale.getDepartementId());

                // Récupérer le nom du département (gère le cas où le département n'existe plus)
                String deptName = "Inconnu";

                apps.app.models.Departement dept = departementDAO.findById(sale.getDepartementId());
                if (dept != null) deptName = dept.getName();

                enriched.put("departement_name", deptName);
                enriched.put("created_by", sale.getCreatedBy());
                enriched.put("notes", sale.getNotes() != null ? sale.getNotes() : "");

                enrichedSales.add(enriched);
            }

            // comstruire la reponse finale
            Map<String,Object> responseData = new HashMap<>();
            responseData.put("sales",enrichedSales);
            responseData.put("totalCA",totalCA);
            responseData.put("count",enrichedSales.size());

            //Envoyer la reponse
            resp.getWriter().write(gson.toJson(responseData));

        } catch (SQLException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     *
     * Méthode utilitaire pour récupérer les ventes selon les filtres.
     * @return liste de ventes (peut être vide)
     */

    private List<Sale> fetchSales(String deptIdStr,String startDateStr,String endDateStr) throws SQLException {
       List<Sale> sales;
       if (deptIdStr != null && !deptIdStr.isEmpty()){
           int deptId = Integer.parseInt(deptIdStr);
           if (startDateStr != null && !startDateStr.isEmpty() && endDateStr != null && !endDateStr.isEmpty()){
               Date start = Date.valueOf(startDateStr);
               Date end = Date.valueOf(endDateStr);
               sales = saleDAO.findByDepartementAndDate(deptId,start,end);
           }else {
               sales = saleDAO.findByDepartement(deptId);
           }
       }else {
           if (startDateStr != null && !startDateStr.isEmpty() && endDateStr != null && !endDateStr.isEmpty()){
               Date start = Date.valueOf(startDateStr);
               Date end = Date.valueOf(endDateStr);
               sales = saleDAO.findByDateRange(start,end);
           }else{
               sales = saleDAO.findAll();
           }
       }
       return sales;
    }
}