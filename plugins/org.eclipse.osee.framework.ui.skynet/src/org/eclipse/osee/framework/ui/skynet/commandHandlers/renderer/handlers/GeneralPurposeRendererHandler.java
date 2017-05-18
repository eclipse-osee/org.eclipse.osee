/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers;

import java.util.ArrayList;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * The handler is associated with the command "org.eclipse.osee.framework.ui.skynet.renderer.command" and is used for
 * all standard renderers through the use of command parameters (PresentationType, open.option, template)
 *
 * @author Ryan D. Brooks
 */
public class GeneralPurposeRendererHandler extends AbstractEditorHandler {
   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      ArrayList<Object> options = new ArrayList<>(6);
      String presentationTypeStr = event.getParameter(PresentationType.class.getSimpleName());
      String template = event.getParameter(ITemplateRenderer.TEMPLATE_OPTION);
      String openOption = event.getParameter(IRenderer.OPEN_OPTION);
      ArtifactId viewId = Handlers.getViewId();

      PresentationType presentationType = PresentationType.valueOf(presentationTypeStr);
      if (template != null) {
         options.add(ITemplateRenderer.TEMPLATE_OPTION);
         options.add(template);
      }
      if (openOption != null) {
         options.add(IRenderer.OPEN_OPTION);
         options.add(openOption);
      }
      if (!viewId.equals(ArtifactId.SENTINEL)) {
         options.add(IRenderer.VIEW_ID);
         options.add(viewId);
      }

      RendererManager.openInJob(artifacts, presentationType, options.toArray());
      return null;
   }
}
