/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socialrelationshiptrackingsystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author Kullanıcı
 */
public class FileLogManager {

    private static final String FILE_NAME = "activity_log.txt";

    public static void writeLog(String actionType, String description) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {

            writer.write("[" + LocalDateTime.now() + "] "
                    + actionType + " - "
                    + description);

            writer.newLine();

        } catch (IOException e) {
            System.out.println("File log error: " + e.getMessage());
        }
    }
}
