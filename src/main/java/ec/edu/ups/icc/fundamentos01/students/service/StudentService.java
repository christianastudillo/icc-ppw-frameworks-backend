package ec.edu.ups.icc.fundamentos01.students.service;


import java.util.List;


import org.springframework.stereotype.Service;


import ec.edu.ups.icc.fundamentos01.students.repository.StudentRepository;
import ec.edu.ups.icc.fundamentos01.students.dto.StudentResponseDto;
import ec.edu.ups.icc.fundamentos01.students.mapper.StudentMapper;



@Service
public class StudentService {


private final StudentRepository repository;

private final StudentMapper mapper;



public StudentService(
        StudentRepository repository,
        StudentMapper mapper){

    this.repository=repository;
    this.mapper=mapper;

}



public List<StudentResponseDto> findAll(){


return repository.findAll()
.stream()
.map(mapper::toModel)
.map(mapper::toResponse)
.toList();


}



}