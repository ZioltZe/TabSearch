package eliott.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/songsterr")
public class SongsterrController
{
    @GetMapping("/search")
    public String search(@RequestParam("key") String keyword)
    {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://www.songsterr.com/api/search?pattern=" + keyword;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }
}
