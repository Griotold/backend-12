package com.sparta.backend_12.infra;

import com.sparta.backend_12.domain.entity.User;
import com.sparta.backend_12.domain.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @DisplayName("findByUsername - 저장된 사용자 존재 시 실제 객체 반환")
    @Test
    void findByUsername_existingUser_returnsUser() {
        // given
        User user = User.create("JIN HO", "12341234", "Mentos", Role.USER);
        userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findByUsername("JIN HO");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get())
                .usingRecursiveComparison()
                .ignoringFields("id")  // ID는 자동 생성되므로 제외
                .isEqualTo(user);
    }

    @DisplayName("findByUsername - 사용자 없을 시 Optional.empty 반환")
    @Test
    void findByUsername_nonExistentUser_returnsEmpty() {
        // when
        Optional<User> foundUser = userRepository.findByUsername("nonExistentUser");

        // then
        assertThat(foundUser).isEmpty();
    }
}