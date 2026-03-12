package com.alba.platform.repository;

import com.alba.platform.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermRepository extends JpaRepository<Term, String> {

    List<Term> findAllByOrderByRequiredDescIdAsc();
}