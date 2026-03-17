package eliott.backend.model.spotify;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Tab
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String songName;
    @Column(nullable = false)
    private String songArtist;
    @Column(nullable = false)
    private boolean tab;
    @Column(nullable = false)
    private int tabId;

    public Tab()
    {
        this.tab = true;
        this.tabId = -1;
        this.songName = "";
        this.songArtist = "";
    }
}
