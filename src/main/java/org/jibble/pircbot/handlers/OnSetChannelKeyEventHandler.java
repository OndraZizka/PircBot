/**
 * Called when a channel key is set.  When the channel key has been set,
 * other users may only join that channel if they know the key.  Channel keys
 * are sometimes referred to as passwords.
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
 * @param key The new key for the channel.
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnSetChannelKeyEventHandler extends AbstractEventHandler {
    public OnSetChannelKeyEventHandler(IrcServerConnection bot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(bot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }
}
