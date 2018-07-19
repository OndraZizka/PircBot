/**
 * Called when a user limit is set for a channel.  The number of users in
 * the channel cannot exceed this limit.
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
 * @param limit The maximum number of users that may be in this channel at the same time.
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnSetChannelLimitEventHandler extends AbstractEventHandler {
    int limit;

    public OnSetChannelLimitEventHandler(IrcServerConnection bot, String channel, String sourceNick, String sourceLogin, String sourceHostname, int limit) {
        super.pircBot = bot ;
        super.channel = channel ;
        super.sourceNick = sourceNick ;
        super.sourceLogin = sourceLogin ;
        super.sourceHostname = sourceHostname ;
        this.limit = limit ;
    }
}
