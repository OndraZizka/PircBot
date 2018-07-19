/**
 * Called when topic protection is enabled for a channel.  Topic protection
 * means that only operators in a channel may change the topic.
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
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnSetTopicProtectionEventHandler extends AbstractEventHandler{
    public OnSetTopicProtectionEventHandler(IrcServerConnection pircBot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(pircBot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }
}
