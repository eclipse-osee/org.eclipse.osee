/*
 * Created on Mar 12, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.event;

import java.util.logging.Level;
import org.eclipse.osee.coverage.msgs.EditorSave;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;

/**
 * @author Donald G. Dunne
 */
public class CoverageMessageSender implements OseeMessagingStatusCallback {

   private final ConnectionNode connection;

   public CoverageMessageSender(ConnectionNode connection) {
      this.connection = connection;
   }

   @Override
   public void fail(Throwable th) {
      OseeLog.log(CoverageMessageSender.class, Level.SEVERE, th);
   }

   @Override
   public void success() {
   }
   
   public void send() throws OseeCoreException{
      EditorSave editorSave = new EditorSave();
      connection.send(CoverageMessages.EditorSave, editorSave, this);
   }
}
