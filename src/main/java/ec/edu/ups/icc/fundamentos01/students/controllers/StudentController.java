package ec.edu.ups.icc.fundamentos01.students.controllers;

import java.util.List;


import org.springframework.web.bind.annotation.*;


import ec.edu.ups.icc.fundamentos01.students.dto.StudentResponseDto;
import ec.edu.ups.icc.fundamentos01.students.service.StudentService;



@RestController
@RequestMapping("/students")
public class StudentController {



private final StudentService service;



public StudentController(StudentService service){

    this.service=service;

}



@GetMapping
public List<StudentResponseDto> findAll(){

    return service.findAll();

}



}