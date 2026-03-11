import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MonitorListener implements ServletContextListener {
    private Thread monitorThread;

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        DataBaseMonitor dm = new DataBaseMonitor();
        monitorThread = new Thread(dm);
        monitorThread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        monitorThread.interrupt();
    }

}
