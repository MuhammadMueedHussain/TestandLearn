package com.learn.app.demo.request;


import com.learn.app.demo.enums.Gender;
import jakarta.validation.constraints.*;

public class SignUpRequest {
    @NotBlank @Size(max=100) private String firstName;
    @NotBlank @Size(max=100) private String lastName;
    @NotBlank @Email @Size(max=150) private String email;
    @NotBlank @Size(min=8, max=72) private String password;
    @NotBlank @Size(min=8, max=20) private String phone;
    @NotNull private Gender gender;

    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { firstName = v; }

    public String getLastName() { return lastName; }
    public void setLastName(String v) { lastName = v; }

    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }

    public String getPassword() { return password; }
    public void setPassword(String v) { password = v; }

    public String getPhone() { return phone; }
    public void setPhone(String v) { phone = v; }

    public Gender getGender() { return gender; }
    public void setGender(Gender v) { gender = v; }
}

