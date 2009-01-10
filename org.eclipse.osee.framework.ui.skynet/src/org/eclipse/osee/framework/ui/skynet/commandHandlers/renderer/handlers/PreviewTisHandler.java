/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers;

import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.render.TisRenderer;

/**
 * @author Jeff C. Phillips
 */
public class PreviewTisHandler extends AbstractEditorHandler {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (!artifacts.isEmpty()) {
         try {
            TisRenderer renderer = new TisRenderer(TisRenderer.RENDERER_EXTENSION);
            renderer.preview(artifacts);
            dispose();

         } catch (OseeCoreException ex) {
            OseeLog.log(PreviewTisHandler.class, Level.SEVERE, ex);
         }
      }
      return null;
   }
}
