package ec.edu.ups.icc.fundamentos01.students.mapper;



import org.springframework.stereotype.Component;


import ec.edu.ups.icc.fundamentos01.students.dto.*;
import ec.edu.ups.icc.fundamentos01.students.entity.*;
import ec.edu.ups.icc.fundamentos01.students.models.*;



@Component
public class StudentMapper {



public StudentEntity toEntity(StudentModel model){


return new StudentEntity(
        model.getName(),
        model.getEmail()
);


}



public StudentModel toModel(StudentEntity entity){


return new StudentModel(
        entity.getId(),
        entity.getName(),
        entity.getEmail()
);


}



public StudentResponseDto toResponse(StudentModel model){


return new StudentResponseDto(
        model.getId(),
        model.getName(),
        model.getEmail()
);


}


}