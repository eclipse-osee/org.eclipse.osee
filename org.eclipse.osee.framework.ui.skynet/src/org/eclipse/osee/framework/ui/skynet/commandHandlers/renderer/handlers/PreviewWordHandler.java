/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers;

import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

/**
 * @author Jeff C. Phillips
 */
public class PreviewWordHandler extends AbstractEditorHandler {

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
            renderer.setOptions(getOptions());
            renderer.preview(artifacts);
            dispose();

         } catch (OseeCoreException ex) {
            OseeLog.log(PreviewWordHandler.class, Level.SEVERE, ex);
         }
      }
      return null;
   }

   /**
    * A subclass may override this method if they would like options to be set on the renderer
    * 
    * @return
    */
   protected VariableMap getOptions() throws OseeArgumentException {
      return null;
   }
}
