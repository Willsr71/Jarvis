package sr.will.jarvis.manager;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;

public class JarvisThread extends Thread {
    public Module module = null;
    private Runnable runnable;
    private long delay = 0;
    private boolean repeating = false;
    private long repeatDelay = 0;
    private boolean running = true;

    public JarvisThread() {
        Jarvis.getInstance().threadManager.addThread(this);
    }

    public JarvisThread module(Module module) {
        this.module = module;
        return this;
    }

    public JarvisThread name(String name) {
        setName(name);
        return this;
    }

    public JarvisThread runnable(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    public JarvisThread delay(long delay) {
        this.delay = delay;
        return this;
    }

    public JarvisThread repeat(boolean repeating, long repeatDelay) {
        this.repeating = repeating;
        this.repeatDelay = repeatDelay;
        return this;
    }

    private synchronized void waitForDelay(long delay) throws InterruptedException {
        Jarvis.debug("Thread " + getName() + " waiting for " + delay + "ms");
        wait(delay);
        Jarvis.debug("Thread " + getName() + " finished waiting");
    }

    public void run() {
        Jarvis.debug("Thread " + getName() + " running!");
        int loops = 0;
        while (true) {
            try {
                // Initial delay
                if (loops == 0) {
                    long sleepTime = delay - System.currentTimeMillis();
                    if (sleepTime > 0) {
                        waitForDelay(sleepTime);
                    }
                }

                // Run the content
                Jarvis.debug("Thread " + getName() + " executing!");
                runnable.run();

                // Repeat if necessary
                if (!repeating) {
                    break;
                }
                waitForDelay(repeatDelay);
            } catch (InterruptedException e) {
                if (!running) {
                    Jarvis.debug("Thread " + getName() + " interrupted");
                    break;
                }
            }
            loops += 1;
        }
        Jarvis.debug("Thread " + getName() + " finished");
        Jarvis.getInstance().threadManager.removeThread(this);
    }

    public synchronized void kill() {
        running = false;
        notify();
    }
}
