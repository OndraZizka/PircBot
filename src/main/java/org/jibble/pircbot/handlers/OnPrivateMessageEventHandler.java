/**
 * This class is called whenever a private message is sent to the PircBot.
 *  <p>
 * The implementation of this method in the PircBot abstract class
 * performs no actions and may be overridden as required.
 *
 * @param sender The nick of the person who sent the private message.
 * @param login The login of the person who sent the private message.
 * @param hostname The hostname of the person who sent the private message.
 * @param message The actual message.
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnPrivateMessageEventHandler extends AbstractEventHandler {
    public OnPrivateMessageEventHandler(IrcServerConnection pircBot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(pircBot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }
}
