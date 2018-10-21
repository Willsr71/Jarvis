package sr.will.jarvis.stats;

import java.util.concurrent.Callable;

public class Stat {
    public String type;
    public String name;
    public Callable<Integer> value;

    public Stat(String type, String name, Callable<Integer> value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }
}
