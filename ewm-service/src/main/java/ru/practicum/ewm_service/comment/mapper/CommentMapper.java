package ru.practicum.ewm_service.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm_service.comment.dto.CommentResponseDto;
import ru.practicum.ewm_service.comment.dto.NewCommentDto;
import ru.practicum.ewm_service.comment.model.Comment;

@UtilityClass
public class CommentMapper {

    public CommentResponseDto toCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public Comment toCommentModel(NewCommentDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .build();
    }
}
