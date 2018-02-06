package sr.will.jarvis.manager;

import sr.will.jarvis.module.Module;

public class ThreadBoss {
    public Module module;
    public String name;
    public Runnable runnable;
    public Thread thread;

    public ThreadBoss(Module module, String name, Runnable runnable, Thread thread) {
        this.module = module;
        this.name = name;
        this.runnable = runnable;
        this.thread = thread;
        thread.run();
    }

    public void stop() {
        thread.interrupt();
    }
}
