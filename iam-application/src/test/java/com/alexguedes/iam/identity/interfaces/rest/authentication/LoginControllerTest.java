package com.alexguedes.iam.identity.interfaces.rest.authentication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alexguedes.iam.identity.application.exception.AuthenticationFailedException;
import com.alexguedes.iam.identity.application.usecase.authentication.LoginUserCommand;
import com.alexguedes.iam.identity.application.usecase.authentication.LoginUserResult;
import com.alexguedes.iam.identity.application.usecase.authentication.LoginUserUseCase;
import com.alexguedes.iam.identity.domain.model.UserStatus;
import com.alexguedes.iam.identity.domain.valueobject.Email;
import com.alexguedes.iam.identity.domain.valueobject.UserId;
import com.alexguedes.iam.identity.interfaces.rest.error.IdentityRestExceptionHandler;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@ContextConfiguration(classes = {
        IdentityRestExceptionHandler.class,
        LoginController.class
})
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {

    private static final UUID USER_ID = UUID.fromString("85d2a3fe-a82f-45de-ac1a-05d71147e8fd");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginUserUseCase loginUserUseCase;

    @Test
    void shouldLoginUser() throws Exception {
        LoginUserResult result = new LoginUserResult(
                new UserId(USER_ID),
                "Alex Guedes",
                new Email("alex@example.com"),
                UserStatus.ACTIVE,
                Instant.parse("2026-07-07T12:00:00Z")
        );

        when(loginUserUseCase.execute(any(LoginUserCommand.class)))
                .thenReturn(result);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email": "alex@example.com",
                              "password": "Password123!"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(USER_ID.toString()))
                .andExpect(jsonPath("$.email").value("alex@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(loginUserUseCase).execute(any(LoginUserCommand.class));
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email": "invalid-email",
                              "password": "Password123!"
                            }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email format is invalid"));

        verifyNoInteractions(loginUserUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsBlank() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email": "alex@example.com",
                              "password": ""
                            }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password must not be blank"));

        verifyNoInteractions(loginUserUseCase);
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        when(loginUserUseCase.execute(any(LoginUserCommand.class)))
                .thenThrow(new AuthenticationFailedException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email": "alex@example.com",
                              "password": "WrongPassword123!"
                            }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));

        verify(loginUserUseCase).execute(any(LoginUserCommand.class));
    }

    @Test
    void shouldReturnBadRequestWhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email": "alex@example.com",
                              "password": "Password123!"
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request body is invalid or malformed"));

        verifyNoInteractions(loginUserUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request body is invalid or malformed"));

        verifyNoInteractions(loginUserUseCase);
    }

    @Test
    void shouldReturnUnsupportedMediaTypeWhenContentTypeIsInvalid() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("""
                            {
                              "email": "alex@example.com",
                              "password": "Password123!"
                            }
                        """))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.message").value("Content type is not supported"));

        verifyNoInteractions(loginUserUseCase);
    }
}
