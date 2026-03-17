package org.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class VerificationCodeRequest {
    
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String telephone;
    
    @NotBlank(message = "Le code est obligatoire")
    @Size(min = 6, max = 6, message = "Le code doit contenir 6 chiffres")
    @Pattern(regexp = "^[0-9]{6}$", message = "Le code doit contenir uniquement des chiffres")
    private String code;

    public VerificationCodeRequest() {
    }

    public VerificationCodeRequest(String telephone, String code) {
        this.telephone = telephone;
        this.code = code;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
