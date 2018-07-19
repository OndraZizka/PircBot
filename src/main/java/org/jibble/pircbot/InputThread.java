/*
Copyright Paul James Mutton, 2001-2009, http://www.jibble.org/
This file is part of PircBot.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/
*/


package org.jibble.pircbot;

import java.io.*;
import java.net.*;
import java.util.*;

import org.jibble.pircbot.handlers.OnDisconnectEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Thread which reads lines from the IRC server.  It then
 * passes these lines to the PircBot without changing them.
 * This running Thread also detects disconnection from the server
 * and is thus used by the OutputThread to send lines to the server.
 *
 * @author  Paul James Mutton,
 *          <a href="http://www.jibble.org/">http://www.jibble.org/</a>
 */
public class InputThread extends Thread {
    private static final Logger log = LoggerFactory.getLogger(InputThread.class);

    /**
     * The InputThread reads lines from the IRC server and allows the
     * PircBot to handle them.
     *
     * @param bot An instance of the underlying PircBot.
     * @param breader The BufferedReader that reads lines from the server.
     * @param bwriter The BufferedWriter that sends lines to the server.
     */
    InputThread(IrcServerConnection bot, Socket socket, BufferedReader breader, BufferedWriter bwriter) {
        _bot = bot;
        _socket = socket;
        _breader = breader;
        _bwriter = bwriter;
        this.setName("input-thread");
    }


    /**
     * Sends a raw line to the IRC server as soon as possible, bypassing the
     * outgoing message queue.
     *
     * @param line The raw line to send to the IRC server.
     * 
     * TODO: Remove, this shouldn't be in an InputThread.
     */
    void sendRawLine(String line) {
        OutputThread.sendRawLine(_bot, _bwriter, line);
    }


    /**
     * Returns true if this InputThread is connected to an IRC server.
     * The result of this method should only act as a rough guide,
     * as the result may not be valid by the time you act upon it.
     *
     * @return True if still connected.
     */
    boolean isConnected() {
        return _isConnected;
    }


    /**
     * Called to start this Thread reading lines from the IRC server.
     * When a line is read, this method calls the handleLine method
     * in the PircBot, which may subsequently call an 'onXxx' method
     * in the PircBot subclass.  If any subclass of Throwable (i.e.
     * any Exception or Error) is thrown by your method, then this
     * method will print the stack trace to the standard output.  It
     * is probable that the PircBot may still be functioning normally
     * after such a problem, but the existence of any uncaught exceptions
     * in your code is something you should really fix.
     */
    public void run() {
        try {
            boolean running = true;
            while (running) {
                try {
                    String line = null;
                    while ((line = _breader.readLine()) != null) {
                        try {
                            // TODO: This call shouldn't be here.
                            if( log.isDebugEnabled() )  log.debug("<<<" + line);
                            _bot.handleLine(line);
                        }
                        catch (Throwable t) {
                            // Stick the whole stack trace into a String so we can output it nicely.
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            t.printStackTrace(pw);
                            pw.flush();
                            StringTokenizer tokenizer = new StringTokenizer(sw.toString(), "\r\n");
                            synchronized (_bot) {
                                log.error("### Your implementation of PircBot is faulty and you have");
                                log.error("### allowed an uncaught Exception or Error to propagate from your code.");
                                log.error("### It may be possible for PircBot to continue operating normally.");
                                log.error("### Here is the stack trace that was produced:");
                                log.error("### ");
                                while (tokenizer.hasMoreTokens()) {
                                    log.error("### " + tokenizer.nextToken());
                                }
                            }
                        }
                    }
                    if (line == null) {
                        // The server must have disconnected us.
                        running = false;
                    }
                }
                catch (InterruptedIOException iioe) {
                    // This will happen if we haven't received anything from the server for a while.
                    // So we shall send it a ping to check that we are still connected.
                    this.sendRawLine("PING " + (System.currentTimeMillis() / 1000));
                    // Now we go back to listening for stuff from the server...
                }
            }
        }
        catch (Exception e) {
            // Do nothing.
        }

        // If we reach this point, then we must have disconnected.
        try {
            _socket.close();
        }
        catch (Exception e) {
            // Just assume the socket was already closed.
        }

        if (!_disposed) {
            log.debug("*** Disconnected.");
            _isConnected = false;
            new OnDisconnectEventHandler().execute();
        }

    }


    /**
     * Closes the socket without onDisconnect being called subsequently.
     */
    public void dispose () {
        try {
            _disposed = true;
            _socket.close();
        }
        catch (Exception e) {
            // Do nothing.
        }
    }

    private IrcServerConnection _bot = null;
    private Socket _socket = null;
    private BufferedReader _breader = null;
    private BufferedWriter _bwriter = null;
    private boolean _isConnected = true;
    private boolean _disposed = false;

    public static final int MAX_LINE_LENGTH = 512;

}
