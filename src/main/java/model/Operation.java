package model;

import java.io.Serializable;


@SuppressWarnings("serial")
public class Operation implements Serializable {
	private Long idOperation;
	  public Long getIdOperation() {
		return idOperation;
	}
	public void setIdOperation(Long idOperation) {
		this.idOperation = idOperation;
	}
	public Long getIdcompte() { 
		return idcompte;
	}
	public Operation() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Operation(Long idOperation, Long idcompte, String dateOperation, String typeOperation, Double montant) {
		super();
		this.idOperation = idOperation;
		this.idcompte = idcompte;
		this.dateOperation = dateOperation;
		this.typeOperation = typeOperation;
		this.montant = montant;
	}
	public Operation(Long idOperation,  String dateOperation, String typeOperation, Double montant) {
		super();
		this.idOperation = idOperation;
		this.dateOperation = dateOperation;
		this.typeOperation = typeOperation;
		this.montant = montant;
	}
	public void setIdcompte(Long idcompte) {
		this.idcompte = idcompte;
	}
	public String getDateOperation() {
		return dateOperation;
	}
	public void setDateOperation(String dateOperation) {
		this.dateOperation = dateOperation;
	}
	public String getTypeOperation() {
		return typeOperation;
	}
	public void setTypeOperation(String typeOperation) {
		this.typeOperation = typeOperation;
	}
	public Double getMontant() {
		return montant;
	}
	public void setMontant(Double montant) {
		this.montant = montant;
	}
	private Long idcompte; 
	  private String  dateOperation;
	  private String typeOperation;
	  public Operation(Long idOperation, Long idcompte, String dateOperation, String typeOperation, Double montant,
			int numeroCompte, String nom) {
		super();
		this.idOperation = idOperation;
		this.idcompte = idcompte;
		this.dateOperation = dateOperation;
		this.typeOperation = typeOperation;
		this.montant = montant;
		this.numeroCompte = numeroCompte;
		this.nom = nom;
	}
	private Double montant;
	  private int numeroCompte;
	  public int getNumeroCompte() {
		return numeroCompte;
	}
	public void setNumeroCompte(int numeroCompte) {
		this.numeroCompte = numeroCompte;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	private String nom ;
	  
}
