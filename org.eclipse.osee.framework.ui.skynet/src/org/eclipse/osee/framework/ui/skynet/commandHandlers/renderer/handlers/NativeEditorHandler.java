/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers;

import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.render.NativeRenderer;

/**
 * @author Jeff C. Phillips
 */
public class NativeEditorHandler extends AbstractEditorHandler {

   /* (non-Javadoc)
    * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (!artifacts.isEmpty()) {
         try {
            NativeRenderer renderer = new NativeRenderer(NativeRenderer.EXTENSION_ID);
            renderer.open(artifacts);
            dispose();

         } catch (OseeCoreException ex) {
            OseeLog.log(WordEditorHandler.class, Level.SEVERE, ex);
         }
      }
      return null;
   }

}
