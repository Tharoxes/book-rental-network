package com.tharoxes.book.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tharoxes.book.email.EmailService;
import com.tharoxes.book.email.EmailTemplateName;
import com.tharoxes.book.role.RoleRepository;
import com.tharoxes.book.security.JwtService;
import com.tharoxes.book.user.Token;
import com.tharoxes.book.user.TokenRepository;
import com.tharoxes.book.user.User;
import com.tharoxes.book.user.UserRepository;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;
	
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    
    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                // todo - better exception handling
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

	private void sendValidationEmail(User user) throws MessagingException {
		// TODO Auto-generated method stub
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
                );	
	}

	private String generateAndSaveActivationToken(User user) {
		//generate token
		String generatedToken = generateActivationCode(6);
		var token = Token.builder()
				.token(generatedToken)
				.createdAt(LocalDateTime.now())
				.expiresAt(LocalDateTime.now().plusMinutes(15))
				.user(user)
				.build();
		tokenRepository.save(token);
		return generatedToken;
	}

	private String generateActivationCode(int length) {
		String characters = "0123456789";
		StringBuilder codeBuilder = new StringBuilder();
		SecureRandom secureRandom = new SecureRandom();
		for(int i = 0;i< length; i++) {
			int randomIndex = secureRandom.nextInt(characters.length()); // 0..9
			codeBuilder.append(characters.charAt(randomIndex));
		}
		return codeBuilder.toString();
	}


}
