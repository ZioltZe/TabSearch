package eliott.backend.repository;

import eliott.backend.model.spotify.SpotifyImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotifyImageRepository extends CrudRepository<SpotifyImage, Long>
{

}
