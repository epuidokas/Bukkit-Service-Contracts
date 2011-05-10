package com.bukkit.epuidokas.ServiceContracts;

import java.util.*;

/**
 *
 * @author ep
 */
public class ServiceContractsCommand {

    private ServiceContractsPlugin plugin;
    private int action = 0;
    private int type = 0;
    private int openings = 0;
    private int length = 0;
    private int payment = 0;
    private int x = 0;
    private int z = 0;
    private String landmark = "";
    private String message = "";
    private String player = "";
    private String contract = "";

    public ServiceContractsCommand(ServiceContractsPlugin instance, String command) throws Exception{

        plugin = instance;
        String[] command_parts = command.split(" ", 10);
        String action_str = (command_parts.length > 1) ?  command_parts[1] : "";
        
        // Help
        if(action_str.contentEquals("-" + plugin.getString("COMMAND_HELP_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_HELP"))) {
            action = 0;
        }
        // Create new contract posting
        else if(action_str.contentEquals("-" + plugin.getString("COMMAND_NEW_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_NEW"))) {
            action = 1;
            if (command_parts.length > 7 & command_parts.length < 10) {
                if (!parseType(command_parts[2]))
                    throw new Exception(String.format(plugin.getString("INVALID_TYPE"), command_parts[2], getCommandFormat(1)));
                if (!parseOpenings(command_parts[3]))
                    throw new Exception(String.format(plugin.getString("INVALID_NUM_OPENINGS"), command_parts[3], getCommandFormat(1)));
                if (!parseLength(command_parts[4]))
                    throw new Exception(String.format(plugin.getString("INVALID_LENGTH"), command_parts[4], getCommandFormat(1)));
                if (!parsePayment(command_parts[5]))
                    throw new Exception(String.format(plugin.getString("INVALID_PAYMENT"), command_parts[5], getCommandFormat(1)));
                if (!parseX(command_parts[6]))
                    throw new Exception(String.format(plugin.getString("INVALID_X"), command_parts[6], getCommandFormat(1)));
                if (!parseZ(command_parts[7]))
                    throw new Exception(String.format(plugin.getString("INVALID_Z"), command_parts[7], getCommandFormat(1)));
                if (command_parts.length == 9 && !parseLandmark(command_parts[8]))
                    throw new Exception(String.format(plugin.getString("INVALID_LANDMARK"), command_parts[8], getCommandFormat(1)));
            }
            else {
                throw new Exception(String.format(plugin.getString("INVALID_NEW_ARGS"), getCommandFormat(1)));
            }
        }

        // Close contract posting
        else if(action_str.contentEquals("-" + plugin.getString("COMMAND_CLOSE_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_CLOSE"))) {
            action = 2;
        }

        // Open contract posting
        else if(action_str.contentEquals("-" + plugin.getString("COMMAND_OPEN_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_OPEN"))) {
            action = 3;
        }

        // Remove contract posting
        else if(action_str.contentEquals("-" + plugin.getString("COMMAND_REMOVE_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_REMOVE"))) {
            action = 4;
        }

        // Apply for a contract
        else if(action_str.contentEquals("-" + plugin.getString("COMMAND_APPLY_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_APPLY"))) {
            action = 5;
            if (command_parts.length > 2) {
                command_parts = command.split(" ", 3);
                if (!parseMessage(command_parts[2]))
                    throw new Exception(String.format(plugin.getString("INVALID_MESSAGE")));
            }
        }

        // Hire applicant
        else if(action_str.contentEquals("-" + plugin.getString("COMMAND_EMPLOY_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_EMPLOY"))) {
            action = 6;
            if (command_parts.length > 2) {
                command_parts = command.split(" ", 3);
                if (!parseContract(command_parts[2]))
                    throw new Exception(String.format(plugin.getString("INVALID_CONTRACT")));
                if (!parsePlayer(command_parts[3]))
                    throw new Exception(String.format(plugin.getString("INVALID_PLAYER")));
            }
        }

        // Fire contractor
        else if(action_str.contentEquals("-" + plugin.getString("COMMAND_FIRE_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_FIRE"))) {
            action = 7;
        }

        // Start paying contractor
        else if(action_str.contentEquals("-" + plugin.getString("COMMAND_START_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_START"))) {
            action = 8;
        }

        // Pause paying contractor
        else if(action_str.contentEquals("-" + plugin.getString("COMMAND_PAUSE_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_PAUSE"))) {
            action = 9;
        }

        // Quit current contract
        else if(action_str.contentEquals("-" + plugin.getString("COMMAND_QUIT_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_QUIT"))) {
            action = 10;
        }

        // Modify contract
        else if(action_str.contentEquals("-" + plugin.getString("COMMAND_MODIFY_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_MODIFY"))) {
            action = 11;
        }

        // Get info on contract
        else if(action_str.contentEquals("-" + plugin.getString("COMMAND_INFO_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_INFO"))) {
            action = 12;
        }


        // Command not recognized
        else {
            throw new Exception(String.format(plugin.getString("INVALID_COMMAND"), getCommandFormat(0)));
        }
    }

