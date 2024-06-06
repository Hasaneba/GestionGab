package servlet;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.Utilisateur;
import service.UtilisateurService;

@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UtilisateurServlet {
      
	
	UtilisateurService service = new UtilisateurService();
	//Ajouter Utilisateur 
	@POST
	@Path("AjouterUtilisateur")
	public void AjouterUtilisateur(Utilisateur utilisateur) {
		  service.create(utilisateur);
	}
	// Modifier Utilisateur 
	@PUT
	@Path("ModifierUtilisateur")
	public void ModifierUtilisateur(Utilisateur utilisateur) {
		service.update(utilisateur);
	}
	// Lister utilisateur par id 
	public Utilisateur ListeById(@PathParam("id") Long id) {
		return service.findById(id);
	}
	
	@GET
	@Path("ListeUtilisateur")
	// Lister Utilisateur 
	public List<Utilisateur>  ListerUtilisateur(){
		return  service.findAll();
	}
	
	// Supprimer Utilisateur 
	@DELETE
	@Path("SupprimerUtilisateur")
	public void  SupprimerUtilisateur(@PathParam("id") Long id) {
		service.delete(id);
	}
}
