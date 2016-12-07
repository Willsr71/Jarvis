package sr.will.jarvis.manager;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.entity.Mute;

import java.util.ArrayList;

public class MuteManager {
    private Jarvis jarvis;

    private ArrayList<Mute> mutes = new ArrayList<>();

    public MuteManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }
}
