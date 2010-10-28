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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.WholeWordRenderer;

/**
 * @author Jeff C. Phillips
 */
public class PreviewWholeWordHandler extends AbstractEditorHandler {

   @Override
   public Object execute(ExecutionEvent event) {
      try {
         WholeWordRenderer renderer = new WholeWordRenderer();
         renderer.open(artifacts, PresentationType.PREVIEW);
         dispose();

      } catch (OseeCoreException ex) {
         OseeLog.log(PreviewWholeWordHandler.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return null;
   }
}
