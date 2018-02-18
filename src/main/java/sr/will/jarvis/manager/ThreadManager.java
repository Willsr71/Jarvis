package sr.will.jarvis.manager;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.thread.JarvisThread;

import java.util.ArrayList;

public class ThreadManager {
    public Jarvis jarvis;

    public ArrayList<JarvisThread> threads = new ArrayList<>();

    public ThreadManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public void addThread(JarvisThread thread) {
        threads.add(thread);
    }

    public void removeThread(JarvisThread thread) {
        threads.remove(thread);
    }

    public ArrayList<JarvisThread> getThreadsByModule(Module module) {
        ArrayList<JarvisThread> moduleThreads = new ArrayList<>();

        threads.forEach((thread -> {
            if (thread.module == module) {
                moduleThreads.add(thread);
            }
        }));

        return moduleThreads;
    }

    public void stopThreadsByModule(Module module) {
        getThreadsByModule(module).forEach(JarvisThread::kill);
    }

    public void stop() {
        threads.forEach(JarvisThread::kill);
    }
}
