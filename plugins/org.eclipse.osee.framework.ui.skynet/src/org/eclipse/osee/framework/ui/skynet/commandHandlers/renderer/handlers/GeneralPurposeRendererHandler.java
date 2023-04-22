/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
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

      RendererMap rendererOptions = new EnumRendererMap();

      String presentationTypeStr = event.getParameter(PresentationType.class.getSimpleName());
      String template = event.getParameter(RendererOption.TEMPLATE_OPTION.getKey());
      String openOption = event.getParameter(RendererOption.OPEN_OPTION.getKey());
      ArtifactId view = Handlers.getViewId();

      PresentationType presentationType = PresentationType.valueOf(presentationTypeStr);
      if (template != null) {
         rendererOptions.setRendererOption(RendererOption.TEMPLATE_OPTION, template);
      }
      if (openOption != null) {
         rendererOptions.setRendererOption(RendererOption.OPEN_OPTION, openOption);
      }
      if (view.isValid()) {
         rendererOptions.setRendererOption(RendererOption.VIEW, view);
      }

      RendererManager.openInJob(artifacts, presentationType, rendererOptions);
      return null;
   }
}
