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
    private boolean valid = false;

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
                if (command_parts[8] != null || !parseLandmark(command_parts[8]))
                    throw new Exception(String.format(plugin.getString("INVALID_LANDMARK"), command_parts[8], getCommandFormat(1)));
            }
            else {
                throw new Exception(String.format(plugin.getString("INVALID_NEW_ARGS"), getCommandFormat(1)));
            }
        }

        // Close contract posting
        else if(action_str.contentEquals(plugin.getString("COMMAND_CLOSE_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_CLOSE"))) {
            action = 2;
        }

        // Open contract posting
        else if(action_str.contentEquals(plugin.getString("COMMAND_OPEN_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_OPEN"))) {
            action = 3;
        }

        // Remove contract posting
        else if(action_str.contentEquals(plugin.getString("COMMAND_REMOVE_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_REMOVE"))) {
            action = 4;
        }

        // Apply for a contract
        else if(action_str.contentEquals(plugin.getString("COMMAND_APPLY_SHORT")) || action_str.contentEquals(plugin.getString("COMMAND_APPLY"))) {
            action = 5;
        }

        // Command not recognized
        else {
            throw new Exception(String.format(plugin.getString("INVALID_COMMAND"), getCommandFormat(0)));
        }
    }

    public int getAction() {
        return action;
    }

    public boolean isValid() {
        return valid;
    }

    private boolean parseType(String str) {
        if ( str == "0" || str == plugin.getString("TYPE_DEMOLITION")) {
            type = 0;
        }
        // Build
        else if(str == "1" || str == plugin.getString("TYPE_BUILD")) {
            type = 1;
        }
        // Protection
        else if(str == "2" || str == plugin.getString("TYPE_PROTECTION")) {
            type = 2;
        }
        // Grief
        else if(str == "3" || str == plugin.getString("TYPE_GRIEF")) {
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
        }
        return "";
    }

    public ArrayList getCommandFormats() {
        return getCommandFormats(false);
    }

    public ArrayList getCommandFormats(boolean full) {
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