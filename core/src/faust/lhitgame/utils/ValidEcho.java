package faust.lhitgame.utils;

import com.badlogic.gdx.utils.JsonValue;
import faust.lhitgame.game.echoes.enums.EchoCommandsEnum;

import java.util.Objects;

/**
 * Valid Echo extractedCommands enum
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class ValidEcho {

    public static void validate(JsonValue parsedSteps, String filename) {
        String childName = null;
        int parsedStep = 1;
        try {
            Objects.requireNonNull(parsedSteps);

            for (JsonValue s : parsedSteps) {
                childName = Objects.nonNull(s.child) ? s.child.name : "";
                inspect(s.child, filename, parsedStep);
                parsedStep++;
            }
        } catch (NullPointerException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(filename + " step " + parsedStep + " :extractedCommand " + childName + " : " + e.getMessage());
        }

    }

    private static void inspect(JsonValue child, final String filename, final int parsedStep) {
        if (Objects.nonNull(child)) {

            EchoCommandsEnum extractedCommand = EchoCommandsEnum.getFromCommandString(child.name());

            if (Objects.nonNull(extractedCommand)) {

                //get value
                Object extractedValue = null;

                //Valid extractedValue check
                if (extractedCommand.getValueClass().equals(String.class)) {
                    extractedValue = child.asString();
                } else if (extractedCommand.getValueClass().equals(Integer.class)) {
                    extractedValue = child.asInt();
                } else if (extractedCommand.getValueClass().equals(Boolean.class)) {
                    extractedValue = child.asBoolean();
                } else if (extractedCommand.getValueClass().equals(EchoCommandsEnum.class)) {
                    //Recursion for children extractedCommands
                    inspect(child.child, filename, parsedStep);
                }

                //is required check
                if (extractedCommand.isRequired() && Objects.isNull(extractedValue)) {
                    throw new NullPointerException(filename + " step " + parsedStep + " :extractedCommand " + extractedCommand.getCommandString() + " is required!");
                }

                //is required check on suncommands
                if (Objects.nonNull(extractedCommand.getSubCommands())) {
                    for (EchoCommandsEnum subCommand : extractedCommand.getSubCommands()) {
                        if (subCommand.isRequired() && !child.has(subCommand.getCommandString())) {
                            throw new NullPointerException(filename + " step " + parsedStep + " :extractedCommand " + subCommand.getCommandString() + " is required!");
                        }
                    }
                }
            }

            if (Objects.nonNull(child.next)) {
                //Recursion for children extractedCommands
                inspect(child.next, filename, parsedStep);
            }
        }
    }
}