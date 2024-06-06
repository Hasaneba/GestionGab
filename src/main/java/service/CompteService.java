package service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import connection.ConnectionGab;
import model.Compte;

public class CompteService {
	 public void create(Compte compte){
		    
	        try( Connection connection = ConnectionGab.getConn();
	             PreparedStatement statement = connection.prepareStatement("INSERT INTO compte (numeroCompte, typeCompte,codePin,solde,Nombank,idclient) VALUES (?,?,?,?,?,?)")) {
	            statement.setInt(1, compte.getNumeroCompte());
	            statement.setString(2, compte.getTypeCompte());
	            statement.setInt(3, compte.getCodePin());
	            statement.setDouble(4, compte.getSolde());
	            statement.setString(5,compte.getBank());
	            statement.setInt(6,compte.getIdclient());
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public Compte findById(int idcompte){
	        Compte compte = null;
	        try (Connection connection = ConnectionGab.getConn();
	             PreparedStatement statement = connection.prepareStatement("SELECT * FROM client c JOIN compte e ON c.idclient = e.idclient WHERE  idcompte = ?")) {
	            statement.setLong(1, idcompte);
	            ResultSet resultSet = statement.executeQuery();
	            if (resultSet.next()) {
	                compte = new Compte();
	                compte.setIdcompte(resultSet.getInt("idcompte"));
	                compte.setNumeroCompte(resultSet.getInt("numeroCompte"));
	                compte.setTypeCompte(resultSet.getString("typeCompte"));
	                compte.setCodePin(resultSet.getInt("codePin"));
	                compte.setSolde(resultSet.getDouble("solde"));
	                compte.setBank(resultSet.getString("Nombank"));
	                compte.setNom(resultSet.getString("nom"));
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return compte;
	    }

	    public List<Compte> findAll() {
	        List<Compte> productList = new ArrayList<>();
	        try (Connection connection = ConnectionGab.getConn();
	             Statement statement = connection.createStatement()) {
	            ResultSet resultSet = statement.executeQuery("SELECT * FROM compte");
	            while (resultSet.next()) {
	                Compte compte = new Compte();
	                compte.setIdcompte(resultSet.getInt("idcompte"));
	                compte.setNumeroCompte(resultSet.getInt("numeroCompte"));
	                compte.setTypeCompte(resultSet.getString("typeCompte"));
	                compte.setCodePin(resultSet.getInt("codePin"));
	                compte.setSolde(resultSet.getDouble("solde"));
	                compte.setBank(resultSet.getString("Nombank"));
	                productList.add(compte);
	                System.out.println("nomBank"+resultSet.getString("Nombank"));
	               
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return productList;
	    }

	    public void update(Compte compte){
	        try (Connection connection = ConnectionGab.getConn();
	             PreparedStatement statement = connection.prepareStatement("UPDATE compte SET  numeroCompte = ?, typeCompte = ?,codePin = ?,solde = ? ,Nombank=? WHERE idcompte = ?")) {
	        	  statement.setInt(1, compte.getNumeroCompte());
		            statement.setString(2, compte.getTypeCompte());
		            statement.setInt(3, compte.getCodePin());
		            statement.setDouble(4, compte.getSolde());
		            statement.setString(5, compte.getBank());
		            statement.setInt(6, compte.getIdcompte());
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public void delete(Long  idcompte) {
	        try (Connection connection = ConnectionGab.getConn();
	             PreparedStatement statement = connection.prepareStatement("DELETE FROM compte WHERE idcompte = ?")) {
	            statement.setLong(1, idcompte);
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    
	  
	    
	    
	    // Depot de l'argent 
	    public void Depot(int id, double montant) throws IOException {
	        String updateQuery = "UPDATE compte SET solde = solde + ? WHERE idcompte = ?";
	        String insertQuery = "INSERT INTO operation(dateOperation,typeOperation, montant, idcompte) SELECT DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'), 'dépôt', ?, idcompte FROM compte WHERE idcompte = ?";

	        try (Connection conn = ConnectionGab.getConn();
	             PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
	             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

	            // Begin transaction
	            conn.setAutoCommit(false);

	            // Update account balance
	            updateStmt.setDouble(1, montant);
	            updateStmt.setInt(2, id);
	            updateStmt.executeUpdate();

	            // Record the deposit operation including idcompte
	            insertStmt.setDouble(1, montant);
	            insertStmt.setInt(2, id);  // Corrected to set the id parameter for the subquery
	            insertStmt.executeUpdate();

	            // Commit transaction
	            conn.commit();
	            
	            // Appler pour gene pdf 
	            generatePDF( id,montant);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    
	  
	    
	    //  Retrait de l'argent 
	    public void retrait(int id, double montant) throws IOException {
	        String sqlUpdateCompte = "UPDATE compte SET solde = solde - ? WHERE idcompte = ?";
	        String sqlInsertOperation = "INSERT INTO operation (dateOperation, typeOperation, montant, idcompte) VALUES (DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'), 'retrait', ?, ?)";

	        try (Connection conn = ConnectionGab.getConn();
	             PreparedStatement updateCompteStmt = conn.prepareStatement(sqlUpdateCompte);
	             PreparedStatement insertOperationStmt = conn.prepareStatement(sqlInsertOperation)) {

	            // Début de la transaction
	            conn.setAutoCommit(false);

	            // Déduire le montant spécifié du solde du compte
	            updateCompteStmt.setDouble(1, montant);
	            updateCompteStmt.setInt(2, id);
	            updateCompteStmt.executeUpdate();

	            // Enregistrer l'opération de retrait en incluant l'idcompte
	            insertOperationStmt.setDouble(1, montant);
	            insertOperationStmt.setInt(2, id);
	            insertOperationStmt.executeUpdate();

	            // Valider la transaction
	            conn.commit();
	            
	            // Appeller de method  retrait 
	            
	            generatePDFRetrait( id,montant);
	        } catch (SQLException e) {
	            // Annuler la transaction en cas d'exception
	            e.printStackTrace();
	        }
	    }



	   
	   
	  
	    
	   // Transfer de l'argent 
	    
	    
	    public void Transfer(int id, int comptedistinateur, double montant) throws IOException {
	        String sqlDebit = "UPDATE compte SET solde = solde - ? WHERE idcompte = ?";
	        String sqlCredit = "UPDATE compte SET solde = solde + ? WHERE idcompte = ?";
	        String sqlInsertOperation = "INSERT INTO operation(dateOperation, typeOperation, montant, idcompte) VALUES (DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'), 'virement', ?, ?)";

	        try (Connection conn = ConnectionGab.getConn();
	             PreparedStatement debitStmt = conn.prepareStatement(sqlDebit);
	             PreparedStatement creditStmt = conn.prepareStatement(sqlCredit);
	             PreparedStatement insertOperationStmt = conn.prepareStatement(sqlInsertOperation)) {

	            // Begin transaction
	            conn.setAutoCommit(false);

	            // Debit from sender's account
	            debitStmt.setDouble(1, montant);
	            debitStmt.setInt(2, id);
	            debitStmt.executeUpdate();

	            // Credit to receiver's account
	            creditStmt.setDouble(1, montant);
	            creditStmt.setInt(2, comptedistinateur);
	            creditStmt.executeUpdate();

	            // Record the transfer operation including idcompte
	            insertOperationStmt.setDouble(1, montant);
	            insertOperationStmt.setInt(2, id);  // Using sender's idcompte
	            insertOperationStmt.executeUpdate();

	            // Commit transaction
	            conn.commit();
	            // Generate pdf 
	            generatePDFTransfer( id,comptedistinateur,montant);
	        } catch (SQLException e) {
	           
	            e.printStackTrace();
	        }
	    }

	    
	    
	   
	    
	    // Affichage de compte 

	    public Compte AffichageSolde(int id) {
	        Compte  etd1 = null;
	        PreparedStatement sts = null ;
	        String sql = "select solde from compte where idcompte = ?";
	        try(Connection conn = ConnectionGab.getConn()){
	            sts =  conn.prepareStatement(sql);
	            sts.setInt(1,id);
	            ResultSet res  = sts.executeQuery();
	            while(res.next()){  
	                etd1 = new Compte();
	                etd1.setIdcompte(id);
	              //  etd1.setIdcompte(res.getInt("id"));
	                etd1.setSolde(res.getDouble("solde"));
	                System.out.println("solde:"+res.getDouble("solde"));
	            }

	        }
	        catch(SQLException e){
	            e.printStackTrace();
	        }
return etd1;
	    }
	
	    // login  client 
	    
	 // login utilisateur 
	    public boolean isValidUser(int id, String codePin) {
	    try (Connection connection = ConnectionGab.getConn();) {
	        String sql = "SELECT * FROM compte  WHERE idcompte = ? AND codePin = ?";
	        try (PreparedStatement statement = connection.prepareStatement(sql)) {
	            statement.setInt(1,id);
	            statement.setString(2,codePin);
	            try (ResultSet resultSet = statement.executeQuery()) {
	                return resultSet.next();
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	    }
	    
	    
	 // Fonction calculer le longeur 
	    public int longueur() {
	        int length = 0;
	     //   String sql = "SELECT COUNT(*) AS longueur FROM compte ";
	        String sql1 = "SELECT COUNT(*) AS longueur " +
                    "FROM client c, compte e " +
                    "WHERE c.idclient = e.idclient";
	        try (Connection conn =ConnectionGab.getConn();
	             PreparedStatement p = conn.prepareStatement(sql1);
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
	   
	    
	    
	    // barre de recherche  est pagination
	    public List<Compte> getData(int page, int pageSize, String searchTerm) {
	        List<Compte> productList = new ArrayList<>();

	        try (Connection conn = ConnectionGab.getConn()) {
	            String sql = "SELECT e.idcompte, e.numeroCompte, e.typeCompte, e.codePin, e.Nombank, e.solde, c.nom " +
	                         "FROM client c, compte e " +
	                         "WHERE c.idclient = e.idclient";
	            if (searchTerm != null && !searchTerm.isEmpty()) {
	                sql += " AND e.typeCompte LIKE ?";
	            }
	            sql += " ORDER BY e.idcompte"; // Assurez-vous d'avoir une clause ORDER BY pour garantir une pagination cohérente
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
	                        Compte compte = new Compte();
	                        compte.setIdcompte(rs.getInt("idcompte"));
	                        compte.setNumeroCompte(rs.getInt("numeroCompte"));
	                        compte.setTypeCompte(rs.getString("typeCompte"));
	                        compte.setCodePin(rs.getInt("codePin"));
	                        compte.setSolde(rs.getDouble("solde"));
	                        compte.setBank(rs.getString("Nombank"));
	                        compte.setNom(rs.getString("nom"));
	                        productList.add(compte);
	                    }
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return productList;
	    }

	   
	  	
	 
	    
	    
	    public List<Integer> getResultPageNumbers(int pageSize, String searchTerm) {
	        List<Integer> resultPageNumbers = new ArrayList<>();

	        try (Connection conn = ConnectionGab.getConn()) {
	            String sql = "SELECT COUNT(*) AS total FROM client c JOIN compte e ON c.idclient = e.idclient";
	            if (searchTerm != null && !searchTerm.isEmpty()) {
	                sql += " WHERE e.typeCompte LIKE ?";
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



	    
	   
	  


		// Idclient 
	    // id client 
	    public List<Integer> Lister() {

	        List<Integer> list = new ArrayList<Integer>();
	        PreparedStatement statement = null ;
	        String sql = "select idclient from client ";
	        try(Connection conn = ConnectionGab.getConn()){
	            statement=conn.prepareStatement(sql);
	            ResultSet res = statement.executeQuery();
	            while(res.next()){
	                int idclient= res.getInt("idclient");
	                list.add(idclient);
	                System.out.println("List id:"+idclient);

	            }


	        }
	        catch(SQLException e){
	            e.printStackTrace();
	        }
	        return list ;

	    }
	    // Importe pdf 
	    
	    public List<Compte> Data(int page, int pageSize, String searchTerm) throws FileNotFoundException, DocumentException {
	       List<Compte> productList = new ArrayList<>();

	        try (Connection conn = ConnectionGab.getConn()) {
	            String sql = "SELECT e.idcompte, e.numeroCompte, e.typeCompte, e.codePin, e.Nombank, e.solde, c.nom " +
	                         "FROM client c, compte e " +
	                         "WHERE c.idclient = e.idclient";
	            if (searchTerm != null && !searchTerm.isEmpty()) {
	                sql += " AND e.typeCompte LIKE ?";
	            }
	            sql += " ORDER BY e.idcompte"; // Assurez-vous d'avoir une clause ORDER BY pour garantir une pagination cohérente
	            sql += " LIMIT ? OFFSET ?"; // Ajoutez des paramètres pour la pagination

	            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	                int paramIndex = 1;
	                if (searchTerm != null && !searchTerm.isEmpty()) {
	                    stmt.setString(paramIndex++, "%" + searchTerm + "%");
	                }

	                // Définissez les paramètres pour la pagination
	                stmt.setInt(paramIndex++, pageSize);
	                stmt.setInt(paramIndex++, (page - 1) * pageSize);
	                // Importation de pdf 
	                

	                try (ResultSet rs = stmt.executeQuery()) {
	                	
	                	// Importation de pdf 
	                	 String pdfFilePath = "C:/Users/Lenovo/Documents/pdf/output_" + System.currentTimeMillis() + ".pdf";
	                     Document document = new Document();
	                     PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
	                     document.open();
	                     // le nom de champ 

	                     PdfPTable table = new PdfPTable(3); // 3 colonnes pour nom, prénom et ID
	                     table.addCell("Nom");
	                     table.addCell("Prénom");
	                     table.addCell("ID");
	                    while (rs.next()) {
	                        Compte compte = new Compte();
	                        compte.setIdcompte(rs.getInt("idcompte"));
	                        compte.setNumeroCompte(rs.getInt("numeroCompte"));
	                        compte.setTypeCompte(rs.getString("typeCompte"));
	                        compte.setCodePin(rs.getInt("codePin"));
	                        compte.setSolde(rs.getDouble("solde"));
	                        compte.setBank(rs.getString("Nombank"));
	                        compte.setNom(rs.getString("nom"));
	                       
	                        table.addCell(rs.getString("typeCompte"));
	                        table.addCell(rs.getString("Nombank"));
	                        table.addCell(String.valueOf(rs.getInt("idcompte")));
	                        productList.add(compte);
	                    }
	                    document.add(table);
	                    document.close();
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	       return productList;
	    }
	    
	   
	    
	    
	    // Debut 
	    
	    
	    public String generatePDF(List<Compte> comptes) {
	        String pdfFilePath = "C:/Users/Lenovo/Documents/pdf/output_" + System.currentTimeMillis() + ".pdf";
	        try {
	            Document document = new Document();
	            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
	            document.open();

	            // Créez la table avec 6 colonnes
	            PdfPTable table = new PdfPTable(6);
	            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15); // Police en-tête avec taille 12

	            // Ajoutez les cellules avec les en-têtes
	            addHeaderCell(table, "Idcompte",headerFont);
	            addHeaderCell(table, "NumeroCompte",headerFont);
	            addHeaderCell(table, "CodePin",headerFont);
	            addHeaderCell(table, "Solde",headerFont);
	            addHeaderCell(table, "Banque",headerFont);
	            addHeaderCell(table, "Client",headerFont);

	            // Ajoutez les données des comptes à la table
	            for (Compte compte : comptes) {
	                addDataCell(table, String.valueOf(compte.getIdcompte()));
	                addDataCell(table, String.valueOf(compte.getNumeroCompte()));
	                addDataCell(table, String.valueOf(compte.getCodePin()));
	                addDataCell(table, String.valueOf(compte.getSolde()));
	                addDataCell(table, compte.getBank());
	                addDataCell(table, compte.getNom());
	            }

	            // Ajoutez la table au document PDF
	            table.setWidthPercentage(100); // Utilisez la largeur de la page entière
	            table.setWidths(new int[]{4, 6, 4, 4, 4, 5}); 
	            document.add(table);
	            document.close();
	        } catch (FileNotFoundException | DocumentException e) {
	            e.printStackTrace();
	        }
	        return pdfFilePath;
	    }

	        // 
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
	        
	        
	        //  TEST 
	        
	        
	        public String  generatePDF(int id, double montant) throws IOException {
	            String pdfFilePath = "C:/Users/Lenovo/Documents/pdf/output_" + System.currentTimeMillis() + ".pdf";; // Spécifiez le chemin de votre fichier PDF

	            try (PDDocument document = new PDDocument()) {
	                PDPage page = new PDPage(PDRectangle.A4);
	                document.addPage(page);

	                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
	                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
	                    contentStream.beginText();
	                    contentStream.newLineAtOffset(100, 700);
	                    contentStream.showText("Opération: Dépôt");
	                    contentStream.newLineAtOffset(0, -20);
	                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	                    String dateStr = sdf.format(new Date());
	                    contentStream.showText("DateOperation : " + dateStr);
	                    contentStream.newLineAtOffset(0, -20);
	                    contentStream.showText("Numero Compte:" + id);
	                    contentStream.newLineAtOffset(0, -20);
	                    contentStream.showText("Montant: " + montant);
	                    contentStream.endText();
	                }

	                document.save(new FileOutputStream(pdfFilePath));
	            }
	            return pdfFilePath;
	        }
	        
	        // Retrait 
	        
	        
	        public String  generatePDFRetrait(int id, double montant) throws IOException {
	            String pdfFilePath = "C:/Users/Lenovo/Documents/pdf/output_" + System.currentTimeMillis() + ".pdf";; // Spécifiez le chemin de votre fichier PDF

	            try (PDDocument document = new PDDocument()) {
	                PDPage page = new PDPage(PDRectangle.A4);
	                document.addPage(page);

	                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
	                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
	                    contentStream.beginText();
	                    contentStream.newLineAtOffset(100, 700);
	                    contentStream.showText("Opération: Retrait");
	                    contentStream.newLineAtOffset(0, -20);
	                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	                    String dateStr = sdf.format(new Date());
	                    contentStream.showText("DateOperation : " + dateStr);
	                    contentStream.newLineAtOffset(0, -20);
	                    contentStream.showText("Numero Compte:" + id);
	                    contentStream.newLineAtOffset(0, -20);
	                    contentStream.showText("Montant: " + montant);
	                    contentStream.endText();
	                }

	                document.save(new FileOutputStream(pdfFilePath));
	            }
	            return pdfFilePath;
	        }
	        
	        
	        // generate pdf Tranfer     

	        
	        public String  generatePDFTransfer(int id,int comptedistinateur,double montant) throws IOException {
	            String pdfFilePath = "C:/Users/Lenovo/Documents/pdf/output_" + System.currentTimeMillis() + ".pdf";; // Spécifiez le chemin de votre fichier PDF

	            try (PDDocument document = new PDDocument()) {
	                PDPage page = new PDPage(PDRectangle.A4);
	                document.addPage(page);

	                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
	                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
	                    contentStream.beginText();
	                    contentStream.newLineAtOffset(100, 700);
	                    contentStream.showText("Opération: Transfer");
	                    contentStream.newLineAtOffset(0, -20);
	                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	                    String dateStr = sdf.format(new Date());
	                    contentStream.showText("DateOperation : " + dateStr);
	                    contentStream.newLineAtOffset(0, -20);
	                    contentStream.showText("Compte Emetteur:" + id);
	                    contentStream.newLineAtOffset(0, -20);
	                    contentStream.showText("Compte Distinateur:" + comptedistinateur);
	                    contentStream.newLineAtOffset(0, -20);
	                    contentStream.showText("Montant: " + montant);
	                    contentStream.endText();
	                }

	                document.save(new FileOutputStream(pdfFilePath));
	            }
	            return pdfFilePath;
	        }
	        
	        // ListePagination
	        public List<Compte> Pagination(int page, int pageSize) {
	            List<Compte> productList = new ArrayList<>();
	            try (Connection conn = ConnectionGab.getConn()) {
	                String sql = "SELECT e.idcompte, e.numeroCompte, e.typeCompte, e.codePin, e.Nombank, e.solde, c.nom " +
	                             "FROM client c, compte e " +
	                             "WHERE c.idclient = e.idclient";
	                sql += " ORDER BY e.idcompte"; // Assurez-vous d'avoir une clause ORDER BY pour garantir une pagination cohérente
	                sql += " LIMIT ? OFFSET ?"; // Ajoutez des paramètres pour la pagination

	                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	                    // Définissez les paramètres pour la pagination
	                    stmt.setInt(1, pageSize);
	                    stmt.setInt(2, (page - 1) * pageSize);

	                    try (ResultSet rs = stmt.executeQuery()) {
	                        while (rs.next()) {
	                            Compte compte = new Compte();
	                            compte.setIdcompte(rs.getInt("idcompte"));
	                            compte.setNumeroCompte(rs.getInt("numeroCompte"));
	                            compte.setTypeCompte(rs.getString("typeCompte"));
	                            compte.setCodePin(rs.getInt("codePin"));
	                            compte.setSolde(rs.getDouble("solde"));
	                            compte.setBank(rs.getString("Nombank"));
	                            compte.setNom(rs.getString("nom"));
	                            productList.add(compte);
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
	        String sql = "SELECT COUNT(*) AS total FROM client c JOIN compte e ON c.idclient = e.idclient WHERE e.typeCompte LIKE ?";
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
