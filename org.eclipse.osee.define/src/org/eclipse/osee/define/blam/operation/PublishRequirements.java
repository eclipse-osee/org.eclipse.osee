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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.templates.ITemplateProvider;
import org.eclipse.osee.framework.ui.skynet.templates.TemplateManager;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class PublishRequirements extends AbstractBlam {
   private boolean includeAttributes;
   private boolean publishAsDiff;
   //private boolean removeTrackedChanges;
   private Date date;
   private Branch branch;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Boolean updateParagraphNumber = variableMap.getBoolean("Update Paragraph Numbers");
      List<Artifact> artifacts = variableMap.getArtifacts("artifacts");
      includeAttributes = variableMap.getBoolean("Publish With Attributes");
      publishAsDiff = variableMap.getBoolean("Publish As Diff");
      // removeTrackedChanges = variableMap.getBoolean("Skip Artifacts with Tracked Changes");
      if (variableMap.getValue("Diff Starting Point") instanceof Date) {
         date = (Date) variableMap.getValue("Diff Starting Point");
      }
      branch = variableMap.getBranch("Diff Branch");

      RelationManager.getRelatedArtifacts(artifacts, 999, true, CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD);

      SkynetTransaction transaction = new SkynetTransaction(artifacts.get(0).getBranch());
      String templateOption =
            publishAsDiff ? (includeAttributes ? ITemplateRenderer.DIFF_VALUE : ITemplateRenderer.DIFF_NO_ATTRIBUTES_VALUE) : (includeAttributes ? ITemplateRenderer.PREVIEW_WITH_RECURSE_VALUE : ITemplateRenderer.PREVIEW_WITH_RECURSE_NO_ATTRIBUTES_VALUE);
      VariableMap options =
            new VariableMap(WordTemplateRenderer.UPDATE_PARAGRAPH_NUMBER_OPTION, updateParagraphNumber,
                  ITemplateRenderer.TEMPLATE_OPTION, templateOption, ITemplateRenderer.TRANSACTION_OPTION, transaction,
                  "linkType", LinkType.INTERNAL_DOC_REFERENCE);
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

      ArrayList<Artifact> nonFolderChildren = new ArrayList<Artifact>();
      if (artifact.isOfType("Folder")) {
         for (Artifact child : artifact.getChildren(true)) {
            if (child.isOfType("Folder")) {
               publish(monitor, child, options);
            } else {
               nonFolderChildren.add(child);
            }
         }
      } else {
         nonFolderChildren.add(artifact);
      }

      if (publishAsDiff) {
         if (branch == null || date == null) {
            throw new OseeCoreException(
                  "Must Select a " + branch == null ? "Branch" : "Date" + " to diff against when publishing as Diff");
         }
         nonFolderChildren = buildRecursiveList(nonFolderChildren);
         int transactionId = BranchManager.getBranchTransaction(date, branch.getBranchId());
         ArrayList<Artifact> olderArtifacts = getOlderArtifacts(nonFolderChildren, transactionId, branch.getBranchId());
         int index = 0;
         for (Artifact art : olderArtifacts) {
            if (art != null && art.isDeleted()) {
               olderArtifacts.set(index, null);
            }
            index++;
         }
         index = 0;
         for (Artifact art : nonFolderChildren) {
            if (art != null && art.isDeleted()) {
               nonFolderChildren.set(index, null);
            }
            index++;
         }
         RendererManager.diffInJob(olderArtifacts, nonFolderChildren, options);
      } else {
         RendererManager.preview(nonFolderChildren, monitor, options);
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
      List<Artifact> templates = new ArrayList<Artifact>();
      try {
         for (ITemplateProvider provider : TemplateManager.getTemplateProviders()) {
            templates.addAll(provider.getAllTemplates());
         }
         Collections.sort(templates);
      } catch (OseeCoreException ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
      }
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Update Paragraph Numbers\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Publish With Attributes\" /><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Publish As Diff\" /><XWidget xwidgetType=\"XLabel\" displayName=\" \" /><XWidget xwidgetType=\"XLabel\" displayName=\"Diff Options:\" /><XWidget xwidgetType=\"XDate\" displayName=\"Diff Starting Point\" /><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Diff Branch\" defaultValue=\"" + BranchManager.getLastBranch().getBranchName() + "\" /><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifacts\" />");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   private ArrayList<Artifact> getOlderArtifacts(ArrayList<Artifact> artifacts, int transactionId, int branchId) throws OseeCoreException {
      ArrayList<Artifact> historicArtifacts = new ArrayList<Artifact>(artifacts.size());
      int queryId = ArtifactLoader.getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

      Set<Artifact> artifactSet = new HashSet<Artifact>(artifacts);
      List<Object[]> insertParameters = new LinkedList<Object[]>();
      for (Artifact artifact : artifactSet) {
         insertParameters.add(new Object[] {queryId, insertTime, artifact.getArtId(), branchId, transactionId});
      }
      ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.FULL, null, insertParameters, false, true, true);
      for (Artifact artifact : artifacts) {
         historicArtifacts.add(ArtifactCache.getHistorical(artifact.getArtId(), transactionId));
      }
      return historicArtifacts;
   }

   private ArrayList<Artifact> buildRecursiveList(ArrayList<Artifact> artifacts) throws OseeCoreException {
      ArrayList<Artifact> artifactWithChildren = new ArrayList<Artifact>(artifacts.size());
      for (Artifact artifact : artifacts) {
         artifactWithChildren.add(artifact);
         addChildren(artifactWithChildren, artifact);
      }
      return artifactWithChildren;
   }

   private void addChildren(ArrayList<Artifact> artifacts, Artifact artifact) throws OseeCoreException {
      for (Artifact loopArtifact : artifact.getChildren(true)) {
         artifacts.add(loopArtifact);
         addChildren(artifacts, loopArtifact);
      }
   }
}