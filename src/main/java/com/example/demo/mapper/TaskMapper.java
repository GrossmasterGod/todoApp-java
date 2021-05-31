package com.example.demo.mapper;

import com.example.demo.model.Task;
import com.example.demo.model.dto.TaskDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskDto taskDTO(Task task);

    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "finishDate", ignore = true)
    Task task(TaskDto taskDto);
}

