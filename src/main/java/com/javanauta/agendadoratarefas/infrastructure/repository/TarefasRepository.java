package com.javanauta.agendadoratarefas.infrastructure.repository;

import com.javanauta.agendadoratarefas.infrastructure.entity.TarefasEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TarefasRepository extends MongoRepository<TarefasEntity, String> {
}
