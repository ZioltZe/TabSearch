package eliott.backend.security.spotify;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spotify")
public class SpotifyConfig
{
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
