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
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.renderer.RenderLocation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * The handler is associated with the command "org.eclipse.osee.framework.ui.skynet.renderer.command" and is used for
 * all standard renderers through the use of the command parameters:
 * <dl>
 * <dt>PresentationType</dt>
 * <dd>Set to the {@link PresentationType#name()} of the type of presentation made by the command. This parameter is
 * required.</dd>
 * <dt>Template</dt>
 * <dd>Set to the publishing template selection option. See {@link PublishingTemplateRequest}. This parameter is
 * optional. The specified value will be set into the {@link RendererOption#TEMPLATE_OPTION} for the selected
 * renderer.</dd>
 * <dt>RenderLocation</dt>
 * <dd>Set to the {@link RenderLocation#name()} of the location where the renderer should perform the render. This
 * parameter is optional. The specified value will be set into the {@link RendererOption#RENDER_LOCATION} for the
 * selected renderer.</dd>
 * <dt>open.option</dt>
 * <dd>Used to specify the preferred renderer for the command. This parameter is optional. The specified value will be
 * set into the {@link RendererOption#OPEN_OPTION} for the selected renderer.</dd>
 * </dl>
 *
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

public class GeneralPurposeRendererHandler extends AbstractEditorHandler {

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {

      RendererMap rendererOptions = new EnumRendererMap();

      String presentationTypeString = event.getParameter(PresentationType.class.getSimpleName());
      String renderLocationKey = event.getParameter(RendererOption.RENDER_LOCATION.getKey());
      String template = event.getParameter(RendererOption.TEMPLATE_OPTION.getKey());
      String openOption = event.getParameter(RendererOption.OPEN_OPTION.getKey());

      ArtifactId view = Handlers.getViewId();

      PresentationType presentationType = null;

      if (presentationTypeString != null) {

         try {
            presentationType = PresentationType.valueOf(presentationTypeString);
         } catch (Exception e) {
            //eat exception, if block below will detect error
         }

      }

      if (presentationType == null) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "Command parameter Presentation Type must be specified as a valid Presentation Type." )
                             .indentInc()
                             .segment( "Presentation Type", presentationTypeString )
                             .toString()
                   );
      }

      if (template != null) {
         rendererOptions.setRendererOption(RendererOption.TEMPLATE_OPTION, template);
      }

      if (renderLocationKey != null) {
         try {
            var renderLocation = RenderLocation.valueOf(renderLocationKey);
            rendererOptions.setRendererOption(RendererOption.RENDER_LOCATION, renderLocation);
         } catch (Exception e) {
            //@formatter:off
            throw
               new OseeCoreException
                      (
                         new Message()
                                .title( "Command parameter Render Location when specified must be a valid Render Location." )
                                .indentInc()
                                .segment( "RenderLocation", renderLocationKey )
                                .toString()
                      );
            //@formatter:on
         }
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
