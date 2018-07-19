/**
 * This event handler class is called whenever we receive a TIME request.
 *  <p>
 * This abstract implementation responds correctly, so if you override this
 * method, be sure to either mimic its functionality or to call
 * super.onTime(...);
 *
 * @param sourceNick The nick of the user that sent the TIME request.
 * @param sourceLogin The login of the user that sent the TIME request.
 * @param sourceHostname The hostname of the user that sent the TIME request.
 * @param target The target of the TIME request, be it our nick or a channel name.
 */
package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

import java.util.Date;

public class OnTimeEventHandler extends AbstractEventHandler {


    public OnTimeEventHandler(IrcServerConnection bot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(bot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }

    /**
     * This method is called whenever we receive a TIME request.
     *  <p>
     * This abstract implementation responds correctly, so if you override this
     * method, be sure to either mimic its functionality or to call
     * super.onTime(...);
     */
    @Override
    public void execute() {
        pircBot.sendRawLine("NOTICE " + sourceNick + " :\u0001TIME " + new Date().toString() + "\u0001");

    }
}
