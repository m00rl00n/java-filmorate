package ru.yandex.practicum.filmorate.ControllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserTest {

    private final UserController userController;

    @Autowired
    public UserTest(UserController userController) {
        this.userController = userController;
    }


    @Test
    void testAddUser() {
        User user = new User(1, "test@1.com", "логин", "имя", LocalDate.of(2013, 9, 1));
        User addedUser = userController.addUser(user);

        assertThat(addedUser.getId()).isNotNull();
        assertThat(addedUser.getEmail()).isEqualTo("test@1.com");
        assertThat(addedUser.getLogin()).isEqualTo("логин");
        assertThat(addedUser.getName()).isEqualTo("имя");
        assertThat(addedUser.getBirthday()).isEqualTo(LocalDate.of(2013, 9, 1));
    }

    @Test
    void testUpdateUser() {
        User user = new User(2, "test@1.com", "логин", "имя", LocalDate.of(2013, 9, 1));
        User addedUser = userController.addUser(user);

        addedUser.setEmail("updated@test.com");
        addedUser.setLogin("updatedLogin");
        addedUser.setName("updatedName");
        addedUser.setBirthday(LocalDate.of(2000, 1, 1));
        User updatedUser = userController.updateUser(addedUser);
        assertThat(updatedUser.getId()).isEqualTo(addedUser.getId());
        assertThat(updatedUser.getEmail()).isEqualTo("updated@test.com");
        assertThat(updatedUser.getLogin()).isEqualTo("updatedLogin");
        assertThat(updatedUser.getName()).isEqualTo("updatedName");
        assertThat(updatedUser.getBirthday()).isEqualTo(LocalDate.of(2000, 1, 1));
    }


    @Test
    void testGetUser() {
        Integer userId = 1;
        User user = userController.getUser(userId);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(userId);
    }

    @Test
    void testAddFriendAndGetFriends() {
        User user1 = new User(4, "test@4.com", "логин4", "имя4", LocalDate.of(2023, 5, 25));
        User user2 = new User(5, "test@5.com", "логин5", "имя5", LocalDate.of(2023, 5, 25));
        userController.addUser(user1);
        userController.addUser(user2);

        userController.addFriend(user1.getId(), user2.getId());

        List<User> friends = userController.getFriends(user1.getId());
        assertThat(friends).isNotNull();
        assertThat(friends.size()).isEqualTo(1);
        assertThat(friends.get(0).getId()).isEqualTo(user2.getId());
    }

    @Test
    void testDeleteFriend() {
        Integer userId = 1;
        Integer friendId = 2;
        assertDoesNotThrow(() -> userController.deleteFriend(userId, friendId));
    }

    @Test
    void testGetCommonFriends() {
        User user1 = new User(6, "test@6.com", "логин6", "имя6", LocalDate.of(2023, 5, 25));
        User user2 = new User(7, "test@7.com", "логин7", "имя7", LocalDate.of(2023, 5, 25));
        User user3 = new User(8, "test@8.com", "логин8", "имя8", LocalDate.of(2023, 5, 25));
        userController.addUser(user1);
        userController.addUser(user2);
        userController.addUser(user3);

        userController.addFriend(user1.getId(), user2.getId());
        userController.addFriend(user1.getId(), user3.getId());
        userController.addFriend(user2.getId(), user3.getId());

        List<User> commonFriends = userController.getCommonFriends(user1.getId(), user2.getId());
        assertThat(commonFriends).isNotNull();
        assertThat(commonFriends.size()).isEqualTo(1);
        assertThat(commonFriends.get(0).getId()).isEqualTo(user3.getId());
    }
}


