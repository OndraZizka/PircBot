/**
 * Called when we are invited to a channel by a user.
 *  <p>
 * The implementation of this method in the PircBot abstract class
 * performs no actions and may be overridden as required.
 *
 * @since PircBot 0.9.5
 *
 * @param targetNick The nick of the user being invited - should be us!
 * @param sourceNick The nick of the user that sent the invitation.
 * @param sourceLogin The login of the user that sent the invitation.
 * @param sourceHostname The hostname of the user that sent the invitation.
 * @param channel The channel that we're being invited to.
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnInviteEventHandler extends AbstractEventHandler {
    public OnInviteEventHandler(IrcServerConnection pircBot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(pircBot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }
}
