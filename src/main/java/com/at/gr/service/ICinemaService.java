package com.at.gr.service;

import com.at.gr.dto.Cinema;

import java.time.LocalDateTime;
import java.util.List;

public interface ICinemaService {

    List<Cinema> getMovies(LocalDateTime from, LocalDateTime to);
}
