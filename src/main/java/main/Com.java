package main;

import service.CompteService;

public class Com {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        CompteService compteService  = new CompteService();
        
        // Définir les paramètres de la pagination
        int page = 1;
        int pageSize = 1; // par exemple
        
        // Appeler la méthode Pagination
        System.out.println("Résultat de la pagination : ");
        compteService.Pagination(page,pageSize);
       String search = "epargen";
     // System.out.println(""+compteService.getTotalNumberOfResults(search));
       compteService.LongeurElement(search);
          
	}

}
