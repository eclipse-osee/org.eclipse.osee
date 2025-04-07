/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.results.table.xresults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.data.ArtifactResultRow;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ISelectedArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.ArtifactDoubleClick;
import org.eclipse.osee.framework.ui.skynet.OpenContributionItem;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class ResultsXViewer extends XViewer implements ISelectedArtifacts {

   List<IResultsEditorTableListener> listeners = new ArrayList<>();

   public ResultsXViewer(Composite parent, int style, List<XViewerColumn> xColumns, XViewerFactory xViewerFactory) {
      super(parent, style, xViewerFactory);
   }

   public void addListener(IResultsEditorTableListener listener) {
      listeners.add(listener);
   }

   @Override
   public void handleDoubleClick() {
      if (listeners.isEmpty()) {
         if (getSelectedRows().size() > 0) {

            Object data = getSelectedRows().iterator().next().getData();
            if (data instanceof Artifact) {
               Artifact artifact = (Artifact) data;
               ArtifactDoubleClick.open(artifact);
               return;
            } else if (data instanceof ArtifactToken) {
               Artifact artifact = null;
               if (data instanceof Artifact) {
                  artifact = (Artifact) data;
               } else {
                  artifact = ArtifactQuery.getArtifactFromToken((ArtifactToken) data);
               }
               ArtifactDoubleClick.open(artifact);
               return;
            } else if (data instanceof ArtifactResultRow) {
               ArtifactResultRow artRow = (ArtifactResultRow) data;
               ArtifactToken artifact = artRow.getArtifact();
               if (artifact != null) {
                  Artifact art = ArtifactQuery.getArtifactFromId(artifact, artRow.getBranch());
                  ArtifactDoubleClick.open(art);
               }
            }
         }
      } else {
         for (IResultsEditorTableListener listener : listeners) {
            listener.handleDoubleClick(getSelectedRows());
         }
      }
   }

   public ArrayList<ResultsXViewerRow> getSelectedRows() {
      ArrayList<ResultsXViewerRow> arts = new ArrayList<>();
      TreeItem items[] = getTree().getSelection();
      for (TreeItem item : items) {
         arts.add((ResultsXViewerRow) item.getData());
      }
      return arts;
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      getMenuManager().addMenuListener(new IMenuListener() {
         @Override
         public void menuAboutToShow(IMenuManager manager) {
            getPopupMenu();
         }
      });
   }

   private boolean isArtifactContent() {
      TreeItem items[] = getTree().getSelection();
      for (TreeItem item : items) {
         if (item.getData() instanceof ResultsXViewerRow) {
            if (((ResultsXViewerRow) item.getData()).getData() instanceof Artifact) {
               return true;
            }
         }
      }
      return false;
   }

   private void getPopupMenu() {
      MenuManager menuManager = getMenuManager();
      if (isArtifactContent()) {
         OpenContributionItem contrib = new OpenContributionItem(getClass().getSimpleName() + ".open", this);
         contrib.fill(menuManager.getMenu(), -1);
         menuManager.insertBefore(XViewer.MENU_GROUP_PRE, contrib);
      }
   }

   @Override
   public Collection<Artifact> getSelectedArtifacts() {
      List<Artifact> selected = new LinkedList<>();
      TreeItem items[] = getTree().getSelection();
      for (TreeItem item : items) {
         if (item.getData() instanceof ResultsXViewerRow) {
            if (((ResultsXViewerRow) item.getData()).getData() instanceof Artifact) {
               selected.add((Artifact) ((ResultsXViewerRow) item.getData()).getData());
            }
         }
      }
      return selected;
   }

   @Override
   public boolean handleLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      boolean result = super.handleLeftClick(treeColumn, treeItem);
      for (IResultsEditorTableListener listener : listeners) {
         listener.handleSelectionListener(getSelectedRows());
      }
      return result;
   }

}
