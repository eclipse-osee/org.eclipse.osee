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
package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import static org.eclipse.osee.framework.core.enums.PresentationType.RENDER_AS_HUMAN_READABLE_TEXT;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.httpRequests.CreateBranchHttpRequestOperation;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.compare.AttributeCompareItem;
import org.eclipse.osee.framework.ui.skynet.compare.CompareHandler;
import org.eclipse.osee.framework.ui.skynet.compare.CompareItem;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.mergeWizard.ConflictResolutionWizard;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Donald G. Dunne
 * @author Theron Virgin
 */
public class MergeXViewer extends XViewer {
   public static final Conflict[] EMPTY_CONFLICTS = new Conflict[0];
   private final MergeXWidget mergeXWidget;
   private Conflict[] conflicts = EMPTY_CONFLICTS;
   private ConflictResolutionWizard conWizard;
   private XMergeLabelProvider labelProvider;

   public MergeXViewer(Composite parent, int style, MergeXWidget xMergeViewer, IOseeTreeReportProvider reportProvider) {
      super(parent, style, new MergeXViewerFactory(reportProvider));
      this.mergeXWidget = xMergeViewer;
   }

   @Override
   public boolean isColumnMultiEditEnabled() {
      return true;
   }

   public Conflict[] getConflicts() {
      return conflicts;
   }

   public void setConflicts(final Conflict[] conflicts) {
      this.conflicts = conflicts != null ? conflicts : EMPTY_CONFLICTS;

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Widgets.isAccessible(getControl())) {
               setInput(conflicts);
            }
         }
      });
   }

   /**
    * Release resources
    */
   @Override
   public void dispose() {
      getLabelProvider().dispose();
   }

   /**
    * @return the xUserRoleViewer
    */
   public MergeXWidget getXUserRoleViewer() {
      return mergeXWidget;
   }

   @Override
   public void resetDefaultSorter() {
      setSorter(new MergeXViewerSorter(this, labelProvider));
   }

   //   public Conflict[] getTransactionArtifactChanges() {
   //      return conflicts;
   //   }

   private boolean hasInteractiveIcon(TreeColumn treeColumn) {
      return isXViewerColumn(treeColumn, MergeXViewerFactory.Source) //
         || isXViewerColumn(treeColumn, MergeXViewerFactory.Destination) //
         || isXViewerColumn(treeColumn, MergeXViewerFactory.Conflict_Resolved) //
         || isXViewerColumn(treeColumn, MergeXViewerFactory.Merged);
   }

   private boolean isXViewerColumn(TreeColumn treeColumn, XViewerColumn expected) {
      return Widgets.isAccessible(treeColumn) && treeColumn.getText().equals(expected.getName());
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      Conflict conflict = (Conflict) treeItem.getData();
      if (!conflict.getStatus().isCommitted() && hasInteractiveIcon(treeColumn)) {
         respondToIconClick(conflict, treeColumn);
      }

      return super.handleLeftClickInIconArea(treeColumn, treeItem);
   }

   private void respondToIconClick(Conflict conflict, TreeColumn treeColumn) {
      Shell shell = Displays.getActiveShell().getShell();

      try {
         if (conflict.getStatus().isInformational()) {
            MergeUtility.showInformationalConflict(shell, conflict);
         } else {
            handleResolvableConflictClick(treeColumn, conflict, shell);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void handleResolvableConflictClick(TreeColumn treeColumn, Conflict conflict, Shell shell) throws MultipleArtifactsExist, ArtifactDoesNotExist, Exception {
      if (isXViewerColumn(treeColumn, MergeXViewerFactory.Source)) {
         MergeUtility.setToSource(conflict, shell, true);
      } else if (isXViewerColumn(treeColumn, MergeXViewerFactory.Destination)) {
         MergeUtility.setToDest(conflict, shell, true);
      } else if (isXViewerColumn(treeColumn, MergeXViewerFactory.Conflict_Resolved)) {
         conflict.handleResolvedSelection();
         OseeEventManager.kickBranchEvent(CreateBranchHttpRequestOperation.class,
            new BranchEvent(BranchEventType.MergeConflictResolved, conflict.getMergeBranch()));
      } else if (isXViewerColumn(treeColumn, MergeXViewerFactory.Merged)) {
         if (!conflict.getConflictType().equals(ConflictType.ARTIFACT)) {
            AttributeConflict attributeConflict = (AttributeConflict) conflict;
            if (attributeConflict.isSimpleStringAttribute()) {
               getCompareHandler(attributeConflict).compare();
            } else if (attributeConflict.involvesNativeContent()) {
               nativeContentAlert(shell);
            } else {
               conWizard = new ConflictResolutionWizard(conflict);
               WizardDialog dialog = new WizardDialog(shell, conWizard);
               dialog.create();
               dialog.open();
            }
         }
      }
      mergeXWidget.loadTable();
   }

   private CompareHandler getCompareHandler(AttributeConflict attributeConflict)  {

      Artifact sourceArtifact = attributeConflict.getArtifact();
      String leftName = sourceArtifact.getName() + " on Branch: " + attributeConflict.getSourceBranch().getName();
      String leftContents =
         RendererManager.getBestRenderer(RENDER_AS_HUMAN_READABLE_TEXT, sourceArtifact).renderAttributeAsString(
            attributeConflict.getAttributeType(), sourceArtifact, RENDER_AS_HUMAN_READABLE_TEXT, Strings.EMPTY_STRING);

      Artifact destArtifact = attributeConflict.getDestArtifact();
      String rightName = destArtifact.getName() + " on Branch: " + attributeConflict.getDestBranch().getName();
      String rightContents =
         RendererManager.getBestRenderer(RENDER_AS_HUMAN_READABLE_TEXT, destArtifact).renderAttributeAsString(
            attributeConflict.getAttributeType(), destArtifact, RENDER_AS_HUMAN_READABLE_TEXT, Strings.EMPTY_STRING);

      Image image = ArtifactImageManager.getImage(sourceArtifact);
      AttributeCompareItem leftCompareItem = new AttributeCompareItem(attributeConflict, leftName, leftContents, true,
         image, CompareItem.generateDiffFile("source"));
      AttributeCompareItem rightCompareItem = new AttributeCompareItem(attributeConflict, rightName, rightContents,
         false, image, CompareItem.generateDiffFile("dest"));

      return new CompareHandler(null, leftCompareItem, rightCompareItem, null);
   }

   private static void nativeContentAlert(Shell shell) {
      MessageDialog dialog = new MessageDialog(shell, "Artifact type not supported", null,
         "Native artifact types are not currently supported for the merge wizard.\n" + "You will need to populate the merge value with the source or destination values" + " and then merge by hand by right-clicking \"Edit Merge Artifact.\"",
         2, new String[] {"OK"}, 1);
      dialog.open();
   }

   @Override
   protected void doUpdateItem(Widget widget, Object element, boolean fullMap) {
      super.doUpdateItem(widget, element, fullMap);
      if (conWizard != null) {
         try {
            conWizard.setResolution();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   public void addLabelProvider(XMergeLabelProvider labelProvider) {
      this.labelProvider = labelProvider;
   }

}
