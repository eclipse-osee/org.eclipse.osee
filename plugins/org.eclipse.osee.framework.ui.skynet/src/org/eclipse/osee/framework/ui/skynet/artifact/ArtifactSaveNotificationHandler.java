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
package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactDecoratorPreferences;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactViewerSorter;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.OSEECheckedFilteredTree;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.SimpleCheckFilteredTreeDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

/**
 * This handler will find any artifacts in the artifact cache that were dirty and allow user to persist before shutdown.
 * 
 * @author Donald G. Dunne
 */
public class ArtifactSaveNotificationHandler implements IWorkbenchListener {

   @Override
   public void postShutdown(IWorkbench arg0) {
      // do nothing
   }

   @Override
   public boolean preShutdown(IWorkbench arg0, boolean force) {
      boolean isShutdownAllowed = true;
      OseeLog.log(SkynetGuiPlugin.class, Level.INFO, "Verifying Artifact Persistence");
      Collection<Artifact> dirtyArts = ArtifactCache.getDirtyArtifacts();

      if (!dirtyArts.isEmpty()) {

         if (RenderingUtil.arePopupsAllowed()) {
            SimpleCheckFilteredTreeDialog dialog = createDialog(dirtyArts);
            int result = dialog.open();
            if (result == Window.OK) {
               isShutdownAllowed = true;
               Object[] selected = dialog.getResult();
               if (selected.length > 0) {
                  Collection<Artifact> itemsToSave = new HashSet<Artifact>();
                  for (Object object : selected) {
                     itemsToSave.add((Artifact) object);
                  }

                  try {
                     HashCollection<Branch, Artifact> branchMap = Artifacts.getBranchArtifactMap(itemsToSave);
                     for (Entry<Branch, Collection<Artifact>> entry : branchMap.entrySet()) {
                        Branch branch = entry.getKey();
                        Collection<Artifact> arts = entry.getValue();
                        OseeLog.log(SkynetGuiPlugin.class, Level.INFO,
                           String.format("Persisting [%d] unsaved artifacts for branch [%s]", arts.size(), branch));
                        Artifacts.persistInTransaction(arts);
                     }
                  } catch (OseeCoreException ex) {
                     OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            } else {
               isShutdownAllowed = false;
               HashCollection<Branch, Artifact> branchMap = Artifacts.getBranchArtifactMap(dirtyArts);

               for (Branch branch : branchMap.keySet()) {
                  MassArtifactEditor.editArtifacts(String.format("Unsaved Artifacts for Branch [%s]", branch),
                     branchMap.getValues(branch));
               }
            }
         } else {
            // For Test Purposes
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP,
               "Found dirty artifacts after tests: " + dirtyArts);
         }
      }

      return force || isShutdownAllowed;
   }

   private SimpleCheckFilteredTreeDialog createDialog(Collection<Artifact> dirtyArts) {
      ArtifactDecoratorPreferences preferences = new ArtifactDecoratorPreferences();
      preferences.setShowArtBranch(true);
      preferences.setShowArtType(true);

      CheckFilteredTreeDialog dialog =
         new CheckFilteredTreeDialog(
            "Unsaved Artifacts Detected",
            "Some artifacts have not been saved.\n\nCheck artifacts to save (if any) and select Ok to continue shutdown. Select Cancel to stop shutdown.",
            new ArrayTreeContentProvider(), new ArtifactLabelProvider(preferences), new ArtifactViewerSorter(), 0,
            Integer.MAX_VALUE);
      dialog.setInput(dirtyArts);

      return dialog;
   }

   private final static class CheckFilteredTreeDialog extends SimpleCheckFilteredTreeDialog {

      public CheckFilteredTreeDialog(String title, String message, ITreeContentProvider contentProvider, LabelProvider labelProvider, ViewerSorter viewerSorter, int minSelectionRequired, int maxSelectionRequired) {
         super(title, message, contentProvider, labelProvider, viewerSorter, minSelectionRequired, maxSelectionRequired);
      }

      @Override
      protected Control createCustomArea(Composite parent) {
         Composite control = (Composite) super.createCustomArea(parent);

         Composite selectionComp = new Composite(control, SWT.NONE);
         selectionComp.setLayout(ALayout.getZeroMarginLayout(2, true));
         selectionComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

         Button selectAll = new Button(selectionComp, SWT.PUSH);
         selectAll.setText("Select All");
         selectAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
         selectAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               setChecked(true);
            }

         });

         Button deselectAll = new Button(selectionComp, SWT.PUSH);
         deselectAll.setText("De-select All");
         deselectAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
         deselectAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               setChecked(false);
            }

         });
         return control;
      }

      private void setChecked(boolean isChecked) {
         OSEECheckedFilteredTree viewer = getTreeViewer();

         viewer.getFilterControl().setText("");
         viewer.getPatternFilter().setPattern("");
         viewer.getViewer().refresh();

         if (isChecked) {
            Collection<Object> objects = viewer.getChecked();
            TreeItem[] items = viewer.getViewer().getTree().getItems();
            for (TreeItem item : items) {
               item.setChecked(true);
               objects.add(item.getData());
            }
         } else {
            viewer.clearChecked();
         }
         viewer.getViewer().refresh();
      }
   }

}
