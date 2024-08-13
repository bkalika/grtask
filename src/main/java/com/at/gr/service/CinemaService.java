package com.at.gr.service;

import com.at.gr.dto.Cinema;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CinemaService implements ICinemaService {

    @Override
    public List<Cinema> getMovies(LocalDateTime from, LocalDateTime to) {
        List<Cinema> cinemas = new ArrayList<>();
        // add movies
        return cinemas.stream()
                .filter(s -> s.screeningTime().isAfter(from))
                .filter(s1 -> s1.screeningTime().isBefore(to))
                .sorted(Comparator.comparing(Cinema::title).thenComparing(Cinema::screeningTime))
                .toList();
    }
}