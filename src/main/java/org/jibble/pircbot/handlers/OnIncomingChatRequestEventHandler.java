/**
 * This class will be called whenever a DCC Chat request is received.
 * This means that a client has requested to chat to us directly rather
 * than via the IRC server. This is useful for sending many lines of text
 * to and from the pircBot without having to worry about flooding the server
 * or any operators of the server being able to "spy" on what is being
 * said. This abstract implementation performs no action, which means
 * that all DCC CHAT requests will be ignored by default.
 *  <p>
 * If you wish to accept the connection, then you may override this
 * method and call the accept() method on the DccChat object, which
 * connects to the sender of the chat request and allows lines to be
 * sent to and from the pircBot.
 *  <p>
 * Your pircBot must be able to connect directly to the user that sent the
 * request.
 *  <p>
 * Example:
 * <pre> public void onIncomingChatRequest(DccChat chat) {
 *     try {
 *         // Accept all chat, whoever it's from.
 *         chat.accept();
 *         chat.sendLine("Hello");
 *         String response = chat.readLine();
 *         chat.close();
 *     }
 *     catch (IOException e);
 * }</pre>
 *
 * Each time this method is called, it is called from within a new Thread
 * so that multiple DCC CHAT sessions can run concurrently.
 *  <p>
 * The implementation of this method in the PircBot abstract class
 * performs no actions and may be overridden as required.
 *
 * @since PircBot 1.2.0
 *
 * @param chat A DccChat object that represents the incoming chat request.
 *
 * @see DccChat
 *
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.DccChat;
import org.jibble.pircbot.IrcServerConnection;

public class OnIncomingChatRequestEventHandler extends AbstractEventHandler {
    private final DccChat chat ;

    public OnIncomingChatRequestEventHandler(IrcServerConnection pircBot, DccChat chat){
        super.pircBot = pircBot;
        this.chat = chat;
    }
}
