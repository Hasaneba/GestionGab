package model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Compte implements Serializable {
	private int idcompte;
	public int getIdcompte() {
		return idcompte;
	}
	public Compte() {
		super();
		// TODO Auto-generated constructor stub
	}
	public void setIdcompte(int idcompte) {
		this.idcompte = idcompte;
	}
	public int getIdclient() {
		return idclient;
	}
	public Compte(int idcompte, int idclient, int numeroCompte, String typeCompte, int codePin, Double solde) {
		super();
		this.idcompte = idcompte;
		this.idclient = idclient;
		this.numeroCompte = numeroCompte;
		this.typeCompte = typeCompte;
		this.codePin = codePin;
		this.solde = solde;
	}
	public void setIdclient(int idclient) {
		this.idclient = idclient;
	}
	public int getNumeroCompte() {
		return numeroCompte;
	}
	public void setNumeroCompte(int numeroCompte) {
		this.numeroCompte = numeroCompte;
	}
	public String getTypeCompte() {
		return typeCompte;
	}
	public void setTypeCompte(String typeCompte) {
		this.typeCompte = typeCompte;
	}
	public int getCodePin() {
		return codePin;
	}
	public void setCodePin(int codePin) {
		this.codePin = codePin;
	}
	public Double getSolde() {
		return solde;
	}
	public Compte(int idcompte, int idclient, int numeroCompte, String typeCompte, int codePin, Double solde,
			String bank) { 
		super();
		this.idcompte = idcompte;
		this.idclient = idclient;
		this.numeroCompte = numeroCompte;
		this.typeCompte = typeCompte;
		this.codePin = codePin;
		this.solde = solde;
		Bank = bank;
	}
	public void setSolde(Double solde) {
		this.solde = solde;
	}
	private int idclient;
	private int numeroCompte;
	private String  typeCompte;
	private int codePin;
	private Double solde;
	private String Bank ;
	private String nom ;
	public Compte(int idcompte, int idclient, int numeroCompte, String typeCompte, int codePin, Double solde,
			String bank, String nom) {
		super();
		this.idcompte = idcompte;
		this.idclient = idclient;
		this.numeroCompte = numeroCompte;
		this.typeCompte = typeCompte;
		this.codePin = codePin;
		this.solde = solde;
		Bank = bank;
		this.nom = nom;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getBank() {
		return Bank;
	}
	public void setBank(String bank) {
		Bank = bank;
	}

}
