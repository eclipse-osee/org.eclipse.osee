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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.render.TisRenderer;

/**
 * @author Jeff C. Phillips
 */
public class PreviewTisHandler extends AbstractEditorHandler {

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (!artifacts.isEmpty()) {
         try {
            TisRenderer renderer = new TisRenderer();
            renderer.preview(artifacts);
            dispose();

         } catch (OseeCoreException ex) {
            OseeLog.log(PreviewTisHandler.class, Level.SEVERE, ex);
         }
      }
      return null;
   }
}
