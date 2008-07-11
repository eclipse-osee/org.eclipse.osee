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

import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.event.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.mergeWizard.ConflictResolutionWizard;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
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
public class MergeXViewer extends XViewer implements IEventReceiver {

   private static String NAMESPACE = "osee.skynet.gui.MergeXViewer";
   private final XMergeViewer xCommitViewer;
   private Conflict[] conflicts;
   private ConflictResolutionWizard conWizard;
   private XMergeLabelProvider labelProvider;

   /**
    * @param parent
    * @param style
    */
   public MergeXViewer(Composite parent, int style, XMergeViewer xViewer) {
      this(parent, style, NAMESPACE, new MergeXViewerFactory(), xViewer);
      SkynetEventManager.getInstance().register(BranchEvent.class, this);
   }

   public MergeXViewer(Composite parent, int style, String nameSpace, IXViewerFactory xViewerFactory, XMergeViewer xRoleViewer) {
      super(parent, style, nameSpace, xViewerFactory);
      this.xCommitViewer = xRoleViewer;
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      createMenuActions();
   }

   Action openMergeViewAction;

   public void createMenuActions() {
      MenuManager mm = getMenuManager();
      mm.createContextMenu(getControl());
      mm.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            updateMenuActions();
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
   public boolean isColumnMultiEditable(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      MergeColumn aCol = MergeColumn.getXColumn((XViewerColumn) treeColumn.getData());
      XViewerColumn xCol = getCustomize().getCurrentCustData().getColumnData().getXColumn(aCol.getName());
      if (!xCol.isShow() || !aCol.isMultiColumnEditable()) return false;
      return true;
   }

   @Override
   public boolean isColumnMultiEditEnabled() {
      return true;
   }

   public void updateMenuActions() {
      MenuManager mm = getMenuManager();

      updateEditMenuActions();

      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

   public void setConflicts(Conflict[] conflicts) throws SQLException {
      this.conflicts = conflicts;
      setInput(conflicts);
   }

   /**
    * Release resources
    */
   public void dispose() {
      getLabelProvider().dispose();
   }

   /**
    * @return the xUserRoleViewer
    */
   public XMergeViewer getXUserRoleViewer() {
      return xCommitViewer;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#onEvent(org.eclipse.osee.framework.ui.plugin.event.Event)
    */
   public void onEvent(Event event) {
      if (xCommitViewer != null && xCommitViewer.getXViewer().getTree().isDisposed() != true) {
         xCommitViewer.refresh();
      }
   }

   public void resetDefaultSorter() {
      setSorter(new MergeXViewerSorter(this, labelProvider));
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }

   /**
    * @return the transactionArtifactChanges
    */
   public Conflict[] getTransactionArtifactChanges() {
      return conflicts;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer#handleLeftClickInIconArea(org.eclipse.swt.widgets.TreeColumn,
    *      org.eclipse.swt.widgets.TreeItem)
    */
   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      Conflict conflict = (Conflict) treeItem.getData();
      try {
         Shell shell = Display.getCurrent().getActiveShell().getShell();
         if (treeColumn.getText().equals(MergeColumn.Source.getName())) {
            if (conflict.statusNotResolvable()) {
               MergeUtility.showArtifactDeletedConflict(conflict, shell);
            } else if (conflict.statusInformational()) {
               MergeUtility.showInformationalConflict(shell);
            } else {
               MergeUtility.setToSource(conflict, shell, true);
            }
         } else if (treeColumn.getText().equals(MergeColumn.Destination.getName())) {
            if (conflict.statusNotResolvable()) {
               MergeUtility.showArtifactDeletedConflict(conflict, shell);
            } else if (conflict.statusInformational()) {
               MergeUtility.showInformationalConflict(shell);
            } else {
               MergeUtility.setToDest(conflict, shell, true);
            }
         } else if (treeColumn.getText().equals(MergeColumn.Merged.getName())) {
            if (!(conflict.getConflictType().equals(Conflict.ConflictType.ARTIFACT))) {
               conWizard = new ConflictResolutionWizard(conflict);
               WizardDialog dialog = new WizardDialog(shell, conWizard);
               dialog.create();
               if (dialog.open() == 0) {
                  conWizard.getResolved();
               }
            } else if (conflict.statusNotResolvable()) {
               MergeUtility.showArtifactDeletedConflict(conflict, shell);
            }
         } else if (treeColumn.getText().equals(MergeColumn.Conflict_Resolved.getName())) {
            if (conflict.statusNotResolvable()) {
               if (MergeUtility.showArtifactDeletedConflict(conflict, shell)) {
                  xCommitViewer.refreshTable();
               }
            } else if (conflict.statusInformational()) {
               MergeUtility.showInformationalConflict(shell);
            } else {
               conflict.handleResolvedSelection();
            }
         }

      } catch (Exception ex) {
         OSEELog.logException(MergeXViewer.class, ex, true);
      }
      xCommitViewer.loadTable();
      return super.handleLeftClickInIconArea(treeColumn, treeItem);
   }

   /* (non-Javadoc) Method declared on StructuredViewer. */
   protected void doUpdateItem(Widget widget, Object element, boolean fullMap) {
      super.doUpdateItem(widget, element, fullMap);
      if (conWizard != null) {
         try {
            conWizard.setResolution();
         } catch (Exception ex) {
            OSEELog.logException(MergeXViewer.class, ex, true);
         }
      }
   }

   public void addLabelProvider(XMergeLabelProvider labelProvider) {
      this.labelProvider = labelProvider;
   }

}
