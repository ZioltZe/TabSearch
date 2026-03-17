package eliott.backend.model.spotify;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SpotifyImage
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String url;
    @Transient
    private static final String DEFAULT_URL= "DEFAULT_URL";

    @Column(nullable = false)
    private int height;
    @Transient
    private static final int DEFAULT_HEIGHT = 30;

    @Column(nullable = false)
    private int width;
    @Transient
    private static final int DEFAULT_WIDTH = 30;

    public SpotifyImage() {
        this.url = DEFAULT_URL;
        this.height = DEFAULT_HEIGHT;
        this.width = DEFAULT_WIDTH;
    }
}