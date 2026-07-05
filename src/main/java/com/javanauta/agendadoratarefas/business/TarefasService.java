package com.javanauta.agendadoratarefas.business;


import com.javanauta.agendadoratarefas.business.dto.TarefasDTO;
import com.javanauta.agendadoratarefas.business.mapper.TarefasConverter;
import com.javanauta.agendadoratarefas.business.mapper.TarefasUpdateConverter;
import com.javanauta.agendadoratarefas.infrastructure.entity.TarefasEntity;
import com.javanauta.agendadoratarefas.infrastructure.enums.StatusNotificacaoEnum;
import com.javanauta.agendadoratarefas.infrastructure.exceptions.ResourceNotFoundException;
import com.javanauta.agendadoratarefas.infrastructure.repository.TarefasRepository;
import com.javanauta.agendadoratarefas.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TarefasService {

    private final TarefasRepository tarefasRepository;
    private final TarefasConverter tarefasConverter;
    private final JwtUtil jwtUtil;
    private final TarefasUpdateConverter tarefasUpdateConverter;


    public TarefasDTO gravarTarefa(String token, TarefasDTO dto){
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        dto.setDataCriacao(LocalDateTime.now());
        dto.setStatusNotificacaoEnum(StatusNotificacaoEnum.PENDENTE);
        dto.setEmailUsuario(email);
        TarefasEntity entity = tarefasConverter.paraTarefaEntity(dto);

        return tarefasConverter.paraTarefaDTO(
                tarefasRepository.save(entity));
    }


    public List<TarefasDTO> buscaTarefasAgendadasPorPeriodo(LocalDateTime dataInicial, LocalDateTime dataFinal){

        return tarefasConverter.paraListaTarefasDTO(
                tarefasRepository.findByDataEventoBetween(dataInicial, dataFinal));
    }

    public List<TarefasDTO> buscaTarefasPorEmail(String token){
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        List<TarefasEntity> listaTarefas = tarefasRepository.findByEmailUsuario(email);

        return tarefasConverter.paraListaTarefasDTO(listaTarefas);
    }

    public void deletarTarefasPorId(String id) {
        try {
        tarefasRepository.deleteById(id);
    } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(("Erro ao deletar tarefa por id, ide inexistente " + id),
                    e.getCause());
        }
    }

    public TarefasDTO alteraStatus(StatusNotificacaoEnum status, String id) {
        try {
            TarefasEntity entity = tarefasRepository.findById(id).
                    orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada " + id));
            entity.setStatusNotificacaoEnum(status);

            return tarefasConverter.paraTarefaDTO(tarefasRepository.save(entity));
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Erro ao alterar o status da tarefa " + e.getCause());
        }
    }

    public TarefasDTO updateTarefas(TarefasDTO dto, String id){
        try {
            TarefasEntity entity = tarefasRepository.findById(id).
                    orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada " + id));
            tarefasUpdateConverter.updateTarefas(dto, entity);
            return tarefasConverter.paraTarefaDTO(tarefasRepository.save(entity));
            
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Erro ao alterar o status da tarefa " + e.getCause());
        }

    }


}
