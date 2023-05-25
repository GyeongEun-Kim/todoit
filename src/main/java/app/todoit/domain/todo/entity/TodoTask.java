package app.todoit.domain.todo.entity;

import app.todoit.domain.challenge.entity.Challenge;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "todo_task")
@Getter
@Setter
@NoArgsConstructor
public class TodoTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_task_id")
    private Long taskId;

    @JoinColumn(name = "todo_id")
    @ManyToOne()
    private Todo todo;

    @Column
    private String task;

    @Column
    private Boolean complete;

    @Column
    private Boolean isFromChallenge;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public TodoTask (String task, Todo todo) {
        this.task=task;
        this.complete=false;
        this.isFromChallenge=false;
        this.todo= todo;
    }

    public void setComplete () {
        if(complete) complete=false;
        else complete=true;
    }


}
