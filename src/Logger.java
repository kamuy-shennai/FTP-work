import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE_PATH = "src/log.txt";

    public static void logLoginSuccess(String ipAddress, String username) {
        String logMessage = String.format("[%s] IP地址：%s 用户名：%s 登录成功",
                getCurrentTimestamp(), ipAddress, username);
        writeLog(logMessage);
    }

    public static void logLoginFailure(String ipAddress, String attemptedUsername) {
        String logMessage = String.format("[%s] IP地址：%s 尝试的用户名：%s 登录失败",
                getCurrentTimestamp(), ipAddress, attemptedUsername);
        writeLog(logMessage);
    }

    public static void logAction(String ipAddress, String username, String actionName) {
        String logMessage = String.format("[%s] IP地址：%s 用户名：%s 执行的操作：%s ",
                getCurrentTimestamp(), ipAddress, username, actionName);
        writeLog(logMessage);
    }

    private static void writeLog(String logMessage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentTimestamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentTime.format(formatter);
    }
}
