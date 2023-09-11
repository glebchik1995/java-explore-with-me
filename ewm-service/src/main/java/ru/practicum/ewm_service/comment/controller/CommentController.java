package ru.practicum.ewm_service.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.comment.dto.CommentResponseDto;
import ru.practicum.ewm_service.comment.dto.NewCommentDto;
import ru.practicum.ewm_service.comment.service.CommentService;

import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.ewm_service.util.Constant.DEF_VAL_FROM;
import static ru.practicum.ewm_service.util.Constant.DEF_VAL_SIZE;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments/{userId}/{eventId}")
    public CommentResponseDto createComment(@PathVariable Long userId,
                                            @PathVariable Long eventId,
                                            @RequestBody NewCommentDto commentDto) {
        log.info("POST запрос на создание комментария пользователем с ID = {} к событию с ID = {}", userId, eventId);
        return commentService.createComment(userId, eventId, commentDto);
    }

    @GetMapping("/comments/{eventId}")
    public List<CommentResponseDto> getComments(@PathVariable Long eventId,
                                                @RequestParam(value = "from", defaultValue = DEF_VAL_FROM) @Min(0) Integer from,
                                                @RequestParam(value = "size", defaultValue = DEF_VAL_SIZE) @Min(1) Integer size) {
        log.info("GET запрос на получение всех комментариев события с ID = {}", eventId);
        return commentService.getComments(eventId, from, size);
    }

    @PatchMapping("/comments/{userId}/{commentId}")
    public CommentResponseDto updateComment(@PathVariable Long userId,
                                            @PathVariable Long commentId,
                                            @RequestBody NewCommentDto commentDto) {
        log.info("PATCH запрос на редактирование комментария с ID = {} пользователем с ID = {}", commentId, userId);
        return commentService.updateComment(userId, commentId, commentDto);
    }

    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminDeleteComment(@PathVariable Long commentId) {
        log.info("DELETE запрос на удаление администратором пользовательского комментария с ID = {}", commentId);
        commentService.adminDeleteComment(commentId);
    }

    @DeleteMapping("/public/comments/{userId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void userDeleteComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("DELETE запрос на удаление комментария с ID = {} пользователем с ID = {}", commentId, userId);
        commentService.deleteComment(userId, commentId);
    }

}