    public int getAction() {
        return action;
    }

    public int getType() {
        return type;
    }

    public int getOpenings() {
        return openings;
    }

    public int getLength() {
        return length;
    }

    public int getPayment() {
        return payment;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public String getLandmark() {
        return landmark;
    }

    public String getMessage() {
        return message;
    }

    public String getPlayer() {
        return player;
    }

    public String getContract() {
        return player;
    }

    private boolean parseType(String str) {
        if ( str.contentEquals("0") || str.contentEquals(plugin.getString("TYPE_0"))) {
            type = 0;
        }
        // Build
        else if(str.contentEquals("1") || str.contentEquals(plugin.getString("TYPE_1"))) {
            type = 1;
        }
        // Protection
        else if(str.contentEquals("2") || str.contentEquals(plugin.getString("TYPE_2"))) {
            type = 2;
        }
        // Grief
        else if(str.contentEquals("3") || str.contentEquals(plugin.getString("TYPE_3"))) {
            type = 3;
        }
        else {
            return false;
        }
        return true;
    }

    private boolean parseOpenings(String str) {

        try {
            openings = Integer.parseInt(str);
        } catch (Exception e) {
            return false;
        }

        if (openings < 1 || openings > 10) {
            return false;
        }
        
        return true;
    }

    private boolean parseLength(String str) {
        try {
            length = Integer.parseInt(str);
        } catch (Exception e) {
            return false;
        }

        if (length < 5 || length > 180) {
            return false;
        }
        return true;
    }

    private boolean parsePayment(String str) {
        try {
            payment = Integer.parseInt(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean parseX(String str) {
        try {
            x = Integer.parseInt(str);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private boolean parseZ(String str) {
        try {
            z = Integer.parseInt(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean parseLandmark(String str) {
        landmark = str;
        return true;
    }

    private boolean parseMessage(String str) {
        message = str;
        return true;
    }

    private boolean parsePlayer(String str) {
        player = str;
        return true;
    }

    private boolean parseContract(String str) {
        contract = str;
        return true;
    }

    public String getCommandFormat() {
        return getCommandFormat(action, false);
    }

    public String getCommandFormat(boolean simple) {
        return getCommandFormat(action, simple);
    }

    public String getCommandFormat(int action_val) {
        return getCommandFormat(action_val, false);
    }

    public String getCommandFormat(int action_val, boolean full) {
        switch(action_val){
            // HELP
            case 0:
                if (!full) {
                    return String.format("/%s -%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_HELP_SHORT"));
                }
                else {
                    return String.format("/%s -%s|%s [<%s>]",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_HELP_SHORT"),
                                         plugin.getString("COMMAND_HELP"),
                                         plugin.getString("HELP_COMMAND_ACTION"));
                }
            // NEW
            case 1:
                if (!full) {
                    return String.format("/%s -%s <%s> <%s> <%s> <%s> <%s> <%s>",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_NEW_SHORT"),
                                         plugin.getString("NEW_COMMAND_TYPE"),
                                         plugin.getString("NEW_COMMAND_NUM_OPENINGS"),
                                         plugin.getString("NEW_COMMAND_LENGTH"),
                                         plugin.getString("NEW_COMMAND_PAYMENT"),
                                         plugin.getString("NEW_COMMAND_X"),
                                         plugin.getString("NEW_COMMAND_Z"));
                }
                else {
                    return String.format("/%s -%s|%s <%s> <%s> <%s> <%s> <%s> <%s> [<%s>]",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_NEW_SHORT"),
                                         plugin.getString("COMMAND_NEW"),
                                         plugin.getString("NEW_COMMAND_TYPE"),
                                         plugin.getString("NEW_COMMAND_NUM_OPENINGS"),
                                         plugin.getString("NEW_COMMAND_LENGTH"),
                                         plugin.getString("NEW_COMMAND_PAYMENT"),
                                         plugin.getString("NEW_COMMAND_X"),
                                         plugin.getString("NEW_COMMAND_Z"),
                                         plugin.getString("NEW_COMMAND_LANDMARK"));
                }
            // CLOSE
            case 2:
                if (!full) {
                    return String.format("/%s -%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_CLOSE_SHORT"));
                }
                else {
                    return String.format("/%s -%s|%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_CLOSE_SHORT"),
                                         plugin.getString("COMMAND_CLOSE"));
                }
            // OPEN
            case 3:
                if (!full) {
                    return String.format("/%s -%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_OPEN_SHORT"));
                }
                else {
                    return String.format("/%s -%s|%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_OPEN_SHORT"),
                                         plugin.getString("COMMAND_OPEN"));
                }
            // REMOVE
            case 4:
                if (!full) {
                    return String.format("/%s -%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_REMOVE_SHORT"));
                }
                else {
                    return String.format("/%s -%s|%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_REMOVE_SHORT"),
                                         plugin.getString("COMMAND_REMOVE"));
                }
            // APPLY
            case 5:
                if (!full) {
                    return String.format("/%s -%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_APPLY_SHORT"));
                }
                else {
                    return String.format("/%s -%s|%s [<%s>]",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_APPLY_SHORT"),
                                         plugin.getString("COMMAND_APPLY"),
                                         plugin.getString("APPLY_COMMAND_MESSAGE"));
                }
            // EMPLOY
            case 6:
                if (!full) {
                    return String.format("/%s -%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_EMPLOY_SHORT"));
                }
                else {
                    return String.format("/%s -%s|%s <%s> <%s>",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_EMPLOY_SHORT"),
                                         plugin.getString("COMMAND_EMPLOY"),
                                         plugin.getString("EMPLOY_COMMAND_CONTRACT"),
                                         plugin.getString("EMPLOY_COMMAND_PLAYER"));
                }
            // FIRE
            case 7:
                if (!full) {
                    return String.format("/%s -%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_FIRE_SHORT"));
                }
                else {
                    return String.format("/%s -%s|%s <%s> <%s>",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_FIRE_SHORT"),
                                         plugin.getString("COMMAND_FIRE"),
                                         plugin.getString("FIRE_COMMAND_CONTRACT"),
                                         plugin.getString("FIRE_COMMAND_PLAYER"));
                }
            // START
            case 8:
                if (!full) {
                    return String.format("/%s -%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_START_SHORT"));
                }
                else {
                    return String.format("/%s -%s|%s <%s> <%s>",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_START_SHORT"),
                                         plugin.getString("COMMAND_START"),
                                         plugin.getString("START_COMMAND_CONTRACT"),
                                         plugin.getString("START_COMMAND_PLAYER"));
                }
            // PAUSE
            case 9:
                if (!full) {
                    return String.format("/%s -%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_PAUSE_SHORT"));
                }
                else {
                    return String.format("/%s -%s|%s <%s> <%s>",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_PAUSE_SHORT"),
                                         plugin.getString("COMMAND_PAUSE"),
                                         plugin.getString("PAUSE_COMMAND_CONTRACT"),
                                         plugin.getString("PAUSE_COMMAND_PLAYER"));
                }
            // QUIT
            case 10:
                if (!full) {
                    return String.format("/%s -%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_QUIT_SHORT"));
                }
                else {
                    return String.format("/%s -%s|%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_QUIT_SHORT"),
                                         plugin.getString("COMMAND_QUIT"));
                }
            // MODIFY
            case 11:
                if (!full) {
                    return String.format("/%s -%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_MODIFY_SHORT"));
                }
                else {
                    return String.format("/%s -%s|%s <%s> <%s> <%s>",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_MODIFY_SHORT"),
                                         plugin.getString("COMMAND_MODIFY"),
                                         plugin.getString("MODIFY_COMMAND_NUM_OPENINGS"),
                                         plugin.getString("MODIFY_COMMAND_LENGTH"),
                                         plugin.getString("MODIFY_COMMAND_PAYMENT"));
                }
            // INFO
            case 12:
                if (!full) {
                    return String.format("/%s -%s",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_INFO_SHORT"));
                }
                else {
                    return String.format("/%s -%s|%s [<%s>]",
                                         plugin.getString("COMMAND"),
                                         plugin.getString("COMMAND_INFO_SHORT"),
                                         plugin.getString("COMMAND_INFO"),
                                         plugin.getString("INFO_COMMAND_CONTRACT"));
                }
        }
        return "";
    }

    public ArrayList<String> getCommandFormats() {
        return getCommandFormats(false);
    }

    public ArrayList<String> getCommandFormats(boolean full) {
        ArrayList<String> command_formats = new ArrayList<String>();
        String command_format;
        int i = 1;
        while(!(command_format = getCommandFormat(i, full)).isEmpty()) {
            command_formats.add(command_format);
            i++;
        }
        return command_formats;
    }
}
