package ru.yandex.practicum.filmorate.ControllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.MPAController;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class MpaTest {
    @Autowired
    public MpaTest(MPAController mpaController) {
        this.mpaController = mpaController;
    }

    private final MPAController mpaController;

    @Test
    void getAllMpaTest() {
        Mpa mpa1 = mpaController.getMpaById(1);
        Mpa mpa2 = mpaController.getMpaById(2);
        Mpa mpa3 = mpaController.getMpaById(3);
        Mpa mpa4 = mpaController.getMpaById(4);
        Mpa mpa5 = mpaController.getMpaById(5);

        List<Mpa> mpaList = mpaController.getAllMpa();
        assertThat(mpaList).isNotNull();
        assertThat(mpaList.isEmpty()).isFalse();
        assertThat(mpaList.contains(mpa1)).isTrue();
        assertThat(mpaList.contains(mpa2)).isTrue();
        assertThat(mpaList.contains(mpa3)).isTrue();
        assertThat(mpaList.contains(mpa4)).isTrue();
        assertThat(mpaList.contains(mpa5)).isTrue();
    }
}
