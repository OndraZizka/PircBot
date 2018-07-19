/**
 * This class is called when we receive a numeric response from the
 * IRC server.
 *  <p>
 * Numerics in the range from 001 to 099 are used for client-server
 * connections only and should never travel between servers.  Replies
 * generated in response to commands are found in the range from 200
 * to 399.  Error replies are found in the range from 400 to 599.
 *  <p>
 * For example, we can use this method to discover the topic of a
 * channel when we join it.  If we join the channel #test which
 * has a topic of &quot;I am King of Test&quot; then the response
 * will be &quot;<code>PircBot #test :I Am King of Test</code>&quot;
 * with a code of 332 to signify that this is a topic.
 * (This is just an example - note that overriding the
 * <code>onTopic</code> method is an easier way of finding the
 * topic for a channel). Check the IRC RFC for the full list of other
 * command response codes.
 *  <p>
 * PircBot implements the interface ReplyConstants, which contains
 * contstants that you may find useful here.
 *  <p>
 * The implementation of this method in the PircBot abstract class
 * performs no actions and may be overridden as required.
 *
 * @param code The three-digit numerical code for the response.
 * @param response The full response from the IRC server.
 *
 * @see ReplyConstants
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.IrcServerConnection;

public class OnServerResponseEventHandler extends AbstractEventHandler{
    int code ;
    String response ;

    public OnServerResponseEventHandler(IrcServerConnection pircBot, int code, String response){
        super.pircBot = pircBot ;
        this.code = code ;
        this.response = response ;
    }
}
