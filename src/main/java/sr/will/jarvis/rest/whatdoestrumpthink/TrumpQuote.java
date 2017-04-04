package sr.will.jarvis.rest.whatdoestrumpthink;

import java.util.ArrayList;

public class TrumpQuote {
    public String message;
    public Attributes nlp_attributes;

    public class Attributes {
        public ArrayList<ArrayList<String>> quote_structure;
    }
}
