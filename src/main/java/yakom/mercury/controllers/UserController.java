package yakom.mercury.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static yakom.mercury.Constants.UI_ME_PATH;
import static yakom.mercury.Constants.WS_ME_PATH;

@RestController
public class UserController {

    @GetMapping(value = UI_ME_PATH, produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') AND hasRole('ROLE_UI_USER')")
    public Map<String, Object> uiMe(
            @AuthenticationPrincipal UserDetails principal) {
        return singletonMap("username", principal.getUsername());
    }

    @GetMapping(value = WS_ME_PATH, produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') AND hasRole('ROLE_WS_USER')")
    public Map<String, Object> wsMe(
            @AuthenticationPrincipal UserDetails principal) {
        return singletonMap("username", principal.getUsername());
    }
}
