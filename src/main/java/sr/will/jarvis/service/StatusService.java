package sr.will.jarvis.service;

import net.dv8tion.jda.core.entities.Game;
import sr.will.jarvis.Jarvis;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class StatusService extends Thread {
    private long timeInterval;
    private ArrayList<String> statusMessages;

    public StatusService(long timeInterval, ArrayList<String> statusMessages) {
        this.timeInterval = timeInterval;
        this.statusMessages = statusMessages;
    }

    public void run() {
        while (true) {
            try {
                int rand = ThreadLocalRandom.current().nextInt(0, statusMessages.size());
                Jarvis.getInstance().getJda().getPresence().setGame(Game.of(statusMessages.get(rand)));

                long sleepTime = timeInterval - (System.currentTimeMillis() % timeInterval);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                if (!Jarvis.getInstance().running) {
                    return;
                }
                e.printStackTrace();
            }
        }
    }
}
