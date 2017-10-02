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
package org.eclipse.osee.framework.ui.data.model.editor;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GraphitiDiagram;
import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.core.enums.PresentationType.RENDER_AS_HUMAN_READABLE_TEXT;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import static org.eclipse.osee.framework.ui.data.model.editor.GraphitiImage.GRAPHITI_DIAGRAM;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Ryan D. Brooks
 */
public class GraphitiRenderer extends DefaultArtifactRenderer {

   public GraphitiRenderer(Map<RendererOption, Object> rendererOptions) {
      super(rendererOptions);
   }

   public GraphitiRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   @Override
   public GraphitiRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new GraphitiRenderer(rendererOptions);
   }

   @Override
   public GraphitiRenderer newInstance() {
      return new GraphitiRenderer();
   }

   @Override
   public String getName() {
      return "GraphitiRenderer";
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      int rating = IRenderer.NO_MATCH;
      if (artifact.getArtifactType().inheritsFrom(CoreArtifactTypes.ModelDiagram)) {
         if (presentationType.matches(RENDER_AS_HUMAN_READABLE_TEXT, PREVIEW, DEFAULT_OPEN, SPECIALIZED_EDIT)) {
            rating = IRenderer.PRESENTATION_SUBTYPE_MATCH;
         }
      }
      return rating;
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      commands.add(new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "Diagram Editor", GRAPHITI_DIAGRAM));
   }

   @Override
   public void open(final List<Artifact> artifacts, PresentationType presentationType)  {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               IWorkbenchPage activePage = AWorkbench.getActivePage();

               for (Artifact artifact : artifacts) {
                  Integer attributeId = artifact.getAttributeIds(GraphitiDiagram).get(0);

                  URI diagramUri = URI.createURI(String.format("osee://branch/%s/artifact/%s/attribute/%d",
                     artifact.getBranch().getIdString(), artifact.getIdString(), attributeId));

                  DiagramEditorInput editorInput = new DiagramEditorInput(diagramUri, null);
                  activePage.openEditor(editorInput, GraphitiDiagramArtifactEditor.EDITOR_ID, true);
               }
            } catch (Exception ex) {
               OseeLog.log(GraphitiRenderer.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }
}