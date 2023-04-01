package app.todoit.domain.todo.service;

import app.todoit.domain.auth.entity.User;
import app.todoit.domain.todo.dto.GetTodoResponseDto;
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

    public GetTodoResponseDto getTodayTodo (User user, LocalDate date) {
        Optional<Todo> todo = todoRepository.findByDateAndUserId(date, user.getId());
        Todo present;
        if (!todo.isPresent()) {
            //새 투두 생성
            present = todoRepository.save(new Todo(user, date));
        }
        else { present= todo.get();}

        Optional<List<TodoTask>> taskEntity = todoTaskRepository.findAllByTodoTodoId(present.getTodoId());
        GetTodoResponseDto res = new GetTodoResponseDto(date);

        if (taskEntity.isPresent()) {
            List<TodoTaskDto> taskDto = listEntityToListDto(taskEntity.get());
            res = GetTodoResponseDto.builder()
                    .date(date)
                    .task(taskDto)
                    .build();
        }

        return res;
    }

    public List<TodoTaskDto> listEntityToListDto (List<TodoTask> entity) {
        List<TodoTaskDto> res = new ArrayList<>();
        for (TodoTask t : entity) {
            TodoTaskDto taskDto = new TodoTaskDto().toDto(t);
            res.add(taskDto);
        }
        return res;
    }

    public TodoTaskDto addTask (User user, String task) {
        Todo todo = todoRepository.findByDateAndUserId(LocalDate.now(), user.getId()).get();
        TodoTask save = todoTaskRepository.save(new TodoTask(task, todo));
        return new TodoTaskDto().toDto(save);
    }

    public String deleteTask(Long taskId) {
        // TODO: 2023/03/28 cascade처리해야함
        // TODO: 2023/03/30  내 투두번호가 아니면 삭제 unauthorized 뱉기
        if (!todoTaskRepository.findById(taskId).isPresent()) throw new TodoException(ErrorCode.TASK_NOT_FOUND);
        else todoTaskRepository.deleteById(taskId);
        return "DElETE SUCCESS";
    }

    public TodoTaskDto modifyTask(Long taskId, String newTask) {
        // TODO: 2023/03/30  내 투두번호가 아니면 삭제 unauthorized 뱉기
        Optional<TodoTask> task = todoTaskRepository.findById(taskId);
        if (task.isPresent()) {
            task.get().setTask(newTask);
            TodoTask save = todoTaskRepository.save(task.get());
            return new TodoTaskDto().toDto(save);
        }
        else {
            throw new TodoException(ErrorCode.TASK_NOT_FOUND);
        }
    }

    public TodoTaskDto setComplete(Long taskId) {
        // TODO: 2023/03/30  내 투두번호가 아니면 삭제 unauthorized 뱉기
        Optional<TodoTask> task = todoTaskRepository.findById(taskId);
        if (task.isPresent()) {
            task.get().setComplete();
            TodoTask save = todoTaskRepository.save(task.get());
            return new TodoTaskDto().toDto(save);
        }
        else {
            throw new TodoException(ErrorCode.TASK_NOT_FOUND);
        }

    }

}
