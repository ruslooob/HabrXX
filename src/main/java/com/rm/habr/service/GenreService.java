package com.rm.habr.service;

import com.rm.habr.model.Genre;
import com.rm.habr.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public void findAll(Model model) {
        List<Genre> genres = genreRepository.findAll();
        model.addAttribute("genres", genres);
    }
}
