package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import javax.validation.ValidationException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(final NotFoundException e) {
        log.info("Айди не найден, проверьте его правильность");
        return new ResponseEntity<>(Map.of("Айди не найден", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidation(final ValidationException e) {
        log.info("Ошибка. Валидация не пройдена");
        return new ResponseEntity<>(Map.of("Ошибка валидации", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}

