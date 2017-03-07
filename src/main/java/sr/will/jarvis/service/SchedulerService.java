package sr.will.jarvis.service;

import sr.will.jarvis.Jarvis;

public class SchedulerService extends Thread {
    private int pollingInterval;

    public SchedulerService(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public void run() {
        System.out.println("Scheduling thread started");

        while (true) {
            try {
                long milliseconds = pollingInterval - (System.currentTimeMillis() % pollingInterval);
                Thread.sleep(milliseconds);

                long time = Math.floorDiv(System.currentTimeMillis(), 1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkDatabase() {
        Jarvis.getInstance().database.executeQuery("SELECT FROM ");
    }
}
