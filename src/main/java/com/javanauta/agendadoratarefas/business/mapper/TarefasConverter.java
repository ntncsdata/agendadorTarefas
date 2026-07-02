package com.javanauta.agendadoratarefas.business.mapper;


import com.javanauta.agendadoratarefas.business.dto.TarefasDTO;
import com.javanauta.agendadoratarefas.infrastructure.entity.TarefasEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TarefasConverter {


    TarefasEntity paraTarefaEntity(TarefasDTO dto);

    TarefasDTO paraTarefaDTO(TarefasEntity entity);

}
