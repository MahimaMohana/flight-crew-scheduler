package com.skylink.crewscheduler.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * A pilot or flight attendant who can be assigned to trips. Also doubles as
 * the login identity for the crew portal (username/password), which keeps
 * this demo self-contained without a separate user/identity table.
 */
@Entity
@Table(name = "crew_members")
public class CrewMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String employeeId;

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** BCrypt-encoded password. Never stored or returned in plain text. */
    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String lastName;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CrewRole crewRole;

    @NotBlank
    @Column(nullable = false, length = 10)
    private String baseAirport;

    private Integer seniorityNumber;

    public CrewMember() {
    }

    public CrewMember(String employeeId, String username, String password, String firstName,
                       String lastName, String email, CrewRole crewRole, String baseAirport,
                       Integer seniorityNumber) {
        this.employeeId = employeeId;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.crewRole = crewRole;
        this.baseAirport = baseAirport;
        this.seniorityNumber = seniorityNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CrewRole getCrewRole() {
        return crewRole;
    }

    public void setCrewRole(CrewRole crewRole) {
        this.crewRole = crewRole;
    }

    public String getBaseAirport() {
        return baseAirport;
    }

    public void setBaseAirport(String baseAirport) {
        this.baseAirport = baseAirport;
    }

    public Integer getSeniorityNumber() {
        return seniorityNumber;
    }

    public void setSeniorityNumber(Integer seniorityNumber) {
        this.seniorityNumber = seniorityNumber;
    }
}
