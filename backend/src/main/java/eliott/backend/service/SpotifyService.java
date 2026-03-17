package eliott.backend.service;

import eliott.backend.model.spotify.SpotifyImage;
import eliott.backend.model.spotify.SpotifyUser;
import eliott.backend.model.user.User;
import eliott.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpotifyService
{
    @Autowired
    private UserRepository userRepository;

    public void connectSpotifyProfile(User user, SpotifyUser spotifyUser)
    {
        SpotifyUser sp = user.getSpotifyUser();
        sp.setCountry(spotifyUser.getCountry());
        sp.setDisplayName(spotifyUser.getDisplayName());
        sp.setEmail(spotifyUser.getEmail());
        sp.setHref(spotifyUser.getHref());
        sp.setSpotifyId(spotifyUser.getSpotifyId());
        sp.setProduct(spotifyUser.getProduct());
        sp.setType(spotifyUser.getType());
        sp.setUri(spotifyUser.getUri());
        sp.setAccessToken(spotifyUser.getAccessToken());
        sp.setRefreshToken(spotifyUser.getRefreshToken());

        SpotifyImage spIm = sp.getSpotifyImage();
        spIm.setUrl(spotifyUser.getSpotifyImage().getUrl());
        spIm.setWidth(spotifyUser.getSpotifyImage().getWidth());
        spIm.setHeight(spotifyUser.getSpotifyImage().getHeight());

        user.setSpotifyUser(sp);
        userRepository.save(user);
    }

    public void updateTokens(User user, String accessToken, String refreshToken)
    {
        SpotifyUser sp = user.getSpotifyUser();
        sp.setAccessToken(accessToken);
        sp.setRefreshToken(refreshToken);

        user.setSpotifyUser(sp);
        userRepository.save(user);
    }
}