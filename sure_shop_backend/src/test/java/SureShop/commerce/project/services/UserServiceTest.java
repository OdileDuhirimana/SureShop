package SureShop.commerce.project.services;

import SureShop.commerce.project.models.User;
import SureShop.commerce.project.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser_EncodesPassword() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("plainpass")
                .build();
        when(passwordEncoder.encode("plainpass")).thenReturn("encodedpass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        User saved = userService.saveUser(user);
        assertEquals("encodedpass", saved.getPassword());
        verify(passwordEncoder).encode("plainpass");
        verify(userRepository).save(any(User.class));
    }
}