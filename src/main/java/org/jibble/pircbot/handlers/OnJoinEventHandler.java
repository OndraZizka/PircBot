/**
 * This class is called whenever someone (possibly us) joins a channel
 * which we are on.
 *  <p>
 * The implementation of this method in the PircBot abstract class
 * performs no actions and may be overridden as required.
 *
 * @param channel The channel which somebody joined.
 * @param sender The nick of the user who joined the channel.
 * @param login The login of the user who joined the channel.
 * @param hostname The hostname of the user who joined the channel.
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnJoinEventHandler extends AbstractEventHandler {
    public OnJoinEventHandler(IrcServerConnection pircBot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(pircBot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }
}
