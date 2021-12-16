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
        try {
            Objects.requireNonNull(parsedSteps);

            for (JsonValue s : parsedSteps) {

                inspect(s.child,filename);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(filename, e);
        }

    }

    private static void inspect(JsonValue child, String filename) {
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
                } else if (extractedCommand.getValueClass().equals(EchoCommandsEnum.class)) {
                    //Recursion for children extractedCommands
                    inspect(child.child, filename);
                }

                //is required check
                if (extractedCommand.isRequired() && Objects.isNull(extractedValue)) {
                    throw new NullPointerException(filename + ":extractedCommand " + extractedCommand.getCommandString() + " is required!");
                }
            }

            if (Objects.nonNull(child.next)) {
                //Recursion for children extractedCommands
                inspect(child.next, filename);
            }
        }
    }
}