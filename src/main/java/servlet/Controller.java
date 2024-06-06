package servlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

//@Path("/api")
public class Controller {
	
	
	@GET
	@Path("/data")
	@Produces(MediaType.APPLICATION_JSON)
	public void Test() {
		  System.out.println("hello world");
	}
	

}
