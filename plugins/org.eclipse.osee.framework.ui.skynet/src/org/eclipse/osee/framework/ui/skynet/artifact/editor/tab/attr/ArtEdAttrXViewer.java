/*********************************************************************
 * Copyright (c) 2020 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.action.EditAttributeAction;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class ArtEdAttrXViewer extends XViewer {

   private final Artifact artifact;

   public Artifact getArtifact() {
      return artifact;
   }

   private ArtEdAttrXViewerMenu attrMenu;
   private final Composite parent;

   public ArtEdAttrXViewer(Composite parent, int style, IOseeTreeReportProvider reportProvider, Artifact artifact) {
      this(parent, style, artifact, new ArtEdAttrXViewerFactory(reportProvider));
   }

   public ArtEdAttrXViewer(Composite parent, int style, Artifact artifact, IXViewerFactory xViewerFactory) {
      super(parent, style, xViewerFactory);
      this.parent = parent;
      this.artifact = artifact;
   }

   public void loadTable(Artifact artifact) {
      if (Widgets.isAccessible(parent)) {
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               setInput(artifact.getAttributes());
            }
         });
      }
   }

   @Override
   public void dispose() {
      // Dispose of the table objects is done through separate dispose listener off tree
      // Tell the label provider to release its resources
      getLabelProvider().dispose();
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         if (xCol.equals(ArtEdAttrXViewerFactory.Value)) {
            new EditAttributeAction(this).run();
            return true;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public List<Attribute<?>> getSelectedAttributes() {
      List<Attribute<?>> arts = new ArrayList<>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            arts.add((Attribute<?>) item.getData());
         }
      }
      return arts;
   }

   ArtEdAttrXViewerMenu getAttrMenu() {
      if (attrMenu == null) {
         attrMenu = new ArtEdAttrXViewerMenu(this, artifact);
         attrMenu.createMenuActions();
      }
      return attrMenu;
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();
      getAttrMenu().updateEditMenuActions();
      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

}
