package com.joana.authentication.services;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.joana.authentication.models.LoginUser;
import com.joana.authentication.models.User;
import com.joana.authentication.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;
    // This method will be called from the controller
    // whenever a user submits a registration form.

    // TO-DO: Write register and login methods!
    public User register(User newUser, BindingResult result) {
        // TO-DO: Additional validations!
        // TO-DO - Reject values or register if no errors:
        Optional<User> potentialUser = userRepo.findByEmail(newUser.getEmail());
        // Reject if email is taken (present in database)
        if (potentialUser.isPresent()) {
            result.rejectValue("email", "emailExists", "A user with this email is already registered");
        }
        // Reject if password doesn't match confirmation
        if (!newUser.getPassword().equals(newUser.getConfirm())) {
            result.rejectValue("confirm", "passwordMismatch", "Passwords must match");
        }
        // Return null if result has errors
        if (result.hasErrors()) {
            return null;
        } else {
            // Hash and set password, save user to database
            String hashed = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
            newUser.setPassword(hashed);
            return userRepo.save(newUser);
        }

    }

    // This method will be called from the controller
    // whenever a user submits a login form.
    public User login(LoginUser newLoginObject, BindingResult result) {
        // TO-DO: Additional validations!
        Optional<User> potentialUser = userRepo.findByEmail(newLoginObject.getEmail());

        // TO-DO - Reject values:
        if (potentialUser.isEmpty()) {
            // Find user in the DB by email
            result.rejectValue("email", "emailNotFound", "This email is not registerd.");
        } else {
            User user = potentialUser.get();
            // Reject if NOT present
            if (!BCrypt.checkpw(newLoginObject.getPassword(), user.getPassword())) {
                result.rejectValue("password", "Matches", "Invalid Password!");
            }
        }

        // Return null if result has errors
        if (result.hasErrors()) {
            return null;
        } else {
            // Otherwise, return the user object
            User user = potentialUser.get();
            return user;
        }
    }

    public User getById(Long id) {
        Optional<User> potentialUser = userRepo.findById(id);
        if (potentialUser.isEmpty()) {
            return null;
        } else {
            return potentialUser.get();
        }
    }
}
