package sr.will.jarvis.modules.elections.rest.formManager;

import java.util.ArrayList;

public class FormGet {
    public ArrayList<Response> userResponses;

    public class Response {
        public String username;
        public ArrayList<String> votes;
    }
}
