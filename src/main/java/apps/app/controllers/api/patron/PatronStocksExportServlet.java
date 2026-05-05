package apps.app.controllers.api.patron;

import apps.app.dao.ProductDAO;
import apps.app.models.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.Port;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.ServerException;
import java.util.List;


@WebServlet("/api/patron/reports/stocks/export")
public class PatronStocksExportServlet {

    private ProductDAO productDAO = new ProductDAO();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServerException, IOException{
         //recuper le paramettre du departement
        String deptIdStr = req.getParameter("departement_id");
        List<Product> products;

        try{
            //charger les produits
            if (deptIdStr != null && !deptIdStr.isEmpty()){
                int deptId = Integer.parseInt(deptIdStr);
                products = productDAO.findByDepartement(deptId);
            }else{
                products = productDAO.findAll();
            }

            //creer un nouveuax classeurs Excel(.xlsx)
            Workbook workbook = new XSSFWorkbook();
            //CREER UNE FEUILLE NOMME STOCK
            Sheet sheet = workbook.createSheet("Stocks");

            //Definir le style pour l'en tete
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            //Creer la ligne pour l'en tete
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID","Deprtement","Produit","Prix unitaire(FBU)","Seuil d'alerte","Statut"};
            for (int i =0;i< columns.length; i++){
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            //remplir les ligns de donnees
            int rowNum = 1;
            for (Product p : products) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getDepartementId());
                row.createCell(2).setCellValue(p.getName());
                row.createCell(3).setCellValue(p.getUnitPrice());
                row.createCell(4).setCellValue(p.getCurrentStock());
                double totalValue = p.getCurrentStock() * p.getUnitPrice();
                row.createCell(5).setCellValue(totalValue);

                //statut : alerte si le stock <= seul, sinon OK
                row.createCell(6).setCellValue(p.getCurrentStock());
                String status = (p.getCurrentStock() <= p.getLowStockThreshold()) ? "ALERTE" : "OK";
                row.createCell(7).setCellValue(status);
            }
                //configurer la reponse HTTP pour forcer le telechargement

                resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                resp.setHeader("Content-Disposition", "attachment; filename=rapport_stocks_" + System.currentTimeMillis() + ".xlsx");

                //Ecrire le classeur dans le flux de sortie
                try(OutputStream out = resp.getOutputStream()){
                    workbook.write(out);
                }
                workbook.close();

        } catch (Exception e) {
            // 12. En cas d'erreur, retourner une réponse JSON d'erreur
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }






}
