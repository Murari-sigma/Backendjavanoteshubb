package com.murari.javanoteshubb.repository;

import com.murari.javanoteshubb.entity.Note;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByCategory(String category);
}
