package faust.lhitgame.utils;

/**
 * @author Jacopo "Faust" Buttiglieri
 *
 * Custom exception for Echoes script validation
 */
public class EchoScriptValidationException extends Exception{
    public EchoScriptValidationException(String message) {
        super(message);
    }
}
