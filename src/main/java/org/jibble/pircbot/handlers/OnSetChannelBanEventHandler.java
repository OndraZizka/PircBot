/**
 * Called when a user (possibly us) gets banned from a channel.  Being
 * banned from a channel prevents any user with a matching hostmask from
 * joining the channel.  For this reason, most bans are usually directly
 * followed by the user being kicked :-)
 *  <p>
 * This is a type of mode change and is also passed to the onMode
 * method in the PircBot class.
 *  <p>
 * The implementation of this method in the PircBot abstract class
 * performs no actions and may be overridden as required.
 *
 * @since PircBot 0.9.5
 *
 * @param channel The channel in which the mode change took place.
 * @param sourceNick The nick of the user that performed the mode change.
 * @param sourceLogin The login of the user that performed the mode change.
 * @param sourceHostname The hostname of the user that performed the mode change.
 * @param hostmask The hostmask of the user that has been banned.
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnSetChannelBanEventHandler extends AbstractEventHandler {
    public OnSetChannelBanEventHandler(IrcServerConnection pircBot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String hostmask) {
        super(pircBot,channel,sourceNick,sourceLogin,sourceHostname,hostmask);
    }
}
