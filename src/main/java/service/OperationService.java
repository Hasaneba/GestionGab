package service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import connection.ConnectionGab;
import model.Compte;
import model.Operation;

public class OperationService {
	public List<Operation> findAll() {
        List<Operation> productList = new ArrayList<>();
        try (Connection connection = ConnectionGab.getConn();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM operation");
            while (resultSet.next()) {
                Operation op = new Operation();
                op.setIdOperation(resultSet.getLong("idOperation"));
                op.setDateOperation(resultSet.getString("dateOperation"));
                op.setTypeOperation(resultSet.getString("typeOperation"));
                op.setMontant(resultSet.getDouble("montant"));
                productList.add(op);    
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }
    // Modifier  operation
    
    public void update(Operation operation){
        try (Connection connection = ConnectionGab.getConn();
             PreparedStatement statement = connection.prepareStatement("UPDATE operation SET dateOperation = ?, typeOperation = ?, montant = ? WHERE idOperation = ?")) {
        	statement.setString(1, operation.getDateOperation());
            statement.setString(2, operation.getTypeOperation());
            statement.setDouble(3, operation.getMontant());
            statement.setLong(4, operation.getIdOperation());
            System.out.println("operation"+operation);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // supprimer operation 
    
    public void delete(Long    idOperation) {
        try (Connection connection = ConnectionGab.getConn();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM operation WHERE idOperation = ?")) {
            statement.setLong(1, idOperation);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Modifier by ID 
    
    public Operation findById(Long idOperation){
    	Operation op = new Operation();
        try (Connection connection =ConnectionGab.getConn();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM client c JOIN compte e ON c.idclient = e.idclient JOIN operation p ON p.idcompte = e.idcompte  where idOperation = ?;")) {
            statement.setLong(1, idOperation);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
            	 op = new Operation();
                op.setIdOperation(resultSet.getLong("idOperation"));
                op.setDateOperation(resultSet.getString("dateOperation"));
                op.setTypeOperation(resultSet.getString("typeOperation"));
                op.setMontant(resultSet.getDouble("montant"));
                op.setNumeroCompte(resultSet.getInt("numeroCompte"));
                op.setNom(resultSet.getString("nom"));                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return op;
    }
    
    // method retourner longeur 
    public int longueur() {
        int length = 0;
        String sql = "SELECT COUNT(*) AS longueur FROM operation ";

        try (Connection conn = ConnectionGab.getConn();
             PreparedStatement p = conn.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                length = r.getInt("longueur");
                System.out.println("La longueur de la liste est " + length);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return length;
    }
    
   
    
   
 
    
    //  barre de recherche avec pagination 
    public List<Operation> getData(int page, int pageSize, String searchTerm) {
        List<Operation> productList = new ArrayList<>();

        try (Connection conn = ConnectionGab.getConn()) {
            String sql = "select  p.idOperation,p.dateOperation,p.typeOperation,p.montant,e.nom,c.numeroCompte from client e ,compte c,operation p " +
                         "WHERE c.idclient = e.idclient and  p.idcompte=c.idcompte";
            if (searchTerm != null && !searchTerm.isEmpty()) {
                sql += " AND p.typeOperation LIKE ?";
            }
            sql += " ORDER BY p.idcompte"; // Assurez-vous d'avoir une clause ORDER BY pour garantir une pagination cohérente
            sql += " LIMIT ? OFFSET ?"; // Ajoutez des paramètres pour la pagination

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int paramIndex = 1;
                if (searchTerm != null && !searchTerm.isEmpty()) {
                    stmt.setString(paramIndex++, "%" + searchTerm + "%");
                }

                // Définissez les paramètres pour la pagination
                stmt.setInt(paramIndex++, pageSize);
                stmt.setInt(paramIndex++, (page - 1) * pageSize);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                    	Operation op = new Operation();
                        op.setIdOperation(rs.getLong("idOperation"));
                        op.setDateOperation(rs.getString("dateOperation"));
                        op.setTypeOperation(rs.getString("typeOperation"));
                        op.setMontant(rs.getDouble("montant"));
                        op.setNumeroCompte(rs.getInt("numeroCompte"));
                        op.setNom(rs.getString("nom"));
                        productList.add(op);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }
    
    // Operation 
    
    
    
    public List<Integer> getResultPageNumbers(int pageSize, String searchTerm) {
        List<Integer> resultPageNumbers = new ArrayList<>();

        try (Connection conn = ConnectionGab.getConn()) {
            String sql ="SELECT COUNT(*) AS total FROM client c JOIN compte e ON c.idclient = e.idclient " +
                    "JOIN operation p ON p.idcompte = e.idcompte";
            if (searchTerm != null && !searchTerm.isEmpty()) {
                sql += " WHERE p.typeOperation LIKE ?";
            }

            try (PreparedStatement countStmt = conn.prepareStatement(sql)) {
                if (searchTerm != null && !searchTerm.isEmpty()) {
                    countStmt.setString(1, "%" + searchTerm + "%");
                }

                try (ResultSet countRs = countStmt.executeQuery()) {
                    if (countRs.next()) {
                        int totalResults = countRs.getInt("total");
                        int totalPages = (int) Math.ceil((double) totalResults / pageSize);
                        for (int i = 1; i <= totalPages; i++) {
                            resultPageNumbers.add(i);
                        }
                        System.out.println("le resultat" + resultPageNumbers);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultPageNumbers;
    }
    
    
    
    // Importation de pdf 
    
    public String generatePDF(List<Operation> productList) {
        String pdfFilePath = "C:/Users/Lenovo/Documents/pdf/output_" + System.currentTimeMillis() + ".pdf";
        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
            document.open();

            // Créez la table avec 6 colonnes
            PdfPTable table = new PdfPTable(6);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 19); // Police en-tête avec taille 12

            // Ajoutez les cellules avec les en-têtes
            addHeaderCell(table, "IdOperation",headerFont);
            addHeaderCell(table, "DateOperation",headerFont);
            addHeaderCell(table, "TypeOperation",headerFont);
            addHeaderCell(table, "Montant",headerFont);
            addHeaderCell(table, "NumCompte",headerFont);
            addHeaderCell(table, "NomClient",headerFont);

            // Ajoutez les données des comptes à la table
            for (Operation  operation : productList) {
                addDataCell(table, String.valueOf(operation.getIdOperation()));
                addDataCell(table,operation.getDateOperation());
                addDataCell(table, operation.getTypeOperation());
                addDataCell(table, String.valueOf(operation.getMontant()));
                addDataCell(table, String.valueOf(operation.getNumeroCompte()));
                addDataCell(table, operation.getNom());
            }

            // Ajoutez la table au document PDF
            table.setWidthPercentage(100); // Utilisez la largeur de la page entière
            table.setWidths(new int[]{10, 13, 10, 10, 10, 13}); 
            document.add(table);
            document.close();
        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
        }
        return pdfFilePath;
    }
    
    // Méthode utilitaire pour ajouter une cellule d'en-tête à la table
    public void addHeaderCell(PdfPTable table, String header,Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(10); // Ajustez le rembourrage selon vos besoins
        cell.setPhrase(new Phrase(header));
        table.addCell(cell);
    }

    // Méthode utilitaire pour ajouter une cellule de données à la table
    public void addDataCell(PdfPTable table, String data) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(10); // Ajustez le rembourrage selon vos besoins
        cell.setPhrase(new Phrase(data));
        table.addCell(cell);
    }

    
    // ListePagination
    public List<Compte> Pagination(int page, int pageSize) {
        List<Compte> productList = new ArrayList<>();
        try (Connection conn = ConnectionGab.getConn()) {
       String sql = 	"select  p.idOperation,p.dateOperation,p.typeOperation,p.montant,e.nom,c.numeroCompte from client e ,compte c,operation p " +
                    "WHERE c.idclient = e.idclient and  p.idcompte=c.idcompte";
            sql += " ORDER BY p.idcompte"; // Assurez-vous d'avoir une clause ORDER BY pour garantir une pagination cohérente
            sql += " LIMIT ? OFFSET ?"; // Ajoutez des paramètres pour la pagination

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Définissez les paramètres pour la pagination
                stmt.setInt(1, pageSize);
                stmt.setInt(2, (page - 1) * pageSize);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                    	Operation op = new Operation();
                        op.setIdOperation(rs.getLong("idOperation"));
                        op.setDateOperation(rs.getString("dateOperation"));
                        op.setTypeOperation(rs.getString("typeOperation"));
                        op.setMontant(rs.getDouble("montant"));
                        op.setNumeroCompte(rs.getInt("numeroCompte"));
                        op.setNom(rs.getString("nom"));
                        System.out.println("nom"+rs.getString("nom")+"idcompte"+rs.getInt("idcompte")+""+productList);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    
 // Longeur 
    
    public int  LongeurElement(String search) {
    	int totalRes =0 ;
    	PreparedStatement statement = null ;
    String sql = "SELECT COUNT(*) AS total FROM client c JOIN compte e ON c.idclient = e.idclient"+"JOIN operation p ON p.idcompte = e.idcompte";
    //client c JOIN
    //ON c.idclient = e.idclient
    try (Connection conn = ConnectionGab.getConn()) {
    		statement =conn.prepareStatement(sql);
    		statement.setString(1, "%" + search + "%");
    		ResultSet countRs =statement.executeQuery();
    		while(countRs.next()) {
    			totalRes = countRs.getInt("total");
    			System.out.println("longeur"+totalRes);
    		}
    }
    catch(SQLException e) {
    	e.printStackTrace();
    }
 return totalRes;
    }
    
}
