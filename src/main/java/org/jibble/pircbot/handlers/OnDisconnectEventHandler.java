/**
 * This class carries out the actions to be performed when the PircBot
 * gets disconnected.  This may happen if the PircBot quits from the
 * server, or if the connection is unexpectedly lost.
 *  <p>
 * Disconnection from the IRC server is detected immediately if either
 * we or the server close the connection normally. If the connection to
 * the server is lost, but neither we nor the server have explicitly closed
 * the connection, then it may take a few minutes to detect (this is
 * commonly referred to as a "ping timeout").
 *  <p>
 * If you wish to get your IRC pircBot to automatically rejoin a server after
 * the connection has been lost, then this is probably the ideal method to
 * override to implement such functionality.
 *  <p>
 * The implementation of this method in the PircBot abstract class
 * performs no actions and may be overridden as required.
 */

package org.jibble.pircbot.handlers;

public class OnDisconnectEventHandler extends AbstractEventHandler {

}
