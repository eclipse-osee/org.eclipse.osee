/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.render.HTMLRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;

/**
 * @author Marc A. Potter
 */
public class HtmlPreviewEditorHandler extends AbstractEditorHandler {

   @Override
   protected Object executeWithException(ExecutionEvent event, IStructuredSelection selection) throws OseeCoreException {
      if (!artifacts.isEmpty()) {
         HTMLRenderer renderer = new HTMLRenderer();
         renderer.open(artifacts, PresentationType.PREVIEW);
         dispose();
      }
      return null;
   }

}
