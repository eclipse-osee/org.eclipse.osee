/**
 * 
 */
package org.eclipse.osee.ats.handlers;

import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osee.ats.editor.AtsWorldEditorRenderer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers.AbstractEditorHandler;

/**
 * @author Jeff C. Phillips
 */
public class AtsWorldEditorHandler extends AbstractEditorHandler {

   /* (non-Javadoc)
    * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (!artifacts.isEmpty()) {
         AtsWorldEditorRenderer renderer = new AtsWorldEditorRenderer(AtsWorldEditorRenderer.RENDERER_EXTENSION);
         try {
            renderer.open(artifacts);
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsEditorHandler.class, Level.SEVERE, ex);
         }
         dispose();
      }
      return null;
   }
}
