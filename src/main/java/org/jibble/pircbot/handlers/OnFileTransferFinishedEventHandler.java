/**
 * This method gets called when a DccFileTransfer has finished.
 * If there was a problem, the Exception will say what went wrong.
 * If the file was sent successfully, the Exception will be null.
 *  <p>
 * Both incoming and outgoing file transfers are passed to this method.
 * You can determine the type by calling the isIncoming or isOutgoing
 * methods on the DccFileTransfer object.
 *
 * @since PircBot 1.2.0
 *
 * @param transfer The DccFileTransfer that has finished.
 * @param e null if the file was transfered successfully, otherwise this
 *          will report what went wrong.
 *
 * @see DccFileTransfer
 *
 */

package org.jibble.pircbot.handlers;

import org.jibble.pircbot.DccFileTransfer;
import org.jibble.pircbot.IrcServerConnection;

public class OnFileTransferFinishedEventHandler extends AbstractEventHandler {
    DccFileTransfer fileTransfer ;
    Exception exception ;

    public OnFileTransferFinishedEventHandler(IrcServerConnection pircBot, DccFileTransfer fileTransfer, Exception exception){
        super.pircBot = pircBot ;
        this.fileTransfer = fileTransfer ;
        this.exception = exception ;
    }

}
