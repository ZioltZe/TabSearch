package eliott.backend.model.spotify;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class SpotifyUser
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String country;
    @Transient
    private static final String DEFAULT_COUNTRY = "DEFAULT_COUNTRY";

    @Column(nullable = false)
    private String displayName;
    @Transient
    private static final String DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME";

    @Column(nullable = false)
    private String email;
    @Transient
    private static final String DEFAULT_EMAIL = "DEFAULT_EMAIL";

    @Column(nullable = false)
    private String href;
    @Transient
    private static final String DEFAULT_HREF = "DEFAULT_HREF";

    @Column(nullable = false)
    private String spotifyId;
    @Transient
    private static final String DEFAULT_SPOTIFY_ID = "DEFAULT_SPOTIFY_ID";

    @Column(nullable = false)
    private String product;
    @Transient
    private static final String DEFAULT_PRODUCT = "DEFAULT_PRODUCT";

    @Column(nullable = false)
    private String type;
    @Transient
    private static final String DEFAULT_TYPE = "DEFAULT_TYPE";

    @Column(nullable = false)
    private String uri;
    @Transient
    private static final String DEFAULT_URI = "DEFAULT_URI";

    @OneToOne(cascade = CascadeType.ALL)
    private SpotifyImage spotifyImage;

    @Lob
    @Column(nullable = false)
    private String accessToken;
    @Transient
    private static final String DEFAULT_ACCESS_TOKEN = "DEFAULT_TOKEN";

    @Lob
    @Column(nullable = false)
    private String refreshToken;
    @Transient
    private static final String DEFAULT_REFRESH_TOKEN = "DEFAULT_TOKEN";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tab> lastVisitedTabs;

    public SpotifyUser(){
        this.country = DEFAULT_COUNTRY;
        this.displayName = DEFAULT_DISPLAY_NAME;
        this.email = DEFAULT_EMAIL;
        this.href = DEFAULT_HREF;
        this.spotifyId = DEFAULT_SPOTIFY_ID;
        this.product = DEFAULT_PRODUCT;
        this.type = DEFAULT_TYPE;
        this.uri = DEFAULT_URI;
        this.spotifyImage = new SpotifyImage();
        this.accessToken = DEFAULT_ACCESS_TOKEN;
        this.refreshToken = DEFAULT_REFRESH_TOKEN;
        this.lastVisitedTabs = new ArrayList<>();
    }
}
