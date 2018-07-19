/**
 * This class is called whenever someone (possibly us) quits from the
 * server.  We will only observe this if the user was in one of the
 * channels to which we are connected.
 *  <p>
 * The implementation of this method in the PircBot abstract class
 * performs no actions and may be overridden as required.
 *
 * @param sourceNick The nick of the user that quit from the server.
 * @param sourceLogin The login of the user that quit from the server.
 * @param sourceHostname The hostname of the user that quit from the server.
 * @param reason The reason given for quitting the server.
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnQuitEventHandler extends AbstractEventHandler{
    public OnQuitEventHandler(IrcServerConnection pircBot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(pircBot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }
}
