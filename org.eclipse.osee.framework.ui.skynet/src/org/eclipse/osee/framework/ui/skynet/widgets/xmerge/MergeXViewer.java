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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.MergeChangesInArtifactException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.HttpBranchCreation;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.event.MergeBranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.compare.AttributeCompareItem;
import org.eclipse.osee.framework.ui.skynet.compare.CompareHandler;
import org.eclipse.osee.framework.ui.skynet.mergeWizard.ConflictResolutionWizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Donald G. Dunne
 * @author Theron Virgin
 */
public class MergeXViewer extends XViewer {

   private final XMergeViewer xMergeViewer;
   private Conflict[] conflicts;
   private ConflictResolutionWizard conWizard;
   private XMergeLabelProvider labelProvider;
   private Action openMergeViewAction;

   /**
    * @param parent
    * @param style
    */
   public MergeXViewer(Composite parent, int style, XMergeViewer xMergeViewer) {
      super(parent, style, new MergeXViewerFactory());
      this.xMergeViewer = xMergeViewer;
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      createMenuActions();
   }

   public void createMenuActions() {
      MenuManager mm = getMenuManager();
      mm.createContextMenu(getControl());
      mm.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            updateMenuActionsForTable();
         }
      });

      openMergeViewAction = new Action("Open Merge View", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            AWorkbench.popup("ERROR", "Not implemented yet");
         }
      };
   }

   public void updateEditMenuActions() {
      MenuManager mm = getMenuManager();

      // EDIT MENU BLOCK
      mm.insertBefore(MENU_GROUP_PRE, openMergeViewAction);
      openMergeViewAction.setEnabled(true);

   }

   @Override
   public boolean isColumnMultiEditEnabled() {
      return true;
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();

      updateEditMenuActions();

      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

   public void setConflicts(final Conflict[] conflicts) {
      this.conflicts = conflicts;

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            setInput(conflicts);
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
   public XMergeViewer getXUserRoleViewer() {
      return xMergeViewer;
   }

   @Override
   public void resetDefaultSorter() {
      setSorter(new MergeXViewerSorter(this, labelProvider));
   }

   /**
    * @return the transactionArtifactChanges
    */
   public Conflict[] getTransactionArtifactChanges() {
      return conflicts;
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      Conflict conflict = (Conflict) treeItem.getData();
      Shell shell = Display.getCurrent().getActiveShell().getShell();
      if (conflict.statusCommitted()) {
         return super.handleLeftClickInIconArea(treeColumn, treeItem);
      }
      try {
         if (treeColumn.getText().equals(MergeXViewerFactory.Source.getName())) {
            if (conflict.statusNotResolvable()) {
               MergeUtility.showDeletedConflict(conflict, shell);
            } else if (conflict.statusInformational()) {
               MergeUtility.showInformationalConflict(shell);
            } else {
               MergeUtility.setToSource(conflict, shell, true);
            }
         } else if (treeColumn.getText().equals(MergeXViewerFactory.Destination.getName())) {
            if (conflict.statusNotResolvable()) {
               MergeUtility.showDeletedConflict(conflict, shell);
            } else if (conflict.statusInformational()) {
               MergeUtility.showInformationalConflict(shell);
            } else {
               MergeUtility.setToDest(conflict, shell, true);
            }
         } else if (treeColumn.getText().equals(MergeXViewerFactory.Merged.getName())) {
            if (conflict.statusNotResolvable()) {
               MergeUtility.showDeletedConflict(conflict, shell);
            } else if (!conflict.getConflictType().equals(ConflictType.ARTIFACT)) {
               AttributeConflict attributeConflict = (AttributeConflict) conflict;

               // Not for word attribute or enumerations but other strings
               if (!attributeConflict.isWordAttribute() && attributeConflict.getAttribute() instanceof StringAttribute && !(attributeConflict.getAttribute() instanceof EnumeratedAttribute)) {
                  AttributeCompareItem leftContributionItem =
                        new AttributeCompareItem(
                              attributeConflict,
                              attributeConflict.getArtifactName() + " on Branch: " + attributeConflict.getSourceBranch().getName(),
                              attributeConflict.getAttribute().getDisplayableString(), true,
                              ImageManager.getImage(attributeConflict.getArtifact()));
                  AttributeCompareItem rightContributionItem =
                        new AttributeCompareItem(
                              attributeConflict,
                              attributeConflict.getArtifactName() + " on Branch: " + attributeConflict.getDestBranch().getName(),
                              attributeConflict.getDestDisplayData(), false,
                              ImageManager.getImage(attributeConflict.getArtifact()));

                  CompareHandler compareHandler = new CompareHandler(leftContributionItem, rightContributionItem, null);
                  compareHandler.compare();
               } else {
                  if (attributeConflict.getArtifact().isAttributeTypeValid(CoreAttributeTypes.NATIVE_CONTENT.getName())) {
                     MessageDialog dialog =
                           new MessageDialog(
                                 shell,
                                 "Artifact type not supported",
                                 null,
                                 "Native artifact types are not currently supported for the merge wizzard.\n" + "You will need to populate the merge value with the source or destination values" + " and then merge by hand by righ-clicking edit merge artifact.",
                                 2, new String[] {"OK"}, 1);
                     dialog.open();
                  } else {
                     conWizard = new ConflictResolutionWizard(conflict);
                     WizardDialog dialog = new WizardDialog(shell, conWizard);
                     dialog.create();
                     if (dialog.open() == 0) {
                        conWizard.getResolved();
                     }
                  }
               }
            }
         } else if (treeColumn.getText().equals(MergeXViewerFactory.Conflict_Resolved.getName())) {
            if (conflict.statusNotResolvable()) {
               if (MergeUtility.showDeletedConflict(conflict, shell)) {
                  xMergeViewer.refreshTable();
               }
            } else if (conflict.statusInformational()) {
               MergeUtility.showInformationalConflict(shell);
            } else {
               conflict.handleResolvedSelection();
               OseeEventManager.kickMergeBranchEvent(HttpBranchCreation.class, MergeBranchEventType.ConflictResolved,
                     conflict.getMergeBranchID());
            }
         }

      } catch (MergeChangesInArtifactException ex) {
         MessageDialog.openError(shell, "Error", ex.getMessage());
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      xMergeViewer.loadTable();
      return super.handleLeftClickInIconArea(treeColumn, treeItem);
   }

   @Override
   protected void doUpdateItem(Widget widget, Object element, boolean fullMap) {
      super.doUpdateItem(widget, element, fullMap);
      if (conWizard != null) {
         try {
            conWizard.setResolution();
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   public void addLabelProvider(XMergeLabelProvider labelProvider) {
      this.labelProvider = labelProvider;
   }

}
