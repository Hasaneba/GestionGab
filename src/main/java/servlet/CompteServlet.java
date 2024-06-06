package servlet;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import service.CompteService;
import model.Compte;

@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompteServlet {
	CompteService compteservice = new CompteService();
	
	//Ajouter compte
	@POST
	@Path("AjouterCompte")
	public void Ajouter(Compte compte) {
		compteservice.create(compte);
	}
	// Modifier Compte 
	@PUT
	@Path("ModfierCompte")
	public void ModifierCompte(Compte compte) {
		compteservice.update(compte);
	}
	
	// Lister Compte By Id 
	@GET
	@Path("ListerCompteById")
	public Compte ListerById(@PathParam("idcompte") int idcompte) {
	  return 	compteservice.findById(idcompte);
	}
	
	// Supprimer Compte 
	@DELETE
	@Path("SupprimerCompte/{idcompte}")
	public void   SupprimerClient(@PathParam("idcompte") Long idcompte) {
		 compteservice.delete(idcompte);
	}
	
	// Login compte 
	@GET
	@Path("loginCompte/{id}/{codePin}")
	public Boolean LoginCompte(@PathParam("id") int id,@PathParam("codePin") String codePin) {
		  return compteservice.isValidUser(id, codePin);
	}

}
