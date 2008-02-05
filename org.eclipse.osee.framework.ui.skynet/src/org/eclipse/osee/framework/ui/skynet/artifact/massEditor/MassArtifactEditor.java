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
package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.DefaultBranchChangedEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Donald G. Dunne
 */
public class MassArtifactEditor extends AbstractArtifactEditor implements IDirtiableEditor, IEventReceiver, IActionable {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.massEditor.MassArtifactEditor";
   private int artifactsPageIndex;
   private Collection<? extends Artifact> artifacts = new HashSet<Artifact>();
   private MassXViewer xViewer;
   private Label branchLabel;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void doSave(IProgressMonitor monitor) {
      for (Artifact art : artifacts)
         try {
            art.persist(true);
         } catch (SQLException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, false);
         }
      onDirtied();
   }

   public static void editArtifacts(final String name, final Collection<? extends Artifact> artifacts) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               AWorkbench.getActivePage().openEditor(new MassArtifactEditorInput(name, artifacts), EDITOR_ID);
            } catch (PartInitException ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
      });
   }

   public static void editArtifact(final Artifact artifact) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               AWorkbench.getActivePage().openEditor(
                     new MassArtifactEditorInput("", Arrays.asList(new Artifact[] {artifact})), EDITOR_ID);
            } catch (PartInitException ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
      });
   }

   public void createTaskActionBar(Composite parent) {

      // Button composite for state transitions, etc
      Composite bComp = new Composite(parent, SWT.NONE);
      // bComp.setBackground(mainSComp.getDisplay().getSystemColor(SWT.COLOR_CYAN));
      bComp.setLayout(ALayout.getZeroMarginLayout(2, false));
      bComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Composite leftComp = new Composite(bComp, SWT.NONE);
      leftComp.setLayout(new GridLayout());
      leftComp.setLayoutData(new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL));

      branchLabel = new Label(leftComp, SWT.NONE);
      branchLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Composite rightComp = new Composite(bComp, SWT.NONE);
      rightComp.setLayout(new GridLayout());
      rightComp.setLayoutData(new GridData(GridData.END));

      ToolBar toolBar = new ToolBar(rightComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);
      ToolItem item = null;

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("refresh.gif"));
      item.setToolTipText("Refresh");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            xViewer.refresh();
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("customize.gif"));
      item.setToolTipText("Customize Table");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            xViewer.getCustomize().handleTableCustomization();
         }
      });

      OseeAts.addButtonToEditorToolBar(this, SkynetGuiPlugin.getInstance(), toolBar, EDITOR_ID, "Mass Artifact Editor");
   }

   public static void editArtifacts(MassArtifactEditorInput input) {
      IWorkbenchPage page = AWorkbench.getActivePage();
      try {
         page.openEditor(input, EDITOR_ID);
      } catch (PartInitException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   public boolean isSaveOnCloseNeeded() {
      return isDirty();
   }

   @Override
   public void dispose() {
      super.dispose();
      for (Artifact taskArt : artifacts)
         try {
            if (taskArt != null && !taskArt.isDeleted() && taskArt.isDirty()) taskArt.revert();
         } catch (SQLException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, false);
         }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
    */
   @Override
   public boolean isDirty() {
      for (Artifact taskArt : artifacts)
         if (taskArt.isDeleted())
            continue;
         else
            try {
               if (taskArt.isDirty()) return true;
            } catch (SQLException ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, false);
            }
      return false;
   }

   public String toString() {
      return "MassArtifactEditor";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
    */
   @Override
   protected void addPages() {

      IEditorInput editorInput = getEditorInput();
      if (editorInput instanceof MassArtifactEditorInput) {
         MassArtifactEditorInput aei = (MassArtifactEditorInput) editorInput;
         artifacts = ((MassArtifactEditorInput) aei).getArtifacts();
      } else
         throw new IllegalArgumentException("Editor Input not TaskEditorInput");

      if (((MassArtifactEditorInput) editorInput).getName().equals(""))
         setPartName("Mass Artifact Editor");
      else
         setPartName(((MassArtifactEditorInput) editorInput).getName());

      SkynetGuiPlugin.getInstance().setHelp(getContainer(), "mass_artifact_editor");

      Composite comp = new Composite(getContainer(), SWT.NONE);
      comp.setLayout(new GridLayout(1, true));
      comp.setLayoutData(new GridData(GridData.FILL_BOTH));

      createTaskActionBar(comp);

      xViewer = new MassXViewer(comp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this);
      xViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
      xViewer.setContentProvider(new org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassContentProvider(
            xViewer));
      xViewer.setLabelProvider(new org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassLabelProvider(xViewer));
      branchLabel.setText("Branch: " + ((MassArtifactEditorInput) editorInput).getArtifacts().iterator().next().getBranch().getBranchName());
      artifactsPageIndex = addPage(comp);
      setPageText(artifactsPageIndex, "Artifacts");

      Tree tree = xViewer.getTree();
      GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);
      SkynetEventManager.getInstance().register(DefaultBranchChangedEvent.class, this);

      setActivePage(artifactsPageIndex);
      xViewer.set(((MassArtifactEditorInput) editorInput).getArtifacts());
   }

   @Override
   public void onDirtied() {
      Displays.ensureInDisplayThread(new Runnable() {

         public void run() {
            firePropertyChange(PROP_DIRTY);
         }
      });
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.plugin.core.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.util.widgets.task.IXTaskViewer#getCurrentStateName()
    */
   public String getCurrentStateName() {
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.util.widgets.task.IXTaskViewer#getEditor()
    */
   public IDirtiableEditor getEditor() {
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.util.widgets.task.IXTaskViewer#isTasksEditable()
    */
   public boolean isArtifactsEditable() {
      return true;
   }

   /**
    * @return the artifacts
    */
   public Collection<? extends Artifact> getArtifacts() {
      return artifacts;
   }

   public void onEvent(final Event event) {

      if (event instanceof DefaultBranchChangedEvent) {
         if (artifacts.size() == 0) return;
         Artifact artifact = artifacts.iterator().next();
         try {
            if (artifact.getBranch() != BranchPersistenceManager.getInstance().getAtsBranch() && artifact.getBranch() != BranchPersistenceManager.getInstance().getDefaultBranch()) {
               if (artifact == null || getEditorInput().getArtifact() == null) {
                  AWorkbench.getActivePage().closeEditor(this, false);
                  return;
               }
            }
         } catch (SQLException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, false);
         }
      } else {
         throw new IllegalStateException("Not registered for legal event.");
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ats.IActionable#getActionDescription()
    */
   public String getActionDescription() {
      return "";
   }

}
