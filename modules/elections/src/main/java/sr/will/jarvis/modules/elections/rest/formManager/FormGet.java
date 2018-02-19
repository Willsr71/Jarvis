package sr.will.jarvis.modules.elections.rest.formManager;

import java.util.ArrayList;

public class FormGet {
    public ArrayList<Response> responses;

    public class Response {
        public String discriminator;
        public String auth_token;
        public ArrayList<String> votes;
    }
}
