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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.define.AUTOGEN.OteAttributeTypes;
import org.eclipse.osee.ote.ui.define.OteDefineImage;
import org.eclipse.osee.ote.ui.define.OteUiDefinePlugin;
import org.eclipse.osee.ote.ui.define.jobs.CommitTestRunJob;
import org.eclipse.osee.ote.ui.define.utilities.CommitConfiguration;
import org.eclipse.osee.ote.ui.define.viewers.actions.AbstractActionHandler;
import org.eclipse.osee.ote.ui.define.viewers.actions.LaunchReportsAction;
import org.eclipse.osee.ote.ui.define.viewers.actions.OpenAssociatedOutfile;
import org.eclipse.osee.ote.ui.define.viewers.actions.OpenAssociatedScript;
import org.eclipse.osee.ote.ui.define.viewers.actions.OpenInArtifactEditor;
import org.eclipse.osee.ote.ui.define.viewers.data.ArtifactItem;
import org.eclipse.osee.ote.ui.define.viewers.data.DataItemContentProvider;
import org.eclipse.osee.ote.ui.define.viewers.data.DataItemLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Roberto E. Escobar
 */
public class TestRunXViewer extends XViewer {

   private static final ImageDescriptor COMMIT_IMAGE = ImageManager.getImageDescriptor(OteDefineImage.COMMIT);
   private static final ImageDescriptor REMOVE_IMAGE = ImageManager.getImageDescriptor(OteDefineImage.REMOVE);
   private static final ImageDescriptor REMOVE_ALL_IMAGE = ImageManager.getImageDescriptor(OteDefineImage.REMOVE_ALL);
   private XViewerDataManager dataManager;
   private final List<AbstractActionHandler> actionList = new ArrayList<>();
   private Action editDisposition;

   public TestRunXViewer(Composite parent) {
      super(parent, //SWT.VIRTUAL |
         SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, new TestRunXViewerFactory());
      setSorter(null);
      setContentProvider(new DataItemContentProvider());
      setLabelProvider(new DataItemLabelProvider(this));
      setUseHashlookup(true);
      setupActions();
      getMenuManager().addMenuListener(new IMenuListener() {
         @Override
         public void menuAboutToShow(IMenuManager manager) {
            customActions();
         }
      });
   }

   private void customActions() {
      MenuManager manager = getMenuManager();
      manager.insertBefore(XViewer.MENU_GROUP_PRE, editDisposition);
      manager.insertBefore(XViewer.MENU_GROUP_PRE, new Separator());
      manager.add(new Separator());
      for (int index = 0; index < actionList.size(); index++) {
         Action action = actionList.get(index);
         if (index + 1 >= actionList.size() || index + 2 >= actionList.size() || index == 3) {
            manager.add(new Separator());
         }
         manager.add(action);
      }
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      this.dataManager = new XViewerDataManager(this);
      new DragDropHandler(dataManager);
   }

   public void registerListener(IDataChangedListener listener) {
      dataManager.registerListener(listener);
   }

   public void deRegisterListener(IDataChangedListener listener) {
      dataManager.deRegisterListener(listener);
   }

