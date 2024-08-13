package com.at.gr.controller;

import com.at.gr.dto.Cinema;
import com.at.gr.service.ICinemaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 8/12/2024
 */
@RestController
@RequestMapping("/api/v1/movies")
public class CinemaController {

    private final ICinemaService iCinemaService;

    public CinemaController(ICinemaService iCinemaService) {
        this.iCinemaService = iCinemaService;
    }

    @GetMapping
    public List<Cinema> getCinema(@RequestParam("from") LocalDateTime from,
                                  @RequestParam("to") LocalDateTime to) {
        return iCinemaService.getMovies(from, to);
    }
}
