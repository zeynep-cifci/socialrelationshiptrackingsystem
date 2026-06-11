/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socialrelationshiptrackingsystem;

import java.sql.Connection;

/**
 *
 * @author Kullanıcı
 */
public class TestConnection {

    public static void main(String[] args) {
        try {
            Connection con = DatabaseConnection.getConnection();

            System.out.println("Database connected successfully");

            con.close();

        } catch (Exception e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}
