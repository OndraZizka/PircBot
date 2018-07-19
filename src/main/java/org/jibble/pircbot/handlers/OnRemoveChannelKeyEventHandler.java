/**
 * Called when a channel key is removed.
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
 * @param key The key that was in use before the channel key was removed.
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnRemoveChannelKeyEventHandler extends AbstractEventHandler {
    private String key ;

    public OnRemoveChannelKeyEventHandler(IrcServerConnection bot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String key) {
        super.pircBot=bot;
        super.channel=channel;
        super.sourceNick=sourceNick;
        super.sourceLogin=sourceLogin;
        super.sourceHostname=sourceHostname;
        this.key = key;
    }
}
