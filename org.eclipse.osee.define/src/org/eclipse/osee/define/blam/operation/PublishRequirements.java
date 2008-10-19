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
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

/**
 * @author Jeff C. Phillips
 */
public class PublishRequirements extends AbstractBlam {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      String updateParagraphNumber = variableMap.getValue(Boolean.class, "Update Paragraph Numbers").toString();

      for (Artifact artifact : variableMap.getArtifacts("artifacts")) {
         if (monitor.isCanceled()) {
            return;
         }
         if (artifact.isOfType("Folder")) {
            List<Artifact> artifacts = artifact.getChildren();
            if (artifacts.size() > 0) {
               RendererManager.previewInJob(artifacts, WordTemplateRenderer.UPDATE_PARAGRAPH_NUMBER_OPTION,
                     updateParagraphNumber);
            }
         } else {
            RendererManager.preview(artifact, monitor, WordTemplateRenderer.UPDATE_PARAGRAPH_NUMBER_OPTION,
                  updateParagraphNumber);
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
   public Branch wrapOperationForBranch(BlamVariableMap variableMap) throws OseeArgumentException {
      return variableMap.getArtifacts("artifacts").get(0).getBranch();
   }
}