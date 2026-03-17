package eliott.backend.controller;

import eliott.backend.model.spotify.SpotifyUser;
import eliott.backend.model.user.User;
import eliott.backend.repository.UserRepository;
import eliott.backend.service.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/spotify")
public class SpotifyController
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpotifyService spotifyService;

    @GetMapping("/profile/{email}")
    public SpotifyUser GetProfile(@PathVariable String email)
    {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));
        return user.getSpotifyUser();
    }

    @PostMapping("/profile/{email}/connect")
    public void ConnectProfile(@PathVariable String email ,@RequestBody SpotifyUser dbProfile)
    {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));
        spotifyService.connectSpotifyProfile(user, dbProfile);
    }

    @PutMapping("/profile/{email}/updateTokens")
    public void UpdateTokens(@PathVariable String email, @RequestParam("access") String accessToken, @RequestParam("refresh") String refreshToken)
    {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));
        spotifyService.updateTokens(user, accessToken, refreshToken);
    }
}
