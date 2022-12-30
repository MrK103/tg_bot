package by.mrk.service.enums;

public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    GET_ONLINE("/get_online"),
    CANCEL("/cansel"),
    START("/start"),
    GET_ALL_PHOTO_LINK("/get_all_photo"),
    GET_ALL_DOC_LINK("/get_all_doc");


    private final String cmd;

    ServiceCommands(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }

    public static ServiceCommands fromValue(String val) {
        for (ServiceCommands commands : ServiceCommands.values()) {
            if (commands.cmd.equals(val)) {
                return commands;
            }
        }
        return null;
    }
//    public  boolean equals(String cmd){
//        return this.toString().equals(cmd);
//    }
}
