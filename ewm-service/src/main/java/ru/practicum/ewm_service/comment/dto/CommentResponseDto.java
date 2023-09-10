package ru.practicum.ewm_service.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import static ru.practicum.ewm_service.util.Constant.DATE_FORMAT;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponseDto {

    Long id;

    @NotBlank
    String text;

    String authorName;

    @JsonProperty("created")
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime created;
}
