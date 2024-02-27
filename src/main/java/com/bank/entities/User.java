package com.bank.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bank.utlis.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Table(name="bank_user")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class User implements UserDetails {

	@Id
	@TableGenerator(name="id_generator",table="id_gen",pkColumnName = "gen_id",valueColumnName = "gen_value",pkColumnValue = "user_gen",initialValue = 10000,allocationSize = 10)
	@jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.TABLE, generator = "id_generator")
	private Long id;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="other_name")
	private String otherName;
	
	@Column(name="gender")
	private String gender;
	
	@Column(name="address")
	private String address;
	
	@Column(name="state_of_origin")
	private String stateOfOrigin;
	
	@Column(name="account_number")
	private String accountNumber;
	
	@Column(name="account_balance")
	private BigDecimal accountBalance;
	
	@Column(name="email")
	private String email;
	
	@Column(name="user_password")
	private String password;
	
	@Column(name="phone_number")
	private String phoneNumber;
	
	@Column(name="alternative_phone_number")
	private String alternativePhoneNumber;
	
	@Column(name="roles_of_users")
	private Role role;
	
	@Column(name="status")
	private String status;
	
	@Column(name="created_time")
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(name="modified_time")
	@UpdateTimestamp
	private LocalDateTime modifiedAt;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	
}
