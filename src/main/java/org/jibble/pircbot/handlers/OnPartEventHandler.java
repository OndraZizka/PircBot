/**
 * This class is called whenever someone (possibly us) parts a channel
 * which we are on.
 *  <p>
 * The implementation of this method in the PircBot abstract class
 * performs no actions and may be overridden as required.
 *
 * @param channel The channel which somebody parted from.
 * @param sender The nick of the user who parted from the channel.
 * @param login The login of the user who parted from the channel.
 * @param hostname The hostname of the user who parted from the channel.
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnPartEventHandler extends  AbstractEventHandler{
    public OnPartEventHandler(IrcServerConnection pircBot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(pircBot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }
}
