package com.example;

import java.time.LocalDate;

public record MoonMission(
        int missionId,
        String spacecraft,
        LocalDate launchDate,
        String carrierRocket,
        String operator,
        String missionType,
        String outcome
) {}

