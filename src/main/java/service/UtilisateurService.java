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
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import connection.ConnectionGab;
import model.Utilisateur;

public class UtilisateurService {

	
	
	 public void create(Utilisateur utilisateur){
		    
	        try( Connection connection = ConnectionGab.getConn();
	            PreparedStatement statement = connection.prepareStatement("INSERT INTO utilisateur (username,Email,password,Role) VALUES (?,?,?,?)")) {
	            statement.setString(1, utilisateur.getUsername());
	            statement.setString(2, utilisateur.getEmail());
	            statement.setString(3, utilisateur.getPassword());
	            statement.setString(4, utilisateur.getRole());
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public Utilisateur findById(Long id){
	    	Utilisateur utilisateur = null;
	        try (Connection connection = ConnectionGab.getConn();
	             PreparedStatement statement = connection.prepareStatement("SELECT ID, username,password,Email,Role FROM utilisateur WHERE ID = ?")) {
	            statement.setLong(1, id);
	            ResultSet resultSet = statement.executeQuery();
	            if (resultSet.next()) {
	                utilisateur = new Utilisateur();
	                utilisateur.setId(resultSet.getLong("ID"));
	                utilisateur.setUsername(resultSet.getString("username"));
	                utilisateur.setPassword(resultSet.getString("password"));
	                utilisateur.setEmail(resultSet.getString("Email"));
	                utilisateur.setRole(resultSet.getString("Role")); 
	               }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return utilisateur;
	    }

	    public List<Utilisateur> findAll() {
	        List<Utilisateur> productList = new ArrayList<>();
	        try (Connection connection = ConnectionGab.getConn();
	             Statement statement = connection.createStatement()) {
	            ResultSet resultSet = statement.executeQuery("SELECT * FROM utilisateur");
	            while (resultSet.next()) {
	            	Utilisateur  utilisateur = new Utilisateur(); 
		                utilisateur.setId(resultSet.getLong("ID"));
		                utilisateur.setUsername(resultSet.getString("username"));
		                utilisateur.setEmail(resultSet.getString("Email"));
		                utilisateur.setPassword(resultSet.getString("password"));
		                utilisateur.setRole(resultSet.getString("Role"));
	                productList.add(utilisateur);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return productList;
	    }

	    public void update(Utilisateur utilisateur){
	        try (Connection connection = ConnectionGab.getConn();
	             PreparedStatement statement = connection.prepareStatement("UPDATE utilisateur SET username = ?, password = ?,Email = ?,Role = ? WHERE ID = ?")) {
	        	
	        	statement.setString(1, utilisateur.getUsername());
	            statement.setString(2, utilisateur.getPassword());
	            statement.setString(3, utilisateur.getEmail());
	            statement.setString(4, utilisateur.getRole());
	            statement.setLong(5, utilisateur.getId());
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public void delete(Long  ID) {
	        try (Connection connection = ConnectionGab.getConn();
	             PreparedStatement statement = connection.prepareStatement("DELETE FROM utilisateur WHERE ID = ?")) {
	            statement.setLong(1, ID);
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    
	    
	    
	    // Fonction calculer le longeur 
	    public int longueur() {
	        int length = 0;
	        String sql = "SELECT COUNT(*) AS longueur FROM utilisateur ";

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
	    
	    
	    public List<Utilisateur> getData(int page, int pageSize,String searchTerm) {
	       // int pageSize = 1; // Nombre d'éléments par page
	        int offset = (page - 1) * pageSize; // Calcul de l'offset pour la pagination
	        List<Utilisateur> productList = new ArrayList<Utilisateur>();
	        try (Connection conn = ConnectionGab.getConn()) {
	            String sql = "SELECT * FROM utilisateur"; // Spécifier les colonnes nécessaires
	            if (searchTerm != null && !searchTerm.isEmpty()) {
	                sql += " WHERE username LIKE ?"; // Préciser la colonne pour la recherche
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
	                    	Utilisateur  utilisateur = new Utilisateur(); 
			                utilisateur.setId(rs.getLong("ID"));
			                utilisateur.setUsername(rs.getString("username"));
			                utilisateur.setEmail(rs.getString("Email"));
			                utilisateur.setPassword(rs.getString("password"));
			                utilisateur.setRole(rs.getString("Role"));
		                productList.add(utilisateur);
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
	            String sql = "SELECT COUNT(*) AS total FROM utilisateur";
	            if (searchTerm != null && !searchTerm.isEmpty()) {
	                sql += " WHERE username LIKE ?";
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

	    
	    public String generatePDF(List<Utilisateur> productList){
	        String pdfFilePath = "C:/Users/Lenovo/Documents/pdf/output_" + System.currentTimeMillis() + ".pdf";
	        try {
	            Document document = new Document();
	            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
	            document.open();

	            // Créez la table avec 6 colonnes
	            PdfPTable table = new PdfPTable(5);
	            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15); // Police en-tête avec taille 12

	            // Ajoutez les cellules avec les en-têtes
	            addHeaderCell(table, "IdUser",headerFont);
	            addHeaderCell(table, "Username",headerFont);
	            addHeaderCell(table, "Email",headerFont);
	            addHeaderCell(table, "Password",headerFont);
	            addHeaderCell(table, "Role",headerFont);

	            // Ajoutez les données des comptes à la table
	            for (Utilisateur  utilisateur : productList) {
	                addDataCell(table, String.valueOf(utilisateur.getId()));
	                addDataCell(table, utilisateur.getUsername());
	                addDataCell(table,  utilisateur.getEmail());
	                addDataCell(table,  utilisateur.getPassword());
	                addDataCell(table,  utilisateur.getRole());
	            }

	            // Ajoutez la table au document PDF
	            table.setWidthPercentage(100); // Utilisez la largeur de la page entière
	            table.setWidths(new int[]{2, 2, 4, 2, 2}); 
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
	    // Login Utilisateur
	    public boolean LoginUtilisateur(String username, String password) {
	        try (Connection connection = ConnectionGab.getConn();) {
	            String sql = "SELECT * FROM utilisateur WHERE username = ? AND password = ?";
	            try (PreparedStatement statement = connection.prepareStatement(sql)) {
	                statement.setString(1, username);
	                statement.setString(2, password);
	                try (ResultSet resultSet = statement.executeQuery()) {
	                    return resultSet.next();
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	           
	        }
	        return false;
	        }
	    
	    
	    
	    
	    
	    
	    public List<Utilisateur> Pagination(int page, int pageSize) {
	         List<Utilisateur> productList = new ArrayList<>();
	         try (Connection conn = ConnectionGab.getConn()) {
	 String sql = "select * from utilisateur LIMIT ? OFFSET ?";
	             // Ajoutez des paramètres pour la pagination

	             try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	                 // Définissez les paramètres pour la pagination
	                 stmt.setInt(1, pageSize);
	                 stmt.setInt(2, (page - 1) * pageSize);

	                 try (ResultSet resultSet = stmt.executeQuery()) {
	                     while (resultSet.next()) {
	                    		Utilisateur  utilisateur = new Utilisateur(); 
	    		                utilisateur.setId(resultSet.getLong("ID"));
	    		                utilisateur.setUsername(resultSet.getString("username"));
	    		                utilisateur.setEmail(resultSet.getString("Email"));
	    		                utilisateur.setPassword(resultSet.getString("password"));
	    		                utilisateur.setRole(resultSet.getString("Role"));
	    	                productList.add(utilisateur);
	                         System.out.println("username"+resultSet.getString("username"));
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
	     String sql = "SELECT COUNT(*) AS total FROM utilisateur  WHERE username LIKE ?";
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
