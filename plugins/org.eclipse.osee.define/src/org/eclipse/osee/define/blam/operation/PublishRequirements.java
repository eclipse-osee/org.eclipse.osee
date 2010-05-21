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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.framework.core.data.TransactionDelta;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class PublishRequirements extends AbstractBlam {
   private boolean includeAttributes;
   private boolean publishAsDiff;
   // private boolean removeTrackedChanges;
   private Date date;
   private Branch branch;
   private boolean useBaselineTransaction;

   @Override
   public String getName() {
      return "Publish Requirements";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Boolean updateParagraphNumber = variableMap.getBoolean("Update Paragraph Numbers");
      List<Artifact> artifacts = variableMap.getArtifacts("artifacts");
      includeAttributes = variableMap.getBoolean("Publish With Attributes");
      publishAsDiff = variableMap.getBoolean("Publish As Diff");
      useBaselineTransaction = variableMap.getBoolean("Diff from Baseline");
      // removeTrackedChanges = variableMap.getBoolean("Skip Artifacts with Tracked Changes");
      if (variableMap.getValue("Diff Starting Point") instanceof Date) {
         date = (Date) variableMap.getValue("Diff Starting Point");
      }
      branch = variableMap.getBranch("Diff Branch");

      boolean useArtifactNameInLinks = variableMap.getBoolean("Use Artifact Names");
      boolean useParagraphNumbersInLinks = variableMap.getBoolean("Use Paragraph Numbers");

      if (!useParagraphNumbersInLinks && !useArtifactNameInLinks) {
         throw new OseeArgumentException("Please select at least one Document Link Format");
      }
      LinkType linkType;
      if (useArtifactNameInLinks && useParagraphNumbersInLinks) {
         linkType = LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME;
      } else if (useParagraphNumbersInLinks) {
         linkType = LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER;
      } else {
         linkType = LinkType.INTERNAL_DOC_REFERENCE_USE_NAME;
      }
      RelationManager.getRelatedArtifacts(artifacts, 999, true, CoreRelationTypes.Default_Hierarchical__Child);

      SkynetTransaction transaction = new SkynetTransaction(artifacts.get(0).getBranch(), getName());
      String templateOption;
      if (publishAsDiff) {
         templateOption = includeAttributes ? ITemplateRenderer.DIFF_VALUE : ITemplateRenderer.DIFF_NO_ATTRIBUTES_VALUE;
      } else {
         templateOption =
               includeAttributes ? ITemplateRenderer.PREVIEW_WITH_RECURSE_VALUE : ITemplateRenderer.PREVIEW_WITH_RECURSE_NO_ATTRIBUTES_VALUE;
      }

      VariableMap options =
            new VariableMap(WordTemplateRenderer.UPDATE_PARAGRAPH_NUMBER_OPTION, updateParagraphNumber,
                  ITemplateRenderer.TEMPLATE_OPTION, templateOption, ITemplateRenderer.TRANSACTION_OPTION, transaction,
                  "linkType", linkType, "inPublishMode", true);

      if (publishAsDiff) {
         if (branch == null || (date == null && !useBaselineTransaction)) {
            throw new OseeCoreException(
                  "Must Select a " + branch == null ? "Branch" : "Date" + " to diff against when publishing as Diff");
         }
      }
      TransactionDelta txDelta = createTransactionDelta(branch);

      for (Artifact artifact : artifacts) {
         try {
            publish(monitor, artifact, options, txDelta);
         } catch (OseeStateException ex) {
            OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
         }
      }
      transaction.execute();
   }

   private TransactionDelta createTransactionDelta(Branch branch) throws OseeCoreException {
      TransactionRecord startTx;
      if (publishAsDiff && date != null && !useBaselineTransaction) {
         startTx = TransactionManager.getTransactionAtDate(branch, date);
      } else {
         startTx = branch.getBaseTransaction();
      }
      return new TransactionDelta(startTx, TransactionManager.getHeadTransaction(branch));
   }

   private void publish(IProgressMonitor monitor, Artifact artifact, VariableMap options, TransactionDelta txDelta) throws OseeCoreException {
      if (monitor.isCanceled()) {
         return;
      }

      List<Artifact> nonFolderChildren = new ArrayList<Artifact>();
      if (artifact.isOfType(CoreArtifactTypes.Folder)) {
         for (Artifact child : artifact.getChildren(publishAsDiff)) {
            if (child.isOfType(CoreArtifactTypes.Folder)) {
               publish(monitor, child, options, txDelta);
            } else {
               nonFolderChildren.add(child);
            }
         }
      } else {
         nonFolderChildren.add(artifact);
      }

      if (publishAsDiff) {
         nonFolderChildren = artifact.getDescendants();
         ArrayList<Artifact> olderArtifacts =
               getOlderArtifacts(nonFolderChildren, txDelta.getStartTx().getId(), branch.getId());

         Collection<ArtifactDelta> compareItems = new ArrayList<ArtifactDelta>();
         for (int index = 0; index < olderArtifacts.size() && index < nonFolderChildren.size(); index++) {
            Artifact base = olderArtifacts.get(index);
            Artifact newer = nonFolderChildren.get(index);
            if (isDeleted(base)) {
               base = null;
            }
            if (isDeleted(newer)) {
               newer = null;
            }
            compareItems.add(new ArtifactDelta(txDelta, base, newer));
         }
         RendererManager.diffInJob(compareItems, options);
      } else {
         RendererManager.preview(nonFolderChildren, monitor, options);
      }
   }

   private boolean isDeleted(Artifact artifact) {
      return artifact != null && artifact.isDeleted();
   }

   @Override
   public String getDescriptionUsage() {
      return "Drag in parent artifacts below and click the play button at the top right.";
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Update Paragraph Numbers\" />");

      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Document Link Format:\"/>");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Use Artifact Names\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Use Paragraph Numbers\" />");

      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Publishing Options:\"/>");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Publish With Attributes\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Publish As Diff\" />");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\" \" /><XWidget xwidgetType=\"XLabel\" displayName=\"Diff Options:\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Diff from Baseline\" />");
      builder.append("<XWidget xwidgetType=\"XDate\" displayName=\"Diff Starting Point\" />");
      builder.append("<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Diff Branch\" defaultValue=\"" + BranchManager.getLastBranch().getGuid() + "\" />");
      builder.append("<XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifacts\" />");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   private ArrayList<Artifact> getOlderArtifacts(List<Artifact> artifacts, int transactionId, int branchId) throws OseeCoreException {
      ArrayList<Artifact> historicArtifacts = new ArrayList<Artifact>(artifacts.size());
      int queryId = ArtifactLoader.getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

      Set<Artifact> artifactSet = new HashSet<Artifact>(artifacts);
      List<Object[]> insertParameters = new LinkedList<Object[]>();
      for (Artifact artifact : artifactSet) {
         insertParameters.add(new Object[] {queryId, insertTime, artifact.getArtId(), branchId, transactionId});
      }

      @SuppressWarnings("unused")
      Collection<Artifact> bulkLoadedArtifacts =
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

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Publish");
   }
}