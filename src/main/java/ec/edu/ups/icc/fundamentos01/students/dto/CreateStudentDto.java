package ec.edu.ups.icc.fundamentos01.students.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



public class CreateStudentDto {


    @NotBlank(message="El nombre es obligatorio")
    @Size(min=3,max=100)
    private String name;



    @NotBlank(message="El email es obligatorio")
    @Email(message="Email inválido")
    private String email;



    public String getName(){
        return name;
    }


    public String getEmail(){
        return email;
    }

}