package sr.will.jarvis.modules.assistance.rest.urbandictionary;

import java.util.ArrayList;

public class Definition {
    public ArrayList<String> tags;
    public String result_type;
    public ArrayList<Word> list;

    public class Word {
        public long defid;
        public String word;
        public String author;
        public String permalink;
        public String definition;
        public String example;
        public int thumbs_up;
        public int thumbs_down;
        public String current_vote;
    }
}
