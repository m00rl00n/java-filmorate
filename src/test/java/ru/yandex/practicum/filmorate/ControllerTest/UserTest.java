package ru.yandex.practicum.filmorate.ControllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserTest {


    @Autowired
    private UserController userController;

    @Test
    void testAddUser() {
        User user = new User(1, "test@1.com", "логин", "имя", LocalDate.of(2013, 9, 1),new HashSet<>());

        User addedUser = userController.addUser(user);
        assertEquals(user, addedUser);
    }

    @Test
    void testUpdateUser() {
        User user = new User(1, "test@1.com", "логин", "имя!!!", LocalDate.of(2013, 9, 1),new HashSet<>());
        ;
        User updatedUser = userController.updateUser(user);
        assertEquals(user, updatedUser);
    }

    @Test
    void testGetAllUser() {
        User user1 = new User(1, "test@1.com", "логин", "имя111", LocalDate.of(2013, 9, 1),new HashSet<>());
        User user2 = new User(1, "test@1.com", "логин", "имя222", LocalDate.of(2013, 9, 1),new HashSet<>());
        userController.addUser(user1);
        userController.addUser(user2);
        List<User> userList = userController.getAllUsers();
        assertEquals(4, userList.size());
        assertTrue(userList.contains(user1));
        assertTrue(userList.contains(user2));
    }

    @Test
    void testEmailValidation() {
        User user = new User(1, "test1.com", "логин", "имя", LocalDate.of(2013, 9, 1),new HashSet<>());
        assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        User user1 = new User(1, null, "логин", "имя", LocalDate.of(2013, 9, 1),new HashSet<>());
        assertThrows(ValidationException.class, () -> {
            userController.addUser(user1);
        });
    }

    @Test
    void testLoginValidation() {
        User user = new User(1, "test@1.com", " vv  v", "имя", LocalDate.of(2013, 9, 1) ,new HashSet<>());
        assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        User user2 = new User(1, "test@1.com", null, "имя", LocalDate.of(2013, 9, 1),new HashSet<>());
        assertThrows(ValidationException.class, () -> {
            userController.addUser(user2);
        });
    }

    @Test
    void testNameValidation() {
        User user = new User(1, "test@1.com", "логин", "", LocalDate.of(2013, 9, 1),new HashSet<>());
        userController.addUser(user);
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void testBirthday() {
        User user = new User(1, "test@1.com", "логин", "", LocalDate.now().plusDays(1),new HashSet<>());
        assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
    }
    @Test
    void testAddFriend() {
        User user1 = new User(1, "test@1.com", "логин1", "имя1", LocalDate.of(2013, 9, 1),new HashSet<>());
        User user2 = new User(2, "test@2.com", "логин2", "имя2", LocalDate.of(2013, 9, 2),new HashSet<>());
        userController.addUser(user1);
        userController.addUser(user2);
        userController.addFriend(user1.getId(), user2.getId());
        List<Integer> friends = userController.getFriends(user1.getId());
        assertEquals(1, friends.size());
        assertEquals(user2.getId(), friends.get(0));
    }

    @Test
    void testRemoveFriend() {
        User user1 = new User(1, "test@1.com", "логин1", "имя1", LocalDate.of(2013, 9, 1),new HashSet<>());
        User user2 = new User(2, "test@2.com", "логin2", "имя2", LocalDate.of(2013, 9, 2),new HashSet<>());
        userController.addUser(user1);
        userController.addUser(user2);
        userController.addFriend(user1.getId(), user2.getId());
        userController.removeFriend(user1.getId(), user2.getId());
        List<Integer> friends = userController.getFriends(user1.getId());
        assertEquals(0, friends.size());
    }
}


