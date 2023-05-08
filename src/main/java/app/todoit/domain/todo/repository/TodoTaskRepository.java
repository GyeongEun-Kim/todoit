package app.todoit.domain.todo.repository;

import app.todoit.domain.challenge.entity.Challenge;
import app.todoit.domain.todo.entity.TodoTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TodoTaskRepository extends JpaRepository<TodoTask,Long> {
    //@Query(value = "SELECT k FROM TodoTask k JOIN FETCH k.todoId t WHERE t.date = :date ")
    Optional<List<TodoTask>> findAllByTask ( String task);

    Optional<List<TodoTask>> findAllByTodoTodoId (Long todoId);
    List<TodoTask> findByChallenge(Challenge Challenge);
}
