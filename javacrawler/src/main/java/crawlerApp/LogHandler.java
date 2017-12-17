package crawlerApp;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Teun on 16-12-2017.
 */
public class LogHandler {
    public static void setLogLevel(Level targetLevel) {
        Logger root = Logger.getLogger("");
        root.setLevel(targetLevel);
        for (Handler handler : root.getHandlers()) {
            handler.setLevel(targetLevel);
        }
    }
}
