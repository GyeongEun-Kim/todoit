package app.todoit.domain.todo.dto;

import app.todoit.domain.challenge.entity.Challenge;
import app.todoit.domain.todo.entity.TodoTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class TodoTaskDto {
    private Long taskId;
    private String task;
    private Boolean complete;
    private Boolean isFromChallenge;
    private Challenge challenge;

}
