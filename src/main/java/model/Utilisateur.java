package model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Utilisateur  implements Serializable{

	private Long id ;

	private String username;
	  private String email;
	  public String getUsername() {
		return username;
	}
	public Long getId() {
		return id;
	}
	public Utilisateur(Long id, String username, String email, String password, String role) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Utilisateur() {
		super();
		// TODO Auto-generated constructor stub
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	private String password;
	  private String role ;  
}
