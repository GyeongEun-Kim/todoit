package app.todoit.domain.todo.service;

import app.todoit.domain.auth.entity.User;
import app.todoit.domain.todo.dto.TodoResponseDto;
import app.todoit.domain.todo.dto.TodoTaskDto;
import app.todoit.domain.todo.entity.Todo;
import app.todoit.domain.todo.entity.TodoTask;
import app.todoit.domain.todo.exception.TodoException;
import app.todoit.domain.todo.repository.TodoTaskRepository;
import app.todoit.domain.todo.repository.TodoRepository;
import app.todoit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final TodoTaskRepository todoTaskRepository;

    public TodoResponseDto getTodo (User user, String inputDate) {
        LocalDate date = checkDate(inputDate);
        Todo todoEntity = getTodoOfTheDate(user, date);
        List<TodoTask> todoTaskEntity = getTodoTaskEntity(todoEntity);
        List<TodoTaskDto> todoTaskDto = listEntityToListDto(todoTaskEntity);
        return TodoResponseDto.builder()
                .date(date)
                .task(todoTaskDto)
                .build();
    }

    public LocalDate checkDate (String date) {
        if (date!= null) return LocalDate.parse(date);
        else return LocalDate.now();
    }

    public Todo getTodoOfTheDate (User user, LocalDate date) {
        Optional<Todo> todo = todoRepository.findByDateAndUserId(date, user.getId());
        if (!todo.isPresent()) return  todoRepository.save(new Todo(user, date));
        else  return todo.get();
    }

    public List<TodoTask> getTodoTaskEntity (Todo todo) {
        Optional<List<TodoTask>> taskEntity = todoTaskRepository.findAllByTodoTodoId(todo.getTodoId());
        if(taskEntity.isPresent()) return taskEntity.get();
        else return new ArrayList<>();
    }

    public List<TodoTaskDto> listEntityToListDto (List<TodoTask> taskEntity) {
        List<TodoTaskDto> res = new ArrayList<>();
        for (TodoTask t : taskEntity) {
            TodoTaskDto taskDto = TodoTaskDto.builder()
                    .taskId(t.getTaskId())
                    .task(t.getTask())
                    .challenge(t.getChallenge())
                    .isFromChallenge(t.getIsFromChallenge())
                    .complete(t.getComplete())
                    .build();
            res.add(taskDto);
        }
        return res;
    }

    public TodoTaskDto addTask (User user, String task) {
        Todo todo = getTodoOfTheDate(user, LocalDate.now());
        TodoTask save = todoTaskRepository.save(new TodoTask(task, todo));
        return TodoTaskDto.builder()
                .taskId(save.getTaskId())
                .task(save.getTask())
                .challenge(save.getChallenge())
                .isFromChallenge(save.getIsFromChallenge())
                .complete(save.getComplete())
                .build();
    }

    public String deleteTask(User user, Long taskId) {
        checkTaskPresentOrElseThrow(taskId,user);
        todoTaskRepository.deleteById(taskId);
        return "태스크 삭제 성공";
    }

    public TodoTaskDto modifyTask(User user, Long taskId, String newTask) {
        TodoTask todoTask = checkTaskPresentOrElseThrow(taskId, user);
        todoTask.setTask(newTask);
        todoTaskRepository.save(todoTask);
        return TodoTaskDto.builder()
                .taskId(taskId)
                .task(todoTask.getTask())
                .challenge(todoTask.getChallenge())
                .isFromChallenge(todoTask.getIsFromChallenge())
                .complete(todoTask.getIsFromChallenge())
                .build();
    }

    public TodoTaskDto setComplete(User user, Long taskId) {
        TodoTask todoTask = checkTaskPresentOrElseThrow(taskId, user);
        todoTask.setComplete();
        todoTaskRepository.save(todoTask);
        return TodoTaskDto.builder()
                .taskId(taskId)
                .task(todoTask.getTask())
                .challenge(todoTask.getChallenge())
                .isFromChallenge(todoTask.getIsFromChallenge())
                .complete(todoTask.getIsFromChallenge())
                .build();
    }

    public Long getUserIdFromTask (TodoTask task) {
        return task.getTodo().getUser().getId();
    }

    public TodoTask checkTaskPresentOrElseThrow (Long taskId, User user) {
        Optional<TodoTask> task = todoTaskRepository.findById(taskId);
        if (task.isPresent()) {
            Long userIdFromTask = getUserIdFromTask(task.get());
            Long userId = user.getId();
            if (!userIdFromTask.equals(userId)) throw new TodoException(ErrorCode.TODO_UNAUTHORIZED);
            else  return task.get();
        }
        else {
            throw new TodoException(ErrorCode.TASK_NOT_FOUND);
        }
    }

}
