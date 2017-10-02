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
package org.eclipse.osee.ote.ui.define.viewers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassXViewerFactory;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.define.artifacts.ArtifactTestRunOperator;
import org.eclipse.osee.ote.ui.define.viewers.data.ArtifactItem;
import org.eclipse.osee.ote.ui.define.viewers.data.BranchItem;
import org.eclipse.osee.ote.ui.define.viewers.data.DataItem;
import org.eclipse.osee.ote.ui.define.viewers.data.ScriptItem;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Roberto E. Escobar
 */
public class XViewerDataManager {

   private final XViewer xViewer;
   private final List<DataItem> commitableItems;
   private final List<DataItem> uncommitableItems;
   private final Map<BranchId, DataItem> branchMap;
   private final List<DataItem> backingData;
   private final Collection<IDataChangedListener> listeners;
   private boolean isInitialized;

   public XViewerDataManager(XViewer xViewer) {
      super();
      this.backingData = new ArrayList<>();
      this.isInitialized = false;
      this.xViewer = xViewer;
      this.branchMap = new HashMap<>();
      this.commitableItems = new ArrayList<>();
      this.uncommitableItems = new ArrayList<>();
      this.listeners = Collections.synchronizedSet(new HashSet<IDataChangedListener>());
   }

   private void ensureInitialized() {
      if (isInitialized != true) {
         isInitialized = true;
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               xViewer.setInput(backingData);
            }
         });
      }
   }

   public void addArtifacts(IProgressMonitor monitor, final List<Artifact> artifacts)  {
      if (branchMap.isEmpty()) {
         if (xViewer.getXViewerFactory() instanceof MassXViewerFactory) {
            ((MassXViewerFactory) xViewer.getXViewerFactory()).registerAllAttributeColumnsForArtifacts(artifacts, true);
         }
      }
      int total = artifacts.size();
      for (int index = 0; index < artifacts.size(); index++) {
         Artifact artifact = artifacts.get(index);
         monitor.subTask(String.format("Adding to Table: [%s] [%s of %s]", artifact.getName(), index + 1, total));

         DataItem branchItem = getBranchItem(artifact);
         DataItem scriptItem = getScriptItem(branchItem, artifact);

         ArtifactItem tempItem = new ArtifactItem(xViewer, artifact, null);

         DataItem itemFound = scriptItem.getChild(tempItem.getKey());
         if (itemFound == null) {
            scriptItem.addChild(tempItem.getKey(), tempItem);
            tempItem.setParent(scriptItem);
            ArtifactTestRunOperator operator = tempItem.getOperator();
            if (operator.isFromLocalWorkspace()) {
               if (operator.isCommitAllowed()) {
                  commitableItems.add(tempItem);
               } else {
                  uncommitableItems.add(tempItem);
               }
            }
         }
         monitor.worked(1);
      }

      refresh();
   }

   private void refresh() {
      ensureInitialized();
      notifyOnDataChanged();

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            xViewer.refresh();
         };
      });
   }

   private DataItem getScriptItem(DataItem branchItem, Artifact artifact) {
      ArtifactTestRunOperator testRunArtifact = new ArtifactTestRunOperator(artifact);
      DataItem tempItem = new ScriptItem(testRunArtifact.getScriptUrl(), testRunArtifact.getScriptRevision(), null);
      DataItem scriptItem = branchItem.getChild(tempItem.getKey());
      if (scriptItem == null) {
         scriptItem = tempItem;
         scriptItem.setParent(branchItem);
         branchItem.addChild(scriptItem.getKey(), scriptItem);
      }
      return scriptItem;
   }

   private DataItem getBranchItem(Artifact artifact) {
      IOseeBranch branch = artifact.getBranchToken();
      DataItem toReturn = branchMap.get(branch);
      if (toReturn == null) {
         toReturn = new BranchItem(branch, null);
         branchMap.put(branch, toReturn);
         backingData.add(toReturn);
      }
      return toReturn;
   }

   private void removeHelper(DataItem item) {
      if (item != null) {
         DataItem parent = item.getParent();
         if (parent != null) {
            parent.removeChild(item);
            if (parent.hasChildren() != true) {
               removeHelper(parent);
               parent.dispose();
            }
         } else {
            branchMap.remove(item.getKey());
            backingData.remove(item);
         }
      }
   }

   public void removeSelected() {
      List<DataItem> items = getSelected();
      for (DataItem item : items) {
         removeHelper(item);
         item.dispose();
         commitableItems.remove(item);
         uncommitableItems.remove(item);
      }
      refresh();
   }

   public void removeAll() {
      commitableItems.clear();
      uncommitableItems.clear();
      for (DataItem dataItem : branchMap.values()) {
         dataItem.dispose();
      }
      branchMap.clear();
      backingData.clear();
      refresh();
   }

   public Control getControl() {
      return xViewer.getTree();
   }

   private List<DataItem> getSelected() {
      List<DataItem> toReturn = new ArrayList<>();
      TreeItem items[] = xViewer.getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            toReturn.add((DataItem) item.getData());
         }
      }
      return toReturn;
   }

   public List<Artifact> getSelectedArtifacts() {
      List<Artifact> toReturn = new ArrayList<>();
      for (DataItem item : getSelected()) {
         Object object = item.getData();
         if (object instanceof Artifact) {
            toReturn.add((Artifact) object);
         }
      }
      return toReturn;
   }

   public Artifact[] getUnCommitable() {
      return getArtifactsFromDataItemList(uncommitableItems);
   }

   public Artifact[] getAllCommitable() {
      return getArtifactsFromDataItemList(commitableItems);
   }

   private Artifact[] getArtifactsFromDataItemList(List<DataItem> sourceList) {
      List<Artifact> toReturn = new ArrayList<>();
      for (DataItem item : sourceList) {
         toReturn.add((Artifact) item.getData());
      }
      return toReturn.toArray(new Artifact[toReturn.size()]);
   }

   public Artifact[] getSelectedForCommit() {
      List<Artifact> toReturn = new ArrayList<>();
      for (DataItem item : getSelected()) {
         if (commitableItems.contains(item)) {
            toReturn.add((Artifact) item.getData());
         }
      }
      return toReturn.toArray(new Artifact[toReturn.size()]);
   }

   public void removeFromCommitable(final Collection<Artifact> artifacts) {
      removeArtifactsFromDataItemList(commitableItems, artifacts);
      removeArtifactsFromDataItemList(uncommitableItems, artifacts);
      refresh();
   }

   private void removeArtifactsFromDataItemList(List<DataItem> sourceList, final Collection<Artifact> artifactsToRemove) {
      List<DataItem> itemsToRemove = new ArrayList<>();
      for (DataItem item : sourceList) {
         Object object = item.getData();
         if (artifactsToRemove.contains(object)) {
            itemsToRemove.add(item);
         }
      }
      sourceList.removeAll(itemsToRemove);
   }

   private void notifyOnDataChanged() {
      synchronized (listeners) {
         for (IDataChangedListener listener : listeners) {
            listener.onDataChanged();
         }
      }
   }

   public void registerListener(IDataChangedListener listener) {
      synchronized (listeners) {
         listeners.add(listener);
      }
   }

   public void deRegisterListener(IDataChangedListener listener) {
      synchronized (listeners) {
         if (listeners.contains(listener)) {
            listeners.remove(listener);
         }
      }
   }

   public boolean isEmpty() {
      return branchMap.isEmpty();
   }
}
