package vn.iotstar.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	private String email;
	private String password;
	private String roles;
    public String getRoles() {
		return roles;
	}
    	public void setRoles(String roles) {
		this.roles = roles;
	}
    public String getPassword() {
    	return password;
    }
    public void setPassword(String password) {
		this.password = password;
	}
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
    // Explicit getter/setter for id to ensure availability when Lombok processing is not active
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}