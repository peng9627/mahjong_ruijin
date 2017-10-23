package mahjong.constant;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by pengyi
 * Date : 17-9-6.
 * desc:
 */
public class Constant {

    public static String apiUrl = "http://127.0.0.1:9999/api";
    public static String userInfoUrl = "/user/info";
    public static String userListUrl = "/user/list";
    public static String gamerecordCreateUrl = "/gamerecord/create";
    public static String moneyDetailedCreate = "/money_detailed/create";

    public static int readyTimeout = 10000;
    public static int playCardTimeout = 18000;
    public static int dissolve = 60000;
    public static int messageTimeout = 60000;
    public static int matchEliminateScoreTimeout = 30000;
    public static int matchEliminateScore = 100;

    public static void init() {
        BufferedInputStream in = null;
        try {
            Properties prop = new Properties();
            prop.load(Constant.class.getResourceAsStream("/config.properties"));

            apiUrl = prop.getProperty("apiUrl");
            userInfoUrl = prop.getProperty("userInfoUrl");
            userListUrl = prop.getProperty("userListUrl");
            gamerecordCreateUrl = prop.getProperty("gamerecordCreateUrl");
            moneyDetailedCreate = prop.getProperty("moneyDetailedCreate");
            readyTimeout = Integer.parseInt(prop.getProperty("readyTimeout"));
            playCardTimeout = Integer.parseInt(prop.getProperty("playCardTimeout"));
            dissolve = Integer.parseInt(prop.getProperty("dissolve"));
            messageTimeout = Integer.parseInt(prop.getProperty("messageTimeout"));
            matchEliminateScoreTimeout = Integer.parseInt(prop.getProperty("matchEliminateScoreTimeout"));
            matchEliminateScore = Integer.parseInt(prop.getProperty("matchEliminateScore"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
