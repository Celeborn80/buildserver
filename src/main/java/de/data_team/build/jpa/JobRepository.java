package de.data_team.build.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.data_team.build.model.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

}
