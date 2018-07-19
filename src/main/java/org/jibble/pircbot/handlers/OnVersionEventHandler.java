/**
 * This event handler class is called whenever we receive a VERSION request.
 * This abstract implementation responds with the PircBot's _version string,
 * so if you override this method, be sure to either mimic its functionality
 * or to call super.onVersion(...);
 *
 * @param sourceNick The nick of the user that sent the VERSION request.
 * @param sourceLogin The login of the user that sent the VERSION request.
 * @param sourceHostname The hostname of the user that sent the VERSION request.
 * @param target The target of the VERSION request, be it our nick or a channel name.
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;
import org.jibble.pircbot.api.IIrcEventHandlerBase;

public class OnVersionEventHandler implements IIrcEventHandlerBase {
    private IrcServerConnection pircBot;
    private String sourceNick;
    String sourceLogin;
    String sourceHostname;
    String target;

    public OnVersionEventHandler(IrcServerConnection pircBot, String sourceNick, String sourceLogin, String sourceHostname, String target){
        this.pircBot=pircBot;
        this.sourceNick=sourceNick;
        this.sourceLogin=sourceLogin;
        this.sourceHostname=sourceHostname;
        this.target=target;
    }

    /**
     * This method is called whenever we receive a VERSION request.
     * This abstract implementation responds with the PircBot's _version string,
     * so if you override this method, be sure to either mimic its functionality
     * or to call super.onVersion(...);
     */
    @Override
    public void execute() {
        pircBot.sendRawLine("NOTICE " + sourceNick + " :\u0001VERSION " + pircBot.getVersion() + "\u0001");
    }



}
