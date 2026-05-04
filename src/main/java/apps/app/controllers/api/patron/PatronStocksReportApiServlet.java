package apps.app.controllers.api.patron;

import apps.app.dao.ProductDAO;
import apps.app.models.Product;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/patron/reports/stocks")
public class PatronStocksReportApiServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String deptIdStr = req.getParameter("departement_id");
        resp.setContentType("application/json");

        List<Product> products;

        try {
            // charger kes produits : tous ou filtres par departement
            if (deptIdStr != null && !deptIdStr.isEmpty()) {
                int deptId = Integer.parseInt(deptIdStr);
                products = productDAO.findByDepartement(deptId);
            } else {
                products = productDAO.findAll();
            }

            //calculer les indicateur statics
            double totalStockValue = 0.0;
            int lowStockValue = 0;

            for(Product p : products){
                double productValue = p.getCurrentStock() * p.getUnitPrice();

                totalStockValue +=productValue;

                if (p.getCurrentStock() <= p.getLowStockThreshold()){
                    lowStockValue++;
                }
            }

            //construire la reponse en json avec les donnees
            Map<String,Object> responsedata = new HashMap<>();
            responsedata.put("products",products); //liste detaille

            responsedata.put("totalStockValue",totalStockValue);//valeur totale

            responsedata.put("lowStockValue",lowStockValue); //nombre de produit en alerte

            resp.getWriter().write(gson.toJson(products));//serialiser en JSON et ecrire la reponse

        } catch (NumberFormatException e) {

           resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
           resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}