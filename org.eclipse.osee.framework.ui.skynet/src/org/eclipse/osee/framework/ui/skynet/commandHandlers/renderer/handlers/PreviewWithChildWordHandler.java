/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers;

import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

/**
 * @author Jeff C. Phillips
 */
public class PreviewWithChildWordHandler extends PreviewWordHandler {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (!artifacts.isEmpty()) {
         try {
            WordTemplateRenderer renderer = new WordTemplateRenderer();
            renderer.setOptions(getOptions());
            renderer.preview(artifacts);
            dispose();

         } catch (OseeCoreException ex) {
            OseeLog.log(PreviewWithChildWordHandler.class, Level.SEVERE, ex);
         }
      }
      return null;
   }

   /**
    * A subclass may override this method if they would like options to be set on the renderer
    * 
    * @return
    * @throws OseeArgumentException
    */
   protected VariableMap getOptions() throws OseeArgumentException {
      return new VariableMap(ITemplateRenderer.PREVIEW_WITH_RECURSE_OPTION_PAIR);
   }
}
