package faust.lhitgame.utils;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import faust.lhitgame.game.echoes.enums.EchoCommandsEnum;
import faust.lhitgame.game.echoes.enums.EchoesActorType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Valid Echo extractedCommands enum
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class ValidEcho {

    /**
     * Validate a set of steps
     *
     * @param parsedSteps steps to validate
     * @param filename    filename of the extracted step (readonly, used for logs)
     * @throws EchoScriptValidationException
     */
    public static void validate(JsonValue parsedSteps, String filename) throws EchoScriptValidationException {
        int parsedStepNumber = 1;
        try {
            Objects.requireNonNull(parsedSteps);

            for (JsonValue s : parsedSteps) {
                inspect(s.child, filename, parsedStepNumber);
                parsedStepNumber++;
            }
        } catch (EchoScriptValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new EchoScriptValidationException(e.getMessage());
        }

    }

    /**
     * Validate a single step
     *
     * @param child            step to validate
     * @param filename         filename of the extracted step (readonly, used for logs)
     * @param parsedStepNumber (readonly, used for logs)
     * @throws EchoScriptValidationException
     */
    private static void inspect(JsonValue child, final String filename, final int parsedStepNumber) throws EchoScriptValidationException {

        try {
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
                        inspect(child.child, filename, parsedStepNumber);
                    }

                    //is required check
                    if (extractedCommand.isRequired() && Objects.isNull(extractedValue)) {
                        throw new NullPointerException(extractedCommand.getCommandString() + " is required!");
                    }

                    //is required check on suncommands
                    if (Objects.nonNull(extractedCommand.getSubCommands())) {
                        for (EchoCommandsEnum subCommand : extractedCommand.getSubCommands()) {
                            if (subCommand.isRequired() && !child.has(subCommand.getCommandString())) {
                                throw new NullPointerException(subCommand.getCommandString() + " is required!");
                            }
                        }
                    }
                }

                if (Objects.nonNull(child.next)) {
                    //Recursion for children extractedCommands
                    inspect(child.next, filename, parsedStepNumber);
                }
            }
        } catch (EchoScriptValidationException e) {
            //Just throws its
            throw e;
        } catch (Exception e) {
            //Prepare nice output!
            String stepname = (Objects.isNull(child.parent) || Objects.isNull(child.parent.name)) ? child.name : child.parent.name + " -> " + child.name;
            String exceptionClassName = e.getClass().getSimpleName();
            throw new EchoScriptValidationException(filename + " step " + parsedStepNumber + " -> extractedCommand " + stepname + " : " + exceptionClassName + " " + e.getMessage());
        }
    }

    /**
     * Main usable for validation outside of game logic, just using this class as Java application
     *
     * @param args
     * @throws IOException
     * @throws EchoScriptValidationException
     */
    public static void main(String[] args) {

        JsonReader reader = new JsonReader();
        try {

        for (EchoesActorType echoesActorType : EchoesActorType.values()) {
            //Read file
            List<String> lines = Files.readAllLines(Paths.get("E:/Repositories/LHIP/core/assets/scripts/" + echoesActorType.getFilename()), Charset.defaultCharset());
            String content = lines.stream().collect(Collectors.joining("\n"));
            // start validate
            JsonValue parsedSteps = reader.parse(content).get("steps");
            validate(parsedSteps, echoesActorType.getFilename());
        }

        System.out.println("------------------------------------------------------");
        System.out.println("All Echoes scripts are valid!");
        System.out.println("------------------------------------------------------");

        }catch (Exception e){
            System.out.println("------------------------------------------------------");
            System.out.println(e.getMessage());
            System.out.println("------------------------------------------------------");
        }
    }
}