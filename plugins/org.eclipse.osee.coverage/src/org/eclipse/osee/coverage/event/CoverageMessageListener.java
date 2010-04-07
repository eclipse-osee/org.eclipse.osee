/*
 * Created on Mar 11, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.event;

import java.util.Map;
import org.eclipse.osee.coverage.msgs.EditorSave;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class CoverageMessageListener extends OseeMessagingListener {

   public CoverageMessageListener() {
      super(EditorSave.class);
   }

   @Override
   public void process(final Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
      PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
         @Override
         public void run() {
            EditorSave editorSave = (EditorSave) message;
            System.out.println("Editor Save " + editorSave.getName());
         }
      });
   }
}
