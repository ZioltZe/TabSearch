package eliott.backend.repository;

import eliott.backend.model.spotify.SpotifyUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotifyUserRepository extends CrudRepository<SpotifyUser, Long>
{

}
