package com.example;
import com.example.Account;
import com.example.AccountRepository;
import com.example.AccountRepositoryJdbc;
import com.example.MoonMissionRepository;
import com.example.MoonMissionRepositoryJdbc;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;


public class Main {

    static void main(String[] args) {
        if (isDevMode(args)) {
            DevDatabaseInitializer.start();
        }
        new Main().run();
    }

    public void run() {
        String jdbcUrl = resolveConfig("APP_JDBC_URL", "APP_JDBC_URL");
        String dbUser = resolveConfig("APP_DB_USER", "APP_DB_USER");
        String dbPass = resolveConfig("APP_DB_PASS", "APP_DB_PASS");
        System.out.println("Connecting to DB: " + jdbcUrl);
        System.out.println("DB User: " + dbUser);

        if (jdbcUrl == null || dbUser == null || dbPass == null) {
            throw new IllegalStateException("Missing DB configuration...");
        }

        SimpleDriverManagerDataSource ds = new SimpleDriverManagerDataSource(jdbcUrl, dbUser, dbPass);
        AccountRepository accountRepo = new AccountRepositoryJdbc(ds);
        MoonMissionRepository missionRepo = new MoonMissionRepositoryJdbc(ds);
        //Seed data test
        try (Connection conn = ds.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT name, password FROM account")) {
            while (rs.next()) {
                System.out.println("Seed account: " + rs.getString("name") + " / " + rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        System.out.println("Username:");
        String username = IO.readln();
        System.out.println("Password:");
        String password = IO.readln();

        if (accountRepo.findByNameAndPassword(username, password).isPresent()) {
            System.out.println("username accepted");
            menuLoop(accountRepo, missionRepo);
        } else {
            System.out.println("Invalid username or password");
            System.out.println("0) Exit");
            String opt = IO.readln();
            if ("0".equals(opt)) {
                return;
            }
        }
    }

    private void menuLoop(AccountRepository accountRepo, MoonMissionRepository missionRepo) {
        boolean running = true;
        while (running) {
            System.out.println("Menu:");
            System.out.println("1) List moon missions");
            System.out.println("2) Get mission by id");
            System.out.println("3) Count missions by year");
            System.out.println("4) Create account");
            System.out.println("5) Update account password");
            System.out.println("6) Delete account");
            System.out.println("0) Exit");

            String choice = IO.readln();
            switch (choice) {
                case "1":
                    missionRepo.findAll().forEach(m -> System.out.println(m.spacecraft()));
                    break;
                case "2":
                    System.out.println("mission_id:");
                    int id = Integer.parseInt(IO.readln());
                    missionRepo.findById(id).ifPresentOrElse(
                            m -> System.out.println("Mission " + m.missionId() + ": " + m.spacecraft()),
                            () -> System.out.println("No mission found")
                    );
                    break;
                case "3":
                    System.out.println("year:");
                    int year = Integer.parseInt(IO.readln());
                    int count = missionRepo.countByYear(year);
                    System.out.println(count);
                    break;
                case "4":
                    System.out.println("first name:");
                    String fn = IO.readln();
                    System.out.println("last name:");
                    String ln = IO.readln();
                    System.out.println("ssn:");
                    String ssn = IO.readln();
                    System.out.println("password:");
                    String pw = IO.readln();
                    String name = fn.substring(0,3) + ln.substring(0,3);
                    accountRepo.create(new Account(0, name, pw, fn, ln, ssn));
                    System.out.println("account created");
                    break;
                case "5":
                    System.out.println("user_id:");
                    int uid = Integer.parseInt(IO.readln());
                    System.out.println("new password:");
                    String newPw = IO.readln();
                    if (accountRepo.updatePassword(uid, newPw)) {
                        System.out.println("updated");
                    }
                    break;
                case "6":
                    System.out.println("user_id:");
                    int delId = Integer.parseInt(IO.readln());
                    if (accountRepo.delete(delId)) {
                        System.out.println("deleted");
                    }
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private static boolean isDevMode(String[] args) {
        if (Boolean.getBoolean("devMode")) return true;
        if ("true".equalsIgnoreCase(System.getenv("DEV_MODE"))) return true;
        return Arrays.asList(args).contains("--dev");
    }

    private static String resolveConfig(String propertyKey, String envKey) {
        String v = System.getProperty(propertyKey);
        if (v == null || v.trim().isEmpty()) {
            v = System.getenv(envKey);
        }
        return (v == null || v.trim().isEmpty()) ? null : v.trim();
    }
}
