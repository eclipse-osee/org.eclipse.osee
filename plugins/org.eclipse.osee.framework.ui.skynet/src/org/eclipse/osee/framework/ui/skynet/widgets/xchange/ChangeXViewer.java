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
package org.eclipse.osee.framework.ui.skynet.widgets.xchange;

import java.util.ArrayList;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.preferences.EditorsPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 * @author Jeff C. Phillips
 */
public class ChangeXViewer extends XViewer {

   private XChangeTextFilter xChangeTextFilter;

   public ChangeXViewer(Composite parent, int style, IXViewerFactory factory) {
      super(parent, style, factory);
   }

   @Override
   public void handleDoubleClick() {
      try {
         if (getSelectedChanges().isEmpty()) {
            return;
         }

         Change change = getSelectedChanges().iterator().next();
         Artifact artifact = (Artifact) ((IAdaptable) change).getAdapter(Artifact.class);

         if (artifact != null) {
            ArrayList<Artifact> artifacts = new ArrayList<Artifact>(1);
            artifacts.add(artifact);

            if (StaticIdManager.hasValue(UserManager.getUser(),
                  EditorsPreferencePage.PreviewOnDoubleClickForWordArtifacts)) {
               RendererManager.previewInJob(artifacts);
            } else {
               RendererManager.openInJob(artifacts, PresentationType.GENERALIZED_EDIT);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public ArrayList<Change> getSelectedChanges() {
      ArrayList<Change> arts = new ArrayList<Change>();
      TreeItem items[] = getTree().getSelection();

      if (items.length > 0) {
         for (TreeItem item : items) {
            arts.add((Change) item.getData());
         }
      }
      return arts;
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
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();
      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

   /**
    * Release resources
    */
   @Override
   public void dispose() {
      getLabelProvider().dispose();
   }

   @Override
   public String getStatusString() {
      if (isShowDocumentOrderFilter()) {
         return "[Show Document Order]";
      }
      return "";
   }

   @Override
   public XViewerTextFilter getXViewerTextFilter() {
      if (xChangeTextFilter == null) {
         xChangeTextFilter = new XChangeTextFilter(this);
      }
      return xChangeTextFilter;
   }

   public boolean isShowDocumentOrderFilter() {
      if (xChangeTextFilter == null) {
         xChangeTextFilter = new XChangeTextFilter(this);
      }
      return xChangeTextFilter.isShowDocumentOrderFilter();
   }

   public void setShowDocumentOrderFilter(boolean showDocumentOrderFilter) {
      if (xChangeTextFilter == null) {
         xChangeTextFilter = new XChangeTextFilter(this);
      }
      xChangeTextFilter.setShowDocumentOrderFilter(showDocumentOrderFilter);
   }

}
