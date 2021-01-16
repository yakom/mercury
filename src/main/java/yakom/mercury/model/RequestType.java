package yakom.mercury.model;

import static yakom.mercury.Constants.UI_USER_ROLE;
import static yakom.mercury.Constants.WS_USER_ROLE;

public enum RequestType {
    UI {
        @Override
        public String getRole() {
            return UI_USER_ROLE;
        }
    },

    WS {
        @Override
        public String getRole() {
            return WS_USER_ROLE;
        }
    };

    public abstract String getRole();
}
