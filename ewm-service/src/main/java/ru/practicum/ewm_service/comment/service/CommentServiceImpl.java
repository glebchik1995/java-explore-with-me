package ru.practicum.ewm_service.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm_service.comment.dto.CommentResponseDto;
import ru.practicum.ewm_service.comment.dto.NewCommentDto;
import ru.practicum.ewm_service.comment.mapper.CommentMapper;
import ru.practicum.ewm_service.comment.model.Comment;
import ru.practicum.ewm_service.comment.repository.CommentRepository;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.repository.EventRepository;
import ru.practicum.ewm_service.exception.exception.ConflictException;
import ru.practicum.ewm_service.exception.exception.DataNotFoundException;
import ru.practicum.ewm_service.user.model.User;
import ru.practicum.ewm_service.user.repository.UserRepository;
import ru.practicum.ewm_service.util.enums.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentResponseDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {

        User user = checkUser(userId);

        Event event = checkEvent(eventId);

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException(String.format(
                    "Событие с ID = %d нельзя комментировать, т.к. оно еще не опубликовано", eventId));
        }

        Comment comment = CommentMapper.toCommentModel(newCommentDto);
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long eventId, Integer from, Integer size) {
        checkEvent(eventId);
        return commentRepository
                .findAllByEvent_Id(eventId, PageRequest.of(from / size, size))
                .stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto updateComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()
                -> new DataNotFoundException("Комментарий не найден"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException(String.format(
                    "Пользователь с ID = %d не является автором комментария с ID = %d ",
                    userId, commentId));
        }

        comment.setText(newCommentDto.getText());
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    @Override
    public void adminDeleteComment(Long commentId) {
        Comment comment = checkComment(commentId);
        commentRepository.delete(comment);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = checkComment(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException(String.format(
                    "Пользователь с ID = %d не может удалить комментарий с ID = %d, " +
                            "т.к. не является его автором ", userId, commentId));
        }
        commentRepository.delete(comment);
    }

    private User checkUser(Long userId) {
        log.info("Совершаем поиск пользователя с ID = {}", userId);
        return userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException(String.format("Пользователь с ID = %d не найден", userId)));
    }

    private Event checkEvent(Long eventId) {
        log.info("Совершаем поиск события с ID = {}", eventId);
        return eventRepository.findById(eventId).orElseThrow(() ->
                new DataNotFoundException(String.format("Событие с ID = %d не найдено", eventId)));
    }

    private Comment checkComment(Long commentId) {
        log.info("Совершаем поиск комментария с ID = {}", commentId);
        return commentRepository.findById(commentId).orElseThrow(() ->
                new DataNotFoundException(String.format("Комментарий с ID = %d не найден", commentId)));
    }
}
