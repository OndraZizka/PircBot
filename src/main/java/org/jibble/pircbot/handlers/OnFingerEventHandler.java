/**
 * This class is called whenever we receive a FINGER request.
 *  <p>
 * This abstract implementation responds correctly, so if you override this
 * method, be sure to either mimic its functionality or to call
 * super.onFinger(...);
 *
 * @param sourceNick The nick of the user that sent the FINGER request.
 * @param sourceLogin The login of the user that sent the FINGER request.
 * @param sourceHostname The hostname of the user that sent the FINGER request.
 * @param target The target of the FINGER request, be it our nick or a channel name.
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;
import org.jibble.pircbot.api.IIrcEventHandlerBase;

public class OnFingerEventHandler extends AbstractEventHandler {

    public OnFingerEventHandler(IrcServerConnection bot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(bot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }

    /**
     * This method is called whenever we receive a FINGER request.
     *  <p>
     * This abstract implementation responds correctly, so if you override this
     * method, be sure to either mimic its functionality or to call
     * super.onFinger(...);
     */
    @Override
    public void execute() {
        pircBot.sendRawLine("NOTICE " + sourceNick + " :\u0001FINGER " + pircBot.getFinger() + "\u0001");
    }
}
