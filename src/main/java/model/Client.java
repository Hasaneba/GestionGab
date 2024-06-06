package model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Client  implements Serializable{
	public int getIdclient() {
		return idclient;
	}
	public void setIdclient(int idclient) {
		this.idclient = idclient;
	}
	public Client(int  idclient, String nom, String prenom, String adress, int cp, String ville, int telephone) {
		super();
		this.idclient = idclient;
		this.nom = nom;
		this.prenom = prenom;
		this.adress = adress;
		this.cp = cp;
		this.ville = ville;
		this.telephone = telephone;
	}
	public Client() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public int getCp() {
		return cp;
	}
	public void setCp(int cp) {
		this.cp = cp;
	}
	public String getVille() {
		return ville;
	}
	public void setVille(String ville) {
		this.ville = ville;
	}
	public int getTelephone() {
		return telephone;
	}
	public void setTelephone(int telephone) {
		this.telephone = telephone;
	}
	private int  idclient;
	private  String nom;
	private String prenom ;
	private String  adress;
	private int  cp;
	private String ville ;
	private int telephone;
}
