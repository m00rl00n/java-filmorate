package ru.yandex.practicum.filmorate.ControllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserTest {


    @Autowired
    private UserController userController;

    @Test
    void testAddUser() {
        User user = new User(1, "test@1.com", "логин", "имя", LocalDate.of(2013, 9, 1));

        User addedUser = userController.addUser(user);
        assertEquals(user, addedUser);
    }

    @Test
    void testUpdateUser() {
        User user = new User(1, "test@1.com", "логин", "имя!!!", LocalDate.of(2013, 9, 1));
        ;
        User updatedUser = userController.updateUser(user);
        assertEquals(user, updatedUser);
    }

    @Test
    void testGetAllUser() {
        User user1 = new User(1, "test@1.com", "логин", "имя111", LocalDate.of(2013, 9, 1));
        User user2 = new User(1, "test@1.com", "логин", "имя222", LocalDate.of(2013, 9, 1));
        userController.addUser(user1);
        userController.addUser(user2);
        List<User> userList = userController.getAllUser();
        assertEquals(3, userList.size());
        assertTrue(userList.contains(user1));
        assertTrue(userList.contains(user2));
    }

    @Test
    void testEmailValidation() {
        User user = new User(1, "test1.com", "логин", "имя", LocalDate.of(2013, 9, 1));
        assertThrows(ValidationException.class, () -> {
            userController.validateUser(user);
        });
        User user1 = new User(1, null, "логин", "имя", LocalDate.of(2013, 9, 1));
        assertThrows(ValidationException.class, () -> {
            userController.validateUser(user1);
        });
    }

    @Test
    void testLoginValidation() {
        User user = new User(1, "test@1.com", " vv  v", "имя", LocalDate.of(2013, 9, 1));
        assertThrows(ValidationException.class, () -> {
            userController.validateUser(user);
        });
        User user2 = new User(1, "test@1.com", null, "имя", LocalDate.of(2013, 9, 1));
        assertThrows(ValidationException.class, () -> {
            userController.validateUser(user2);
        });
    }

    @Test
    void testNameValidation() {
        User user = new User(1, "test@1.com", "логин", "", LocalDate.of(2013, 9, 1));
        userController.validateUser(user);
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void testBirthday() {
        User user = new User(1, "test@1.com", "логин", "", LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> {
            userController.validateUser(user);
        });
    }
}


