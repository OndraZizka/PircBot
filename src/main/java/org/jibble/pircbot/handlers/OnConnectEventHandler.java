/**
 * This class event handler is called once the PircBot has successfully connected to
 * the IRC server.
 *  <p>
 * The implementation of this method in the PircBot abstract class
 * performs no actions and may be overridden as required.
 *
 * @since PircBot 0.9.6
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnConnectEventHandler extends AbstractEventHandler {

    public OnConnectEventHandler(IrcServerConnection bot){
        super.pircBot = bot;
    }
}
