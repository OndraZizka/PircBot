/**
 * This event handler class is called whenever we receive a PING request from another
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

public class OnServerPingEventHandler implements IIrcEventHandlerBase {
    private IrcServerConnection pircBot;
    private String response;

    public OnServerPingEventHandler(IrcServerConnection pircBot, String response){
        this.pircBot = pircBot;
        this.response = response;
    }
    /**
     * The actions to perform when a PING request comes from the server.
     *  <p>
     * This sends back a correct response, so if you override this method,
     * be sure to either mimic its functionality or to call
     * super.onServerPing(response);
     */

    @Override
    public void execute() {
        pircBot.sendRawLine("PONG " + response);
    }
}
