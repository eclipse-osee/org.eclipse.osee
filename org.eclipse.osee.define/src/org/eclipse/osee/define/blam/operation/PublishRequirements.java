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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

/**
 * @author Jeff C. Phillips
 */
public class PublishRequirements extends AbstractBlam {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Boolean updateParagraphNumber = variableMap.getBoolean("Update Paragraph Numbers");
      List<Artifact> artifacts = variableMap.getArtifacts("artifacts");

      SkynetTransaction transaction = new SkynetTransaction(artifacts.get(0).getBranch());
      VariableMap options =
            new VariableMap(WordTemplateRenderer.UPDATE_PARAGRAPH_NUMBER_OPTION, updateParagraphNumber,
                  ITemplateRenderer.TEMPLATE_OPTION, ITemplateRenderer.PREVIEW_WITH_RECURSE_VALUE,
                  ITemplateRenderer.TRANSACTION_OPTION, transaction);
      for (Artifact artifact : artifacts) {
         try {
            publish(monitor, artifact, options);
         } catch (OseeStateException ex) {
            OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
         }
      }
      transaction.execute();
   }

   private void publish(IProgressMonitor monitor, Artifact artifact, VariableMap options) throws OseeCoreException {
      if (monitor.isCanceled()) {
         return;
      }
      if (artifact.isOfType("Folder")) {
         List<Artifact> nonFolderChildren = new ArrayList<Artifact>();
         for (Artifact child : artifact.getChildren()) {
            if (child.isOfType("Folder")) {
               publish(monitor, child, options);
            } else {
               nonFolderChildren.add(child);
            }
         }
         RendererManager.preview(nonFolderChildren, monitor, options);
      } else {
         RendererManager.preview(artifact, monitor, options);
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
}