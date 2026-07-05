package com.alexguedes.iam.identity.interfaces.rest.registration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alexguedes.iam.identity.application.usecase.RegisterUserCommand;
import com.alexguedes.iam.identity.application.usecase.RegisterUserResult;
import com.alexguedes.iam.identity.application.usecase.RegisterUserUseCase;
import com.alexguedes.iam.identity.domain.model.UserStatus;
import com.alexguedes.iam.identity.domain.valueobject.Email;
import com.alexguedes.iam.identity.domain.valueobject.UserId;

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
@ContextConfiguration(classes = UserRegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @Test
    void shouldRegisterUser() throws Exception {
        RegisterUserResult result = new RegisterUserResult(
                new UserId(UUID.randomUUID()),
                "Alex Guedes",
                new Email("alex@example.com"),
                UserStatus.ACTIVE,
                Instant.now()
        );

        when(registerUserUseCase.execute(any(RegisterUserCommand.class)))
                .thenReturn(result);

        mockMvc.perform(post("/identity/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name": "Alex Guedes",
                              "email": "alex@example.com",
                              "password": "Senha123456"
                            }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alex Guedes"))
                .andExpect(jsonPath("$.email").value("alex@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(registerUserUseCase).execute(any(RegisterUserCommand.class));
    }


}