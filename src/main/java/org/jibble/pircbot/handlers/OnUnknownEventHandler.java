/**
 * This event handler class is called whenever we receive a line from the server that
 * the PircBot has not been programmed to recognise.
 *  <p>
 * The implementation of this method in the PircBot abstract class
 * performs no actions and may be overridden as required.
 *
 * @param line The raw line that was received from the server.
 */
package org.jibble.pircbot.handlers;

public class OnUnknownEventHandler extends AbstractEventHandler {
    private String line;
    public OnUnknownEventHandler(String line){
        this.line = line;
    }
}
