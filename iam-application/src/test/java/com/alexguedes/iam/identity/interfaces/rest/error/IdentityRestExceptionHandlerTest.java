package com.alexguedes.iam.identity.interfaces.rest.error;

import com.alexguedes.iam.identity.application.RegisterUserCommand;
import com.alexguedes.iam.identity.application.RegisterUserUseCase;
import com.alexguedes.iam.identity.domain.exception.InvalidEmailException;
import com.alexguedes.iam.identity.domain.exception.UserAlreadyExistsException;
import com.alexguedes.iam.identity.domain.valueobject.Email;
import com.alexguedes.iam.identity.domain.valueobject.UserId;
import com.alexguedes.iam.identity.interfaces.rest.registration.UserRegistrationController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {
        IdentityRestExceptionHandler.class,
        UserRegistrationController.class,
})
@AutoConfigureMockMvc(addFilters = false)
public class IdentityRestExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    // BAD_REQUEST
    @Test
    void shouldReturnBadRequestWhenRequestEmailFormatIsInvalid() throws Exception {
        mockMvc.perform(post("/identity/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Alex Guedes",
                          "email": "invalid-email",
                          "password": "Senha123456"
                        }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email format is invalid"));

        verifyNoInteractions(registerUserUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenInvalidEmailExceptionOccurs() throws Exception {
        when(registerUserUseCase.execute(any(RegisterUserCommand.class)))
                .thenThrow(new InvalidEmailException("Invalid Email"));

        mockMvc.perform(post("/identity/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Alex Guedes",
                          "email": "alex@example.com",
                          "password": "Senha123456"
                        }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid Email"));

        verify(registerUserUseCase).execute(any(RegisterUserCommand.class));
    }

    @Test
    void shouldReturnBadRequestWhenNameIsInvalid() throws Exception {
        mockMvc.perform(post("/identity/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "",
                          "email": "alex@example.com",
                          "password": "123456"
                        }
                    """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(registerUserUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsInvalid() throws Exception {
        mockMvc.perform(post("/identity/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Alex Guedes",
                          "email": "alex@example.com",
                          "password": "1"
                        }
                    """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(registerUserUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/identity/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Alex Guedes",
                          "email": "alex@example.com",
                          "password": "Senha123456"
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request body is invalid or malformed"));

        verifyNoInteractions(registerUserUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/identity/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request body is invalid or malformed"));

        verifyNoInteractions(registerUserUseCase);
    }

    // Content-Type incorreto
    @Test
    void shouldReturnUnsupportedMediaTypeWhenContentTypeIsInvalid() throws Exception {
        mockMvc.perform(post("/identity/users/register")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("""
                        {
                          "name": "Alex Guedes",
                          "email": "alex@example.com",
                          "password": "Senha123456"
                        }
                    """))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.message").value("Content type is not supported"));

        verifyNoInteractions(registerUserUseCase);
    }

    //CONFLICT
    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        when(registerUserUseCase.execute(any(RegisterUserCommand.class)))
                .thenThrow(new UserAlreadyExistsException("User already exists"));

        mockMvc.perform(post("/identity/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Alex Guedes",
                          "email": "alex@example.com",
                          "password": "Senha123456"
                        }
                    """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User already exists"));
        verify(registerUserUseCase).execute(any(RegisterUserCommand.class));
    }

    // Internal Server Error
    @Test
    void shouldReturnInternalServerErrorWhenUnexpectedExceptionOccurs() throws Exception {
        when(registerUserUseCase.execute(any(RegisterUserCommand.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/identity/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Alex Guedes",
                          "email": "alex@example.com",
                          "password": "Senha123456"
                        }
                    """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal server error"));

        verify(registerUserUseCase).execute(any(RegisterUserCommand.class));
    }


}
