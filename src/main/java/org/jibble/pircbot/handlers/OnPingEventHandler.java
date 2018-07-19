/**
 * This event handler is called whenever we receive a PING request from another
 * user.
 *  <p>
 * This abstract implementation responds correctly, so if you override this
 * method, be sure to either mimic its functionality or to call
 * super.onPing(...);
 *
 * @param sourceNick The nick of the user that sent the PING request.
 * @param sourceLogin The login of the user that sent the PING request.
 * @param sourceHostname The hostname of the user that sent the PING request.
 * @param target The target of the PING request, be it our nick or a channel name.
 * @param pingValue The value that was supplied as an argument to the PING command.
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;
import org.jibble.pircbot.api.IIrcEventHandlerBase;

public class OnPingEventHandler implements IIrcEventHandlerBase {
    IrcServerConnection pircBot;
    String sourceNick;
    String pingValue;


    public OnPingEventHandler(IrcServerConnection pircBot, String sourceNick, String pingValue) {
        this.pircBot = pircBot;
        this.sourceNick = sourceNick;
        this.pingValue = pingValue;
    }


    /**
     * This method is called whenever we receive a PING request from another
     * user.
     *  <p>
     * This abstract implementation responds correctly, so if you override this
     * method, be sure to either mimic its functionality or to call
     * super.onPing(...);
     */
    @Override
    public void execute() {
        pircBot.sendRawLine("NOTICE " + sourceNick + " :\u0001PING " + pingValue + "\u0001");
    }
}
