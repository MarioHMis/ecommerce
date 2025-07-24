package com.marware.ecommerce.service;

import com.marware.ecommerce.dto.AuthRequest;
import com.marware.ecommerce.dto.AuthResponse;
import com.marware.ecommerce.dto.RegisterRequest;
import com.marware.ecommerce.exception.UnauthorizedException;
import com.marware.ecommerce.model.Role;
import com.marware.ecommerce.model.Tenant;
import com.marware.ecommerce.model.User;
import com.marware.ecommerce.repository.RoleRepository;
import com.marware.ecommerce.repository.TenantRepository;
import com.marware.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks AuthService authService;
    @Mock          UserRepository userRepo;
    @Mock          RoleRepository roleRepo;
    @Mock          TenantRepository tenantRepo;
    @Mock          PasswordEncoder encoder;
    @Mock          JwtService jwtService;
    @Mock          AuthenticationManager authManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_whenTenantAndRoleExist_thenReturnToken() {
        // given
        RegisterRequest req = new RegisterRequest();
        req.setTenantId(5L);
        req.setEmail("a@b.c");
        req.setFullName("Foo");
        req.setPassword("pass");

        Tenant ten = new Tenant(5L, "Demo", "desc");
        when(tenantRepo.findById(5L)).thenReturn(Optional.of(ten));

        Role role = new Role(1L, "ROLE_CUSTOMER");
        when(roleRepo.findByName("ROLE_CUSTOMER")).thenReturn(Optional.of(role));

        when(encoder.encode("pass")).thenReturn("ENC");

        User saved = User.builder()
                .id(10L)
                .email("a@b.c")
                .fullName("Foo")
                .tenant(ten)
                .roles(Set.of(role))
                .password("ENC")
                .build();
        when(userRepo.save(any())).thenReturn(saved);
        when(jwtService.generateToken(any())).thenReturn("JWT-TOKEN");

        // when
        AuthResponse resp = authService.register(req);

        // then
        assertThat(resp.getToken()).isEqualTo("JWT-TOKEN");
    }

    @Test
    void login_whenBadCredentials_thenThrowUnauthorized() {
        // given
        AuthRequest req = new AuthRequest();
        req.setEmail("x");
        req.setPassword("y");
        doThrow(new RuntimeException()).when(authManager)
                .authenticate(new UsernamePasswordAuthenticationToken("x", "y"));

        // when / then
        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void login_whenSuccess_thenReturnsToken() {
        // given
        AuthRequest req = new AuthRequest();
        req.setEmail("u");
        req.setPassword("p");
        when(authManager.authenticate(any())).thenReturn(null);

        User u = new User();
        u.setEmail("u");
        when(userRepo.findByEmail("u")).thenReturn(Optional.of(u));
        when(jwtService.generateToken(u)).thenReturn("TK");

        // when
        AuthResponse res = authService.login(req);

        // then
        assertThat(res.getToken()).isEqualTo("TK");
    }
}
