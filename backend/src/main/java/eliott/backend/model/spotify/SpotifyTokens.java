package eliott.backend.model.spotify;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SpotifyTokens
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column
    private String access_token;
    @Column
    private String refresh_token;

    public SpotifyTokens()
    {
        this.access_token = "";
        this.refresh_token = "";
    }

    public SpotifyTokens(String access_token, String refresh_token)
    {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }
}
