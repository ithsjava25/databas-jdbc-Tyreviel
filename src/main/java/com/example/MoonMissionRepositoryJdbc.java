package com.example;

import java.sql.*;
import java.util.*;

public class MoonMissionRepositoryJdbc implements MoonMissionRepository {
    private final SimpleDriverManagerDataSource ds;

    public MoonMissionRepositoryJdbc(SimpleDriverManagerDataSource ds) {
        this.ds = ds;
    }

    @Override
    public List<MoonMission> findAll() {
        List<MoonMission> missions = new ArrayList<>();
        String sql = "SELECT * FROM moon_mission ORDER BY mission_id";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                missions.add(new MoonMission(
                        rs.getInt("mission_id"),
                        rs.getString("spacecraft"),
                        rs.getDate("launch_date").toLocalDate(),
                        rs.getString("carrier_rocket"),
                        rs.getString("operator"),
                        rs.getString("mission_type"),
                        rs.getString("outcome")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return missions;
    }

    @Override
    public Optional<MoonMission> findById(int missionId) {
        String sql = "SELECT * FROM moon_mission WHERE mission_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, missionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new MoonMission(
                            rs.getInt("mission_id"),
                            rs.getString("spacecraft"),
                            rs.getDate("launch_date").toLocalDate(),
                            rs.getString("carrier_rocket"),
                            rs.getString("operator"),
                            rs.getString("mission_type"),
                            rs.getString("outcome")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public int countByYear(int year) {
        String sql = "SELECT COUNT(*) FROM moon_mission WHERE launch_date >= ? AND launch_date < ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.of(year, 1, 1)));
            ps.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.of(year + 1, 1, 1)));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
