package sr.will.jarvis.manager;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;

import java.util.ArrayList;

public class ThreadManager {
    public Jarvis jarvis;

    public ArrayList<ThreadBoss> threads = new ArrayList<>();
    private long threadCounter = 0;

    public ThreadManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public void newThread(Module module, String name, Runnable runnable) {
        Thread thread = new Thread(runnable, name);
        ThreadBoss boss = new ThreadBoss(module, name, runnable, thread);

        threads.add(boss);
    }

    public void newThread(Module module, Runnable runnable) {
        newThread(module, "JarvisThread#" + threadCounter, runnable);
        threadCounter = threadCounter++;
    }

    public ArrayList<ThreadBoss> getThreadsByModule(Module module) {
        ArrayList<ThreadBoss> moduleThreads = new ArrayList<>();

        threads.forEach((boss -> {
            if (boss.module == module) {
                moduleThreads.add(boss);
            }
        }));

        return moduleThreads;
    }

    public void stopThreadsByModule(Module module) {
        getThreadsByModule(module).forEach(ThreadBoss::stop);
    }

    public void stop() {
        for (ThreadBoss boss : threads) {
            boss.stop();
        }
    }
}
