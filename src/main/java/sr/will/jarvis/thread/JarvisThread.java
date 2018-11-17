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

    public JarvisThread executeAt(long executeAt) {
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

    private void log(String message) {
        if (silent) {
            return;
        }

        Jarvis.getLogger().debug("{} {}", getName(), message);
    }

    private void waitForDelay(long delay) throws InterruptedException {
        if (delay <= 0) {
            return;
        }

        log("waiting for " + delay + "ms");
        sleep(delay);
        log("finished waiting");
    }

    public void run() {
        log("running!");
        int loops = 0;
        while (true) {
            try {
                // Initial execute at and delay
                if (loops == 0) {
                    // Execute at
                    waitForDelay(executeAt - System.currentTimeMillis());

                    // Delay
                    waitForDelay(delay);
                }
            } catch (InterruptedException e) {
                if (!running) {
                    log("interrupted");
                    return;
                }
            }

            // Run the content
            log("executing!");
            runnable.run();

            try {
                // Repeat if necessary
                if (!repeating) {
                    break;
                }

                waitForDelay(repeatDelay);
            } catch (InterruptedException e) {
                if (!running) {
                    log("interrupted");
                    return;
                }
            }
            loops += 1;
        }

        log("finished");
        Jarvis.getInstance().threadManager.removeThread(this);
    }

    public void kill() {
        running = false;
        interrupt();
    }
}
