/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers;

import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

/**
 * @author Jeff C. Phillips
 */
public class WordEditorHandler extends AbstractEditorHandler {
   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (!artifacts.isEmpty()) {
         try {
            WordTemplateRenderer renderer = new WordTemplateRenderer(WordTemplateRenderer.RENDERER_EXTENSION);
            renderer.open(artifacts);
            dispose();

         } catch (OseeCoreException ex) {
            OseeLog.log(WordEditorHandler.class, Level.SEVERE, ex);
         }
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers.AbstractEditorHandler#getPermissionLevel()
    */
   @Override
   protected PermissionEnum getPermissionLevel() {
      return PermissionEnum.WRITE;
   }
}
