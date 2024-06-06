package service;

import java.io.InputStream;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Client;
import model.Compte;

@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Test {
	// Creation de objet de client 
	ClientService t = new ClientService();
	UtilisateurService utilisateur = new UtilisateurService();
	CompteService compteservice = new CompteService();
	// Method pour Liste Client
	@GET
	@Path("bien")
	//@Produces(MediaType.APPLICATION_JSON)
	public List<Client> bien(){
		  return  t.findAll(); 
	}
	// Ajouter de client 
	@POST
	@Path("ajouterClient")
	//@Produces(MediaType.APPLICATION_JSON)
	public void Ajouter(Client client) {
		t.create(client);
	}
	// Liste client par id 
	@GET 
	@Path("ListeIdClient/{idclient}")
	//@Produces(MediaType.APPLICATION_JSON)
	public Client ListerId(@PathParam("idclient") int  idclient) {
		
		return t.findById(idclient);
	}
	
	// Supprimer client par id 
	@DELETE
	@Path("Supprimer/{idclient}")
	//@Produces(MediaType.APPLICATION_JSON)
	public void SupprimerClien(@PathParam("idclient") int idclient) {
		   t.delete(idclient);
	}
	
	
	// Modification de client 
	@PUT
	@Path("ModifierClient")
	//@Produces(MediaType.APPLICATION_JSON)
	public  void ModifierClient(Client client) {
		t.update(client);
	}
	@GET
	@Path("loginCompte/{id}/{codePin}")
	public Boolean LoginCompte(@PathParam("id") int id,@PathParam("codePin") String codePin) {
		  return compteservice.isValidUser(id, codePin);
	}
	// Consulter Compte 
	@GET
	@Path("ConsulterCompte/{id}")
	public Compte ConsulterCompte(@PathParam("id") int id) {
		return compteservice.AffichageSolde(id);

	}
	//depot d'argent
	@POST
	@Path("DepotArgent/{id}/{montant}")
	public  void  DepotArgent(@PathParam("id") int id,@PathParam("montant") double montant) throws IOException {
		compteservice.Depot(id, montant);
	}
	// Retrait l'argent 
	@POST
	@Path("RetraitArgent/{id}/{montant}")
	public void RetraitArgent(@PathParam("id") int id,@PathParam("montant") double montant) throws IOException {
		compteservice.retrait(id, montant);
	}
	// Transfer l'argent 
	@POST
	@Path("TransferArgent/{id}/{comptedistinateur}/{montant}")
	public void TranferArgent(@PathParam("id") int id,@PathParam("comptedistinateur") int comptedistinateur,@PathParam("montant") double montant) throws IOException {
		compteservice.Transfer(id, comptedistinateur, montant);
	}
	// Historique de operation 
	//  importer pdf 
	@GET
	@Path("generatePdf/{id}/{montant}")
	public String ExporterPdf(@PathParam("id") int id,@PathParam("montant") double montant) throws IOException {
		return compteservice.generatePDFRetrait(id, montant);
	}
	
	
 
	@GET
    @Path("generatePdfbien/{id}/{montant}")
    @Produces("application/pdf")
    public Response ExporterPdfTest(@PathParam("id") int id, @PathParam("montant") double montant) throws IOException {
        // Appel à la méthode de génération de PDF
        String pdfFilePath = compteservice.generatePDFRetrait(id, montant);
        
        // Construire une réponse avec le PDF en tant que flux de données
        File file = new File(pdfFilePath);
        InputStream fileStream = new FileInputStream(file);
        Response.ResponseBuilder response = Response.ok(fileStream);
        response.header("Content-Disposition", "attachment; filename=document.pdf");
        
        // Assurez-vous de fermer le flux de fichier une fois que vous avez terminé avec lui
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        return response.build();
    }
	// Expoter pdf Tranfer 
	@GET
    @Path("Pdf/Tranfer/generatePdfTranfer/{id}/{comptedistinateur}/{montant}")
    @Produces("application/pdf")
    public Response ExporterPdfTranfer(@PathParam("id") int id,@PathParam("comptedistinateur") int comptedistinateur, @PathParam("montant") double montant) throws IOException {
        // Appel à la méthode de génération de PDF
        String pdfFilePath = compteservice.generatePDFTransfer(id,comptedistinateur,montant);
        
        // Construire une réponse avec le PDF en tant que flux de données
        File file = new File(pdfFilePath);
        InputStream fileStream = new FileInputStream(file);
        Response.ResponseBuilder response = Response.ok(fileStream);
        response.header("Content-Disposition", "attachment; filename=document.pdf");
        
        // Assurez-vous de fermer le flux de fichier une fois que vous avez terminé avec lui
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        return response.build();
    }
	//Expoter pdf Depot 
	@GET
    @Path("pdf/generatePdfDepot/{id}/{montant}")
    @Produces("application/pdf")
    public Response ExporterPdfDepot(@PathParam("id") int id,@PathParam("montant") double montant) throws IOException {
        // Appel à la méthode de génération de PDF
        String pdfFilePath = compteservice.generatePDF(id, montant);
        
        // Construire une réponse avec le PDF en tant que flux de données
        File file = new File(pdfFilePath);
        InputStream fileStream = new FileInputStream(file);
        Response.ResponseBuilder response = Response.ok(fileStream);
        response.header("Content-Disposition", "attachment; filename=document.pdf");
        
        // Assurez-vous de fermer le flux de fichier une fois que vous avez terminé avec lui
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        return response.build();
    }
	// Pagination est Barre de recherche 
	@GET
	@Path("pagination")
	public List<Compte> ListePagination(@QueryParam("page") int page,@QueryParam("pageSize") int pageSize,@QueryParam("searchTerm") String searchTerm){
		return  compteservice.getData(page,pageSize, searchTerm);
	}
	@GET
	@Path("nombrePage")
	public List<Integer> NombrePage(@QueryParam("pageSize") int pageSize,@QueryParam("searchTerm") String searchTerm){
		return compteservice.getResultPageNumbers(pageSize, searchTerm);
	}
	
	@GET
	@Path("LongeurCompte")
	public int LongeurCompte() {
		return compteservice.longueur();
	}
	
	@GET
	@Path("page/Pagination")
	public List<Compte> Paginationbien(@QueryParam("page") int page,@QueryParam("pageSize") int pageSize){
		return compteservice.Pagination(page, pageSize);
	}

	// Longeur de element pagination 
	@GET
	@Path("Longeur/search")
	public int LongeurElement(@QueryParam("search") String search) {
		return compteservice.LongeurElement(search);
	}
	// Login Utilisateur  espace admin 
	@GET 
	@Path("LoginUtilisateur/Login/{username}/{password}")
	public boolean LoginUtilisateur(@PathParam("username")String username,@PathParam("password") String password) {
		 return utilisateur.LoginUtilisateur(username,password);
	}
	
	
}