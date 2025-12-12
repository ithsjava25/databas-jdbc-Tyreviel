package com.example;

import com.example.MoonMission;
import java.util.List;
import java.util.Optional;

public interface MoonMissionRepository {
    List<MoonMission> findAll();
    Optional<MoonMission> findById(int missionId);
    int countByYear(int year);
}
