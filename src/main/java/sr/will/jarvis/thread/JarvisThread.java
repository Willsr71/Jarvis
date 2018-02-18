package sr.will.jarvis.thread;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;

public class JarvisThread extends Thread {
    public Module module;
    private Runnable runnable;
    private long executeAt = 0;
    private long delay = 0;
    private boolean repeating = false;
    private long repeatDelay = 0;
    private boolean silent = false;
    private boolean running = true;

    public JarvisThread(Module module, Runnable runnable) {
        this.module = module;
        this.runnable = runnable;
        Jarvis.getInstance().threadManager.addThread(this);
    }

    public JarvisThread name(String name) {
        setName(getName() + " " + name);
        return this;
    }

    public JarvisThread executeAt(long time) {
        this.executeAt = executeAt;
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

    public JarvisThread silent(boolean silent) {
        this.silent = silent;
        return this;
    }

    private synchronized void log(String message) {
        if (silent) {
            return;
        }

        Jarvis.debug(message);
    }

    private synchronized void waitForDelay(long delay) throws InterruptedException {
        if (delay == 0) {
            return;
        }

        log(getName() + " waiting for " + delay + "ms");
        wait(delay);
        log(getName() + " finished waiting");
    }

    public void run() {
        log(getName() + " running!");
        int loops = 0;
        while (true) {
            try {
                // Initial execute at and delay
                if (loops == 0) {
                    // Execute at
                    long sleepTime = executeAt - System.currentTimeMillis();
                    if (sleepTime > 0) {
                        waitForDelay(sleepTime);
                    }

                    // Delay
                    waitForDelay(delay);
                }
            } catch (InterruptedException e) {
                if (!running) {
                    log(getName() + " interrupted");
                    return;
                }
            }

            // Run the content
            log(getName() + " executing!");
            runnable.run();

            try {
                // Repeat if necessary
                if (!repeating) {
                    break;
                }

                waitForDelay(repeatDelay);
            } catch (InterruptedException e) {
                if (!running) {
                    log(getName() + " interrupted");
                    return;
                }
            }
            loops += 1;
        }

        log(getName() + " finished");
        Jarvis.getInstance().threadManager.removeThread(this);
    }

    public synchronized void kill() {
        running = false;
        notify();
    }
}
