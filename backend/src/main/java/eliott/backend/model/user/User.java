package eliott.backend.model.user;

import eliott.backend.model.role.Role;
import eliott.backend.model.spotify.SpotifyUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="users")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String email;
    @Transient
    private static final String DEFAULT_EMAIL = "default@email.com";

    @Column(nullable = false)
    private String password;
    @Transient
    private static final String DEFAULT_PASSWORD = "password";

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @OneToOne(cascade = CascadeType.ALL) // TODO: orphan removal?
    private SpotifyUser spotifyUser;

    public User()
    {
        this.email = DEFAULT_EMAIL;
        this.password = DEFAULT_PASSWORD;
        this.roles = new HashSet<>();
        this.spotifyUser = new SpotifyUser();
    }

    public User(String email, String password, Set<Role> roles)
    {
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.spotifyUser = new SpotifyUser();
    }
}