   private void setupActions() {
      try {
         editDisposition = new Action("Edit Disposition", IAction.AS_PUSH_BUTTON) {
            @Override
            public void run() {
               try {
                  handleChangeDisposition();
               } catch (Exception ex) {
                  OseeLog.log(OteUiDefinePlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         };

         actionList.add(new OpenInArtifactEditor(this, "Open as Artifact"));
         actionList.add(new OpenAssociatedOutfile(this, "Open Outfile"));
         actionList.add(new OpenAssociatedScript(this, "Open Script"));
         actionList.add(new RemoveAction("Remove", REMOVE_IMAGE));
         actionList.add(new RemoveAllAction("Remove All", REMOVE_ALL_IMAGE));
         actionList.add(new LaunchReportsAction(this, "Reports..."));
         actionList.add(new CommitAction("Commit Test Runs...", COMMIT_IMAGE));
      } catch (Exception ex) {
         OseeLog.log(OteUiDefinePlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void refresh() {
      super.refresh();
      try {
         if (getTree() == null || getTree().isDisposed() || actionList == null) {
            return;
         }
         for (AbstractActionHandler action : actionList) {
            action.updateState();
         }
      } catch (Exception ex) {
         OseeLog.log(OteUiDefinePlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private final class CommitAction extends AbstractActionHandler {

      public CommitAction(String text, ImageDescriptor image) throws Exception {
         super(TestRunXViewer.this, text, image);
      }

      @Override
      public void run() {
         Artifact[] preSelected = dataManager.getSelectedForCommit();
         Artifact[] unSelectable = dataManager.getUnCommitable();

         Job job = new CommitTestRunJob(getAllItemsList(), preSelected, unSelectable, isOverrideAllowed());
         job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
               Artifact[] committed = ((CommitTestRunJob) event.getJob()).getCommitted();
               dataManager.removeFromCommitable(Arrays.asList(committed));
            }
         });
         job.schedule();
      }

      @Override
      public void updateState() {
         boolean haveUnCommitableItems = dataManager.getUnCommitable().length > 0;
         boolean haveCommitableItems = dataManager.getAllCommitable().length > 0;
         this.setEnabled(haveCommitableItems || CommitConfiguration.isCommitOverrideAllowed() && haveUnCommitableItems);
      }

      private boolean isOverrideAllowed() {
         return CommitConfiguration.isCommitOverrideAllowed();
      }

      private Artifact[] getAllItemsList() {
         List<Artifact> allItems = new ArrayList<>();
         allItems.addAll(Arrays.asList(dataManager.getAllCommitable()));
         if (isOverrideAllowed() != false) {
            allItems.addAll(Arrays.asList(dataManager.getUnCommitable()));
         }
         return allItems.toArray(new Artifact[allItems.size()]);
      }
   }

   private final class RemoveAllAction extends AbstractActionHandler {

      public RemoveAllAction(String text, ImageDescriptor image) throws Exception {
         super(TestRunXViewer.this, text, image);
      }

      @Override
      public void run() {
         dataManager.removeAll();
      }

      @Override
      public void updateState() {
         this.setEnabled(dataManager.isEmpty() != true);
      }
   }

   private final class RemoveAction extends AbstractActionHandler {

      public RemoveAction(String text, ImageDescriptor image) throws Exception {
         super(TestRunXViewer.this, text, image);
      }

      @Override
      public void run() {
         dataManager.removeSelected();
      }

      @Override
      public void updateState() {
         this.setEnabled(getSelection().isEmpty() != true);
      }
   }

   public boolean handleChangeDisposition() {
      if (!getSelectedArtifacts().isEmpty()) {
         return setDisposition(getSelectedArtifacts());
      }
      return false;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
      Object holder = treeItem.getData();
      boolean returnValue = false;
      if (holder instanceof ArtifactItem) {
         if (xCol.equals(TestRunXViewerFactory.DISPOSITION)) {
            ArrayList<Artifact> list = new ArrayList<>();
            list.add(((ArtifactItem) holder).getData());
            try {
               returnValue = setDisposition(list);
            } catch (Exception ex) {
               OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex);
            }
         }
      }
      return returnValue;
   }

   private boolean setDisposition(ArrayList<Artifact> selectedArtifacts) {
      boolean returnValue = false;
      ArrayList<Artifact> dispositionArtifacts = new ArrayList<>();
      for (Artifact artifact : selectedArtifacts) {
         if (artifact.isOfType(CoreArtifactTypes.TestRun)) {
            String name = artifact.getName();
            if (!name.equals(Artifact.UNNAMED)) {
               Artifact dispoArtifact = null;
               try {
                  dispoArtifact = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.TestRunDisposition, name,
                     artifact.getBranch());
               } catch (ArtifactDoesNotExist ex) {
                  dispoArtifact =
                     ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestRunDisposition, artifact.getBranch());
                  dispoArtifact.setName(name);
               }
               if (dispoArtifact != null) {
                  dispositionArtifacts.add(dispoArtifact);
               }
            }
         }
      }
      try {
         returnValue = ArtifactPromptChange.promptChangeAttribute(OteAttributeTypes.TestDisposition,
            dispositionArtifacts, true, false);
         refresh();
      } catch (Exception ex) {
         OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex);
      }
      return returnValue;
   }

   public ArrayList<Artifact> getSelectedArtifacts() {
      ArrayList<Artifact> arts = new ArrayList<>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            if (item.getData() instanceof ArtifactItem) {
               arts.add(((ArtifactItem) item.getData()).getData());
            }
         }
      }
      return arts;
   }
}
