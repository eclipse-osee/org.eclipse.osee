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
package org.eclipse.osee.ats.handlers;

import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osee.ats.export.AtsExportRenderer;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers.AbstractEditorHandler;

/**
 * @author Jeff C. Phillips
 */
public class AtsExportHandler extends AbstractEditorHandler {

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (!artifacts.isEmpty()) {
         AtsExportRenderer renderer = new AtsExportRenderer();
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
