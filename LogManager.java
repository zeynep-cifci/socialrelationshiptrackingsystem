/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socialrelationshiptrackingsystem;

/**
 *
 * @author Kullanıcı
 */
public class LogManager {

    private static ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    public static void log(User user, String actionType, String description) {
        activityLogDAO.addLog(user, actionType, description);
        FileLogManager.writeLog(actionType, description);
    }
}
