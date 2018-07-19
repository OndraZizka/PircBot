/**
 * Called when the mode of a user is set.
 *  <p>
 * The implementation of this method in the PircBot abstract class
 * performs no actions and may be overridden as required.
 *
 * @since PircBot 1.2.0
 *
 * @param targetNick The nick that the mode operation applies to.
 * @param sourceNick The nick of the user that set the mode.
 * @param sourceLogin The login of the user that set the mode.
 * @param sourceHostname The hostname of the user that set the mode.
 * @param mode The mode that has been set.
 *
 */
package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnUserModeEventHandler extends AbstractEventHandler{
    public OnUserModeEventHandler(IrcServerConnection pircBot, String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
        super(pircBot, channel, sourceNick, sourceLogin, sourceHostname, param);
    }
}
