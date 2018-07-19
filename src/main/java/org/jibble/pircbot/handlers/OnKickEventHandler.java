/**
 * This class is called whenever someone (possibly us) is kicked from
 * any of the channels that we are in.
 *  <p>
 * The implementation of this method in the PircBot abstract class
 * performs no actions and may be overridden as required.
 *
 * @param channel The channel from which the recipient was kicked.
 * @param kickerNick The nick of the user who performed the kick.
 * @param kickerLogin The login of the user who performed the kick.
 * @param kickerHostname The hostname of the user who performed the kick.
 * @param recipientNick The unfortunate recipient of the kick.
 * @param reason The reason given by the user who performed the kick.
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnKickEventHandler extends AbstractEventHandler {
    String recipient ;
    String line ;

    public OnKickEventHandler(IrcServerConnection pircBot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient, String line) {
        super.pircBot = pircBot;
        super.channel = channel;
        super.sourceNick = sourceNick;
        super.sourceLogin = sourceLogin;
        super.sourceHostname = sourceHostname;
        this.recipient = recipient;
        this.line = line;
    }
}
