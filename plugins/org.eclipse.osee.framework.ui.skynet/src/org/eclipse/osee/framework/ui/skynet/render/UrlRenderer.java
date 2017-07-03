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

package org.eclipse.osee.framework.ui.skynet.render;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ContentUrl;
import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERAL_REQUESTED;
import static org.eclipse.osee.framework.core.enums.PresentationType.WEB_PREVIEW;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class UrlRenderer extends DefaultArtifactRenderer {

   public UrlRenderer(Map<RendererOption, Object> rendererOptions) {
      super(rendererOptions);
   }

   public UrlRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   @Override
   public UrlRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new UrlRenderer(rendererOptions);
   }

   @Override
   public UrlRenderer newInstance() {
      return new UrlRenderer();
   }

   @Override
   public String getName() {
      return "UrlRenderer";
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      if (!presentationType.matches(GENERALIZED_EDIT,
         GENERAL_REQUESTED) && artifact.getAttributeCount(ContentUrl) > 0) {
         if (presentationType.equals(WEB_PREVIEW)) {
            return SPECIALIZED_MATCH;
         } else if (presentationType.equals(PresentationType.PREVIEW)) {
            return PRESENTATION_SUBTYPE_MATCH;
         } else if (presentationType.equals(DEFAULT_OPEN)) {
            return PRESENTATION_TYPE;
         }
      }
      return NO_MATCH;
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      if (artifact.getAttributeCount(ContentUrl) > 0) {
         commands.add(new MenuCmdDef(CommandGroup.PREVIEW, WEB_PREVIEW, "Web Browser", PluginUiImage.URL));
      }
   }

   @Override
   public void open(final List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               for (Artifact artifact : artifacts) {
                  Program.launch(artifact.getSoleAttributeValueAsString(ContentUrl, getName()));
               }
            } catch (Exception ex) {
               OseeLog.log(UrlRenderer.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }
}