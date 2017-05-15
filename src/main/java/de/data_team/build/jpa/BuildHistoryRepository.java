package de.data_team.build.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.data_team.build.model.BuildHistory;

@Repository
public interface BuildHistoryRepository extends JpaRepository<BuildHistory, Long> {

    BuildHistory findByBuildNumber(Long buildNumber);

}
