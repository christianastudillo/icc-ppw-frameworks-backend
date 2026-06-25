package ec.edu.ups.icc.fundamentos01.students.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.ups.icc.fundamentos01.students.entity.StudentEntity;



public interface StudentRepository 
extends JpaRepository<StudentEntity,Long>{

}