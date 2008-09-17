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
package org.eclipse.osee.define.blam.operation;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * @author Jeff C. Phillips
 */
public class PublishRequirements extends AbstractBlam {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      RendererManager rendererManager = RendererManager.getInstance();
      boolean updateParagraphNumber = variableMap.getValue(Boolean.class, "Update Paragraph Numbers");

      for (Artifact artifact : variableMap.getArtifacts("artifacts")) {
         if (monitor.isCanceled()) {
            return;
         }
         if (artifact.isOfType("Folder")) {
            List<Artifact> arts = artifact.getChildren();
            if (arts.size() > 0) {
               IRenderer renderer = rendererManager.getBestRenderer(PresentationType.PREVIEW, arts.get(0));
               renderer.setRendererOptions(new String[] {"updateParagraphNumber=" + updateParagraphNumber});
               renderer.preview(arts, "PREVIEW_WITH_RECURSE_NO_ATTRIBUTES", monitor);
               renderer.setDefaultOptions();
            }
         } else {
            IRenderer renderer = rendererManager.getBestRenderer(PresentationType.PREVIEW, artifact);
            renderer.setRendererOptions(new String[] {"updateParagraphNumber=" + updateParagraphNumber});
            renderer.preview(artifact, "PREVIEW_WITH_RECURSE_NO_ATTRIBUTES", monitor);
            renderer.setDefaultOptions();
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getDescriptionUsage()
    */
   public String getDescriptionUsage() {
      return "Drag in parent artifacts below and click the play button at the top right.";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Update Paragraph Numbers\" /><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifacts\" /></xWidgets>";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#wrapOperationForBranch(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap)
    */
   @Override
   public Branch wrapOperationForBranch(BlamVariableMap variableMap) {
      return variableMap.getArtifacts("artifacts").get(0).getBranch();
   }
}