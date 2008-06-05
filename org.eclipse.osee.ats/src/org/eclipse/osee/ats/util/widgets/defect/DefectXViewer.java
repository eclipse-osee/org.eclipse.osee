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
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.skynet.util.EnumStringSingleSelectionDialog;
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

   private static String NAMESPACE = "osee.ats.DefectXViewer";
   private final XDefectViewer xDefectViewer;

   /**
    * @param parent
    * @param style
    */
   public DefectXViewer(Composite parent, int style, XDefectViewer xViewer) {
      this(parent, style, NAMESPACE, new DefectXViewerFactory(), xViewer);
   }

   public DefectXViewer(Composite parent, int style, String nameSpace, IXViewerFactory xViewerFactory, XDefectViewer xDefectViewer) {
      super(parent, style, nameSpace, xViewerFactory);
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

   @Override
   public boolean isColumnMultiEditable(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      DefectColumn aCol = DefectColumn.getAtsXColumn((XViewerColumn) treeColumn.getData());
      XViewerColumn xCol = getCustomize().getCurrentCustData().getColumnData().getXColumn(aCol.getName());
      if (!xCol.isShow() || !aCol.isMultiColumnEditable()) return false;
      return true;
   }

   @Override
   public boolean isColumnMultiEditEnabled() {
      return true;
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
      DefectColumn aCol = DefectColumn.getAtsXColumn(xCol);
      if (aCol == DefectColumn.User_Col) {
         return handleAltLeftClick(treeColumn, treeItem);
      } else if (aCol == DefectColumn.Injection_Activity_Col) {
         return handleAltLeftClick(treeColumn, treeItem);
      }
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer#handleLeftClick(org.eclipse.swt.widgets.TreeColumn, org.eclipse.swt.widgets.TreeItem)
    */
   @Override
   public boolean handleLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         DefectColumn aCol = DefectColumn.getAtsXColumn(xCol);
         DefectItem defectItem = (DefectItem) treeItem.getData();
         boolean modified = false;
         if (aCol == DefectColumn.Closed_Col) {
            modified = true;
            defectItem.setClosed(!defectItem.isClosed());
         }
         if (aCol == DefectColumn.Severity_Col) {
            return handleAltLeftClick(treeColumn, treeItem);
         }
         if (aCol == DefectColumn.Disposition_Col) {
            return handleAltLeftClick(treeColumn, treeItem);
         }
         if (modified) {
            xDefectViewer.getReviewArt().getDefectManager().addOrUpdateDefectItem(defectItem, false);
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
      try {
         // System.out.println("Column " + treeColumn.getText() + " item " +
         // treeItem);
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         DefectColumn aCol = DefectColumn.getAtsXColumn(xCol);
         DefectItem defectItem = (DefectItem) treeItem.getData();
         boolean modified = false;
         if (aCol == DefectColumn.Created_Date_Col) {
            Date selDate = XPromptChange.promptChangeDate(aCol.getName(), defectItem.getDate());
            if (selDate != null) {
               modified = true;
               defectItem.setDate(selDate);
            }
         } else if (aCol == DefectColumn.Closed_Col) {
            Boolean closed = XPromptChange.promptChangeBoolean(aCol.getName(), aCol.getName(), defectItem.isClosed());
            if (closed != null && (defectItem.isClosed() != closed)) {
               modified = true;
               defectItem.setClosed(closed);
            }
         } else if (aCol == DefectColumn.Description_Col) {
            String desc =
                  XPromptChange.promptChangeString(aCol.getName(), defectItem.getDescription(), null, Option.MULTI_LINE);
            if (desc != null && !defectItem.getDescription().equals(desc)) {
               modified = true;
               defectItem.setDescription(desc);
            }
         } else if (aCol == DefectColumn.Resolution_Col) {
            String resolution =
                  XPromptChange.promptChangeString(aCol.getName(), defectItem.getResolution(), null, Option.MULTI_LINE);
            if (resolution != null && !defectItem.getResolution().equals(resolution)) {
               modified = true;
               defectItem.setResolution(resolution);
            }
         } else if (aCol == DefectColumn.Location_Col) {
            String desc =
                  XPromptChange.promptChangeString(aCol.getName(), defectItem.getLocation(), null, Option.MULTI_LINE);
            if (desc != null && !defectItem.getLocation().equals(desc)) {
               modified = true;
               defectItem.setLocation(desc);
            }
         } else if (aCol == DefectColumn.User_Col) {
            UserListDialog ld = new UserListDialog(Display.getCurrent().getActiveShell(), "Select New User");
            int result = ld.open();
            if (result == 0) {
               User selectedUser = (User) ld.getSelection();
               if (selectedUser != null && defectItem.getUser() != selectedUser) {
                  modified = true;
                  defectItem.setUser(selectedUser);
               }
            }
         } else if (aCol == DefectColumn.Severity_Col) {
            EnumStringSingleSelectionDialog enumDialog =
                  XPromptChange.promptChangeSingleSelectEnumeration(aCol.getName(), Severity.strValues(),
                        defectItem.getSeverity().name());
            if (enumDialog != null) {
               if (enumDialog.getResult()[0] != null) {
                  modified = true;
                  defectItem.setSeverity(Severity.valueOf((String) enumDialog.getResult()[0]));
               }
            }
         } else if (aCol == DefectColumn.Disposition_Col) {
            EnumStringSingleSelectionDialog enumDialog =
                  XPromptChange.promptChangeSingleSelectEnumeration(aCol.getName(), Disposition.strValues(),
                        defectItem.getDisposition().name());
            if (enumDialog != null) {
               if (enumDialog.getResult()[0] != null) {
                  modified = true;
                  defectItem.setDisposition(Disposition.valueOf((String) enumDialog.getResult()[0]));
               }
            }
         } else if (aCol == DefectColumn.Injection_Activity_Col) {
            EnumStringSingleSelectionDialog enumDialog =
                  XPromptChange.promptChangeSingleSelectEnumeration(aCol.getName(), InjectionActivity.strValues(),
                        defectItem.getInjectionActivity().name());
            if (enumDialog != null) {
               if (enumDialog.getResult()[0] != null) {
                  modified = true;
                  defectItem.setInjectionActivity(InjectionActivity.valueOf((String) enumDialog.getResult()[0]));
               }
            }
         } else
            throw new IllegalStateException("Unhandled defect column");

         if (modified) {
            xDefectViewer.getReviewArt().getDefectManager().addOrUpdateDefectItem(defectItem, false);
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
