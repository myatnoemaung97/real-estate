package com.example.Online.Housing.Show.config;

import com.example.Online.Housing.Show.models.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component("mySecurity")
public class MySecurity {

    public boolean checkAccountId(Authentication authentication, Long accountId) {
        // Check if the user is authenticated and has the correct role
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            Account authenticatedUser = (Account) authentication.getPrincipal();
            for (GrantedAuthority auth : authenticatedUser.getAuthorities()) {
                if (auth.getAuthority().equals("ROLE_ADMIN")) {
                    // Admins can access any account
                    return true;
                }
            }

            // Check if the authenticated user's ID matches the requested accountId
            if (authenticatedUser.getUsername().equals(accountId.toString())) {
                return true;
            }
        }

        // Return false if the user is not authenticated or the IDs don't match
        return false;
    }
}
