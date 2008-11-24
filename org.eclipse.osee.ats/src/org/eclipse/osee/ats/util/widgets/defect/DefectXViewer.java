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
package org.eclipse.osee.ats.util.widgets.defect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Disposition;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.InjectionActivity;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Severity;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.EnumStringSingleSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetGuiDebug;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XPromptChange;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XPromptChange.Option;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class DefectXViewer extends XViewer {

   private final XDefectViewer xDefectViewer;

   /**
    * @param parent
    * @param style
    */
   public DefectXViewer(Composite parent, int style, XDefectViewer xDefectViewer) {
      this(parent, style, new DefectXViewerFactory(), xDefectViewer);
   }

   public DefectXViewer(Composite parent, int style, IXViewerFactory xViewerFactory, XDefectViewer xDefectViewer) {
      super(parent, style, xViewerFactory);
      this.xDefectViewer = xDefectViewer;
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      parent.addDisposeListener(new DisposeListener() {
         public void widgetDisposed(DisposeEvent e) {
            ((DefectContentProvider) getContentProvider()).clear();
         }
      });
      createMenuActions();
   }

   public void createMenuActions() {
      MenuManager mm = getMenuManager();
      mm.createContextMenu(getControl());
      mm.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            updateMenuActions();
         }
      });
   }

   public void updateEditMenuActions() {
      // MenuManager mm = getMenuManager();

      // EDIT MENU BLOCK
   }

   public void updateMenuActions() {
      MenuManager mm = getMenuManager();

      updateEditMenuActions();

      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

   public Collection<DefectItem> getLoadedDefectItems() {
      return ((DefectContentProvider) getContentProvider()).getRootSet();
   }

   public void add(Collection<DefectItem> defectItems) {
      if (((DefectContentProvider) getContentProvider()) != null) ((DefectContentProvider) getContentProvider()).add(defectItems);
   }

   public void set(Collection<? extends DefectItem> defectItems) {
      if (((DefectContentProvider) getContentProvider()) != null) ((DefectContentProvider) getContentProvider()).set(defectItems);
   }

   public void clear() {
      if (((DefectContentProvider) getContentProvider()) != null) ((DefectContentProvider) getContentProvider()).clear();
   }

   /**
    * Release resources
    */
   @Override
   public void dispose() {
      // Dispose of the table objects is done through separate dispose listener off tree
      // Tell the label provider to release its ressources
      getLabelProvider().dispose();
   }

   public ArrayList<DefectItem> getSelectedDefectItems() {
      ArrayList<DefectItem> arts = new ArrayList<DefectItem>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items)
         arts.add((DefectItem) item.getData());
      return arts;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer#handleLeftClickInIconArea(org.eclipse.swt.widgets.TreeColumn, org.eclipse.swt.widgets.TreeItem)
    */
   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
      if (xCol.equals(DefectXViewerFactory.User_Col)) {
         return handleAltLeftClick(treeColumn, treeItem);
      } else if (xCol.equals(DefectXViewerFactory.Injection_Activity_Col)) {
         return handleAltLeftClick(treeColumn, treeItem);
      }
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer#handleLeftClick(org.eclipse.swt.widgets.TreeColumn, org.eclipse.swt.widgets.TreeItem)
    */
   @Override
   public boolean handleLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (!xDefectViewer.isEditable()) {
         return false;
      }
      try {
         XViewerColumn aCol = (XViewerColumn) treeColumn.getData();
         DefectItem defectItem = (DefectItem) treeItem.getData();
         boolean modified = false;
         if (aCol.equals(DefectXViewerFactory.Closed_Col)) {
            modified = true;
            defectItem.setClosed(!defectItem.isClosed());
         }
         if (aCol.equals(DefectXViewerFactory.Severity_Col)) {
            modified = handleAltLeftClick(treeColumn, treeItem);
         }
         if (aCol.equals(DefectXViewerFactory.Disposition_Col)) {
            modified = handleAltLeftClick(treeColumn, treeItem);
         }
         if (modified) {
            SkynetTransaction transaction =
                  new SkynetTransaction(xDefectViewer.getReviewArt().getArtifact().getBranch());
            xDefectViewer.getReviewArt().getDefectManager().addOrUpdateDefectItem(defectItem, false, transaction);
            transaction.execute();
            xDefectViewer.refresh();
            xDefectViewer.notifyXModifiedListeners();
            update(defectItem, null);
            return true;
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.viewer.XViewer#handleAltLeftClick(org.eclipse.swt.widgets.TreeColumn,
    *      org.eclipse.swt.widgets.TreeItem)
    */
   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (!xDefectViewer.isEditable()) {
         return false;
      }
      try {
         // System.out.println("Column " + treeColumn.getText() + " item " +
         // treeItem);
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         DefectItem defectItem = (DefectItem) treeItem.getData();
         boolean modified = false;
         if (xCol.equals(DefectXViewerFactory.Created_Date_Col)) {
            Date selDate = XPromptChange.promptChangeDate(xCol.getName(), defectItem.getDate());
            if (selDate != null) {
               modified = true;
               defectItem.setDate(selDate);
            }
         } else if (xCol.equals(DefectXViewerFactory.Closed_Col)) {
            Boolean closed = XPromptChange.promptChangeBoolean(xCol.getName(), xCol.getName(), defectItem.isClosed());
            if (closed != null && (defectItem.isClosed() != closed)) {
               modified = true;
               defectItem.setClosed(closed);
            }
         } else if (xCol.equals(DefectXViewerFactory.Description_Col)) {
            String desc =
                  XPromptChange.promptChangeString(xCol.getName(), defectItem.getDescription(), null, Option.MULTI_LINE);
            if (desc != null && !defectItem.getDescription().equals(desc)) {
               modified = true;
               defectItem.setDescription(desc);
            }
         } else if (xCol.equals(DefectXViewerFactory.Resolution_Col)) {
            String resolution =
                  XPromptChange.promptChangeString(xCol.getName(), defectItem.getResolution(), null, Option.MULTI_LINE);
            if (resolution != null && !defectItem.getResolution().equals(resolution)) {
               modified = true;
               defectItem.setResolution(resolution);
            }
         } else if (xCol.equals(DefectXViewerFactory.Location_Col)) {
            String desc =
                  XPromptChange.promptChangeString(xCol.getName(), defectItem.getLocation(), null, Option.MULTI_LINE);
            if (desc != null && !defectItem.getLocation().equals(desc)) {
               modified = true;
               defectItem.setLocation(desc);
            }
         } else if (xCol.equals(DefectXViewerFactory.User_Col)) {
            UserListDialog ld = new UserListDialog(Display.getCurrent().getActiveShell(), "Select New User");
            int result = ld.open();
            if (result == 0) {
               User selectedUser = ld.getSelection();
               if (selectedUser != null && defectItem.getUser() != selectedUser) {
                  modified = true;
                  defectItem.setUser(selectedUser);
               }
            }
         } else if (xCol.equals(DefectXViewerFactory.Severity_Col)) {
            EnumStringSingleSelectionDialog enumDialog =
                  XPromptChange.promptChangeSingleSelectEnumeration(xCol.getName(), Severity.strValues(),
                        defectItem.getSeverity().name());
            if (enumDialog != null) {
               if (enumDialog.getResult()[0] != null) {
                  modified = true;
                  defectItem.setSeverity(Severity.valueOf((String) enumDialog.getResult()[0]));
               }
            }
         } else if (xCol.equals(DefectXViewerFactory.Disposition_Col)) {
            EnumStringSingleSelectionDialog enumDialog =
                  XPromptChange.promptChangeSingleSelectEnumeration(xCol.getName(), Disposition.strValues(),
                        defectItem.getDisposition().name());
            if (enumDialog != null) {
               if (enumDialog.getResult()[0] != null) {
                  modified = true;
                  defectItem.setDisposition(Disposition.valueOf((String) enumDialog.getResult()[0]));
               }
            }
         } else if (xCol.equals(DefectXViewerFactory.Injection_Activity_Col)) {
            EnumStringSingleSelectionDialog enumDialog =
                  XPromptChange.promptChangeSingleSelectEnumeration(xCol.getName(), InjectionActivity.strValues(),
                        defectItem.getInjectionActivity().name());
            if (enumDialog != null) {
               if (enumDialog.getResult()[0] != null) {
                  modified = true;
                  defectItem.setInjectionActivity(InjectionActivity.valueOf((String) enumDialog.getResult()[0]));
               }
            }
         } else
            throw new OseeStateException("Unhandled defect column");

         if (modified) {
            SkynetTransaction transaction =
                  new SkynetTransaction(xDefectViewer.getReviewArt().getArtifact().getBranch());
            xDefectViewer.getReviewArt().getDefectManager().addOrUpdateDefectItem(defectItem, false, transaction);
            transaction.execute();
            xDefectViewer.notifyXModifiedListeners();
            update(defectItem, null);
            return true;
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiDebug.class, ex, true);
      }
      return false;
   }
}
