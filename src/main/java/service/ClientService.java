package service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import connection.ConnectionGab;
import model.Client;


public class ClientService {
	 public void create(Client client){
		    
	        try( Connection connection = ConnectionGab.getConn();
	             PreparedStatement statement = connection.prepareStatement("INSERT INTO client (nom, prenom,adress,cp,ville,telephone) VALUES (?, ?,?,?,?,?)")) {
	            statement.setString(1, client.getNom());
	            statement.setString(2, client.getPrenom());
	            statement.setString(3, client.getAdress());
	            statement.setInt(4, client.getCp());
	            statement.setString(5, client.getVille());
	            statement.setInt(6, client.getTelephone());
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public Client findById(int idclient){
	        Client client = null;
	        try (Connection connection =ConnectionGab.getConn();
	             PreparedStatement statement = connection.prepareStatement("SELECT idclient, nom,prenom,adress,cp,ville,telephone FROM client WHERE idclient = ?")) {
	            statement.setInt(1, idclient);
	            ResultSet resultSet = statement.executeQuery();
	            if (resultSet.next()) {
	                client = new Client();
	                client.setIdclient(resultSet.getInt("idclient"));
	                client.setNom(resultSet.getString("nom"));
	                client.setPrenom(resultSet.getString("prenom"));
	                client.setAdress(resultSet.getString("adress"));
	               
	                client.setCp(resultSet.getInt("cp"));
	                client.setVille(resultSet.getString("ville"));
	                client.setTelephone(resultSet.getInt("telephone"));
	                
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return client;
	    } 

	    public List<Client> findAll() {
	        List<Client> productList = new ArrayList<>();
	        PreparedStatement statement = null ;
	     //   int offset = (page - 1) * pageSize;
	        String sql = "select * from client";
	        try (Connection connection = ConnectionGab.getConn();
	        		 ) {
	        	statement = connection.prepareStatement(sql);
	        	
	            ResultSet resultSet = statement.executeQuery();
	            while (resultSet.next()) {
	                Client client = new Client();
	                client.setIdclient(resultSet.getInt("idclient"));
	                client.setNom(resultSet.getString("nom"));
	                client.setPrenom(resultSet.getString("prenom"));
	                client.setAdress(resultSet.getString("adress"));
	               
	                client.setCp(resultSet.getInt("cp"));
	                client.setVille(resultSet.getString("ville"));
	                client.setTelephone(resultSet.getInt("telephone"));
	                productList.add(client);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return productList;
	    }

	    public void update(Client client){
	        try (Connection connection = ConnectionGab.getConn();
	             PreparedStatement statement = connection.prepareStatement("UPDATE client SET nom = ?, prenom = ?,adress = ?,cp = ?,ville = ? ,telephone  = ? WHERE idclient = ?")) {
	        	 statement.setString(1, client.getNom());
		            statement.setString(2, client.getPrenom());
		            statement.setString(3, client.getAdress());
		            statement.setInt(4, client.getCp());
		            statement.setString(5, client.getVille());
		            statement.setInt(6, client.getTelephone());
		            statement.setInt(7, client.getIdclient());  
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    public void delete(int idclient) {
	    	PreparedStatement statement = null ;
	    	String sql ="DELETE FROM client WHERE idclient = ?";
	    	
	        try (Connection connection = ConnectionGab.getConn()) {
	        	statement = connection.prepareStatement(sql);
	            statement.setInt(1, idclient);
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    
	    // login utilisateur  
	    // pagination client 
	   
	   
	    // Fonction calculer le longeur 
	    public int longueur() {
	        int length = 0;
	        String sql = "SELECT COUNT(*) AS longueur FROM client ";

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
	    
	    
	    public List<Client> getData(int page, int pageSize,String searchTerm) {
	       // int pageSize = 1; // Nombre d'éléments par page
	        int offset = (page - 1) * pageSize; // Calcul de l'offset pour la pagination
	        List<Client> productList = new ArrayList<Client>();
	        try (Connection conn = ConnectionGab.getConn()) {
	            String sql = "SELECT * FROM client"; // Spécifier les colonnes nécessaires
	            if (searchTerm != null && !searchTerm.isEmpty()) {
	                sql += " WHERE nom LIKE ?"; // Préciser la colonne pour la recherche
	            }
	            sql += " LIMIT ? OFFSET ?";

	            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	                int paramIndex = 1;
	                if (searchTerm != null && !searchTerm.isEmpty()) {
	                    stmt.setString(paramIndex++, "%" + searchTerm + "%");
	                }
	                stmt.setInt(paramIndex++, pageSize);
	                stmt.setInt(paramIndex++, offset);

	                try (ResultSet rs = stmt.executeQuery()) {
	                    while (rs.next()) {
	                    	 Client client = new Client();
	     	                client.setIdclient(rs.getInt("idclient"));
	     	                client.setNom(rs.getString("nom"));
	     	                client.setPrenom(rs.getString("prenom"));
	     	                client.setAdress(rs.getString("adress"));
	     	               
	     	                client.setCp(rs.getInt("cp"));
	     	                client.setVille(rs.getString("ville"));
	     	                client.setTelephone(rs.getInt("telephone"));
	     	                productList.add(client);
	                    }
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return productList;
	    }
	    
	    // longeur de element repeter 
	    public List<Integer> getResultPageNumbers(int pageSize, String searchTerm) {
	        List<Integer> resultPageNumbers = new ArrayList<>();

	        try (Connection conn = ConnectionGab.getConn()) {
	            String sql = "SELECT COUNT(*) AS total FROM client";
	            if (searchTerm != null && !searchTerm.isEmpty()) {
	                sql += " WHERE nom LIKE ?";
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

	                        } System.out.println("le resultat"+resultPageNumbers);
	                    }
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }

	        return resultPageNumbers;
	    }
	    // Importation de pdf 
	    
	    public String generatePDF(List<Client> productList) {
	        String pdfFilePath = "C:/Users/Lenovo/Documents/pdf/output_" + System.currentTimeMillis() + ".pdf";
	        try {
	            Document document = new Document();
	            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
	            document.open();

	            // Créez la table avec 7 colonnes
	            PdfPTable table = new PdfPTable(7);
	            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12); // Police en-tête avec taille 12

	            // Ajoutez les cellules avec les en-têtes
	            addHeaderCell(table, "Idclient",headerFont);
	            addHeaderCell(table, "Nom",headerFont);
	            addHeaderCell(table, "Prenom",headerFont);
	            addHeaderCell(table, "Adress",headerFont);
	            addHeaderCell(table, "Cp",headerFont);
	            addHeaderCell(table, "Ville",headerFont);
	            addHeaderCell(table, "Telephone",headerFont);

	            // Ajoutez les données des comptes à la table
	            for (Client  client : productList) {
	                addDataCell(table, String.valueOf(client.getIdclient()));
	                addDataCell(table,client.getNom());
	                addDataCell(table, client.getPrenom());
	                addDataCell(table, client.getAdress());
	                addDataCell(table, String.valueOf(client.getCp()));
	                addDataCell(table, client.getVille());
	                addDataCell(table, String.valueOf(client.getTelephone()));
	            }

	            // Ajoutez la table au document PDF
	            table.setWidthPercentage(100); // Utilisez la largeur de la page entière
	            table.setWidths(new int[]{2, 4, 2, 2, 2, 2,4}); 
	            document.add(table);
	            document.close();
	        } catch (FileNotFoundException | DocumentException e) {
	            e.printStackTrace();
	        }
	        return pdfFilePath;
	    }

	  
	    // Méthode utilitaire pour ajouter une cellule d'en-tête à la table
     public  void addHeaderCell(PdfPTable table, String header,Font font) {
         PdfPCell cell = new PdfPCell();
         cell.setPadding(8); // Ajustez le rembourrage selon vos besoins
         cell.setPhrase(new Phrase(header));
         ///    cell.setPadding(8); // Set padding here (adjust value as needed)
       
         table.addCell(cell);
     }

     // Méthode utilitaire pour ajouter une cellule de données à la table
     public void addDataCell(PdfPTable table, String data) {
         PdfPCell cell = new PdfPCell();
         cell.setPadding(10); // Ajustez le rembourrage selon vos besoins
         cell.setPhrase(new Phrase(data));
         table.addCell(cell);
     }
  
		// Liste Client par pagination
     
     public List<Client> Pagination(int page, int pageSize) {
         List<Client> productList = new ArrayList<>();
         try (Connection conn = ConnectionGab.getConn()) {
 String sql = "select * from client LIMIT ? OFFSET ?";
             // Ajoutez des paramètres pour la pagination

             try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                 // Définissez les paramètres pour la pagination
                 stmt.setInt(1, pageSize);
                 stmt.setInt(2, (page - 1) * pageSize);

                 try (ResultSet rs = stmt.executeQuery()) {
                     while (rs.next()) {
                    	     Client client = new Client();
	     	                client.setIdclient(rs.getInt("idclient"));
	     	                client.setNom(rs.getString("nom"));
	     	                client.setPrenom(rs.getString("prenom"));
	     	                client.setAdress(rs.getString("adress"));
	     	               
	     	                client.setCp(rs.getInt("cp"));
	     	                client.setVille(rs.getString("ville"));
	     	                client.setTelephone(rs.getInt("telephone"));
	     	                productList.add(client);
                         System.out.println("nom"+rs.getString("nom"));
                     }
                 }
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
         return productList;
     }
     
     // Longeur de element de pagination
     // Longeur 
     
     public int  LongeurElementNom(String search) {
     	int totalRes =0 ;
     	PreparedStatement statement = null ;
     String sql = "SELECT COUNT(*) AS total FROM client c  WHERE c.nom LIKE ?";
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
