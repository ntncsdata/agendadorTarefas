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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private Authentication auth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isAdmin() {
        return auth().getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    private TarefasEntity buscaTarefaAutorizada(String id) {
        TarefasEntity entity = tarefasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada: " + id));

        if (!isAdmin() && !entity.getEmailUsuario().equals(auth().getName())) {
            throw new AccessDeniedException("Tarefa não pertence ao usuário autenticado");
        }
        return entity;
    }

    public TarefasDTO gravarTarefa(String token, TarefasDTO dto) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        dto.setId(null);
        dto.setDataCriacao(LocalDateTime.now());
        dto.setStatusNotificacaoEnum(StatusNotificacaoEnum.PENDENTE);
        dto.setEmailUsuario(email);
        TarefasEntity entity = tarefasConverter.paraTarefaEntity(dto);
        return tarefasConverter.paraTarefaDTO(tarefasRepository.save(entity));
    }

    public List<TarefasDTO> buscaTarefasAgendadasPorPeriodo(LocalDateTime dataInicial,
                                                            LocalDateTime dataFinal) {
        return tarefasConverter.paraListaTarefasDTO(
                tarefasRepository.findByDataEventoBetweenAndStatusNotificacaoEnum(
                        dataInicial,
                        dataFinal));
    }

    public List<TarefasDTO> buscaTarefasPorEmail(String token) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        return tarefasConverter.paraListaTarefasDTO(
                tarefasRepository.findByEmailUsuario(email));
    }

    public void deletarTarefasPorId(String id) {
        TarefasEntity entity = buscaTarefaAutorizada(id);
        tarefasRepository.delete(entity);
    }

    public TarefasDTO alteraStatus(StatusNotificacaoEnum status, String id) {
        TarefasEntity entity = buscaTarefaAutorizada(id);
        entity.setStatusNotificacaoEnum(status);
        entity.setDataAlteracao(LocalDateTime.now());
        return tarefasConverter.paraTarefaDTO(tarefasRepository.save(entity));
    }

    public TarefasDTO updateTarefas(TarefasDTO dto, String id) {
        TarefasEntity entity = buscaTarefaAutorizada(id);
        dto.setEmailUsuario(null);
        dto.setId(null);
        tarefasUpdateConverter.updateTarefas(dto, entity);
        entity.setDataAlteracao(LocalDateTime.now());
        return tarefasConverter.paraTarefaDTO(tarefasRepository.save(entity));
    }
}

