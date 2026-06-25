package ec.edu.ups.icc.fundamentos01.students.entity;


import jakarta.persistence.*;


@Entity
@Table(name="students")
public class StudentEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable=false,length=100)
    private String name;


    @Column(nullable=false,unique=true)
    private String email;



    public StudentEntity(){}



    public StudentEntity(String name,String email){

        this.name=name;
        this.email=email;

    }



    public Long getId(){
        return id;
    }


    public String getName(){
        return name;
    }


    public String getEmail(){
        return email;
    }


}