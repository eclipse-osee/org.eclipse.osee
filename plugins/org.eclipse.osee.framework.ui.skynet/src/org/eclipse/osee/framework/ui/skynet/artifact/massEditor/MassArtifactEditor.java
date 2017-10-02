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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Donald G. Dunne
 */
public class MassArtifactEditor extends AbstractArtifactEditor {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.massEditor.MassArtifactEditor";
   private int artifactsPageIndex;
   private MassXViewer xViewer;
   private Label branchLabel;
   private ToolBar toolBar;

   /**
    * @return the xViewer
    */
   public MassXViewer getXViewer() {
      return xViewer;
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      try {
         Artifacts.persistInTransaction("Mass Artifact Editor - Save", xViewer.getArtifacts());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      onDirtied();
   }

   public static void editArtifacts(final String name, final Collection<? extends Artifact> artifacts, TableLoadOption... tableLoadOptions) {
      Set<TableLoadOption> options = new HashSet<>();
      options.addAll(Arrays.asList(tableLoadOptions));
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            boolean accessControlFilteredResults = false;
            try {
               Set<Artifact> accessibleArts = new HashSet<>();
               for (Artifact artifact : artifacts) {
                  if (!AccessControlManager.hasPermission(artifact, PermissionEnum.READ)) {
                     OseeLog.log(Activator.class, Level.INFO,
                        "The user " + UserManager.getUser() + " does not have read access to " + artifact);
                     accessControlFilteredResults = true;
                  } else {
                     accessibleArts.add(artifact);
                  }
               }
               if (accessibleArts.isEmpty()) {
                  AWorkbench.popup("ERROR", "No Artifacts to edit");
               } else {
                  AWorkbench.getActivePage().openEditor(
                     new MassArtifactEditorInput(name, accessibleArts, new MassXViewerFactory(accessibleArts)),
                     EDITOR_ID);
               }
               if (accessControlFilteredResults) {
                  AWorkbench.popup("ERROR", "Some Artifacts not loaded due to access control limitations.");
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }, options.contains(TableLoadOption.ForcePend));
   }

   public static void editArtifact(final Artifact artifact, TableLoadOption... tableLoadOptions) {
      editArtifacts("", Arrays.asList(artifact));
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

      toolBar = new ToolBar(rightComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);
      ToolItem item = null;

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(PluginUiImage.REFRESH));
      item.setToolTipText("Refresh");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            ArtifactQuery.reloadArtifacts(xViewer.getArtifacts());
            xViewer.refresh();
         }
      });

   }

   public static void editArtifacts(final MassArtifactEditorInput input) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(input, EDITOR_ID);
            } catch (PartInitException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

   }

   @Override
   public boolean isSaveOnCloseNeeded() {
      return isDirty();
   }

   @Override
   public void dispose() {
      super.dispose();

      for (Artifact artifact : xViewer.getArtifacts()) {
         try {
            if (artifact != null && !artifact.isDeleted() && artifact.hasDirtyAttributes()) {
               artifact.reloadAttributesAndRelations();
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   public ArrayList<Artifact> getLoadedArtifacts() {
      return xViewer.getLoadedArtifacts();
   }

   @Override
   public boolean isDirty() {
      for (Artifact artifact : xViewer.getArtifacts()) {
         if (!artifact.isDeleted() && artifact.hasDirtyAttributes()) {
            return true;
         }
      }
      return false;
   }

   @Override
   public String toString() {
      return "MassArtifactEditor";
   }

   @Override
   protected void addPages() {
      IEditorInput editorInput = getEditorInput();
      if (!(editorInput instanceof MassArtifactEditorInput)) {
         throw new IllegalArgumentException("Editor Input not TaskEditorInput");
      }

      if (((MassArtifactEditorInput) editorInput).getName().equals("")) {
         setPartName("Mass Artifact Editor");
      } else {
         setPartName(((MassArtifactEditorInput) editorInput).getName());
      }

      HelpUtil.setHelp(getContainer(), OseeHelpContext.MASS_EDITOR);

      Composite comp = new Composite(getContainer(), SWT.NONE);
      comp.setLayout(new GridLayout(1, true));
      comp.setLayoutData(new GridData(GridData.FILL_BOTH));

      createTaskActionBar(comp);

      xViewer = new MassXViewer(comp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this);
      xViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
      xViewer.setContentProvider(new MassContentProvider(xViewer));
      xViewer.setLabelProvider(new MassLabelProvider(xViewer));
      try {
         branchLabel.setText("Branch: " + (getBranch() == null ? "No Artifacts Returned" : getBranch().getShortName()));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      artifactsPageIndex = addPage(comp);
      setPageText(artifactsPageIndex, "Artifacts");

      new ActionContributionItem(xViewer.getCustomizeAction()).fill(toolBar, -1);
      comp.layout();

      Tree tree = xViewer.getTree();
      GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);

      setActivePage(artifactsPageIndex);
      try {
         xViewer.set(((MassArtifactEditorInput) editorInput).getArtifacts());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      bindMenu();
      getSite().setSelectionProvider(xViewer);

      comp.redraw();
   }

   private void bindMenu() {
      MenuManager manager = xViewer.getMenuManager();
      manager.setRemoveAllWhenShown(true);
      manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      manager.addMenuListener(new MassEditorMenuListener(xViewer));

      Control control = xViewer.getTree();
      Menu menu = manager.createContextMenu(control);
      control.setMenu(menu);

      getSite().registerContextMenu("org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassEdit", manager,
         xViewer);
      getSite().setSelectionProvider(xViewer);
   }
   private static final class MassEditorMenuListener implements IMenuListener {

      private final MassXViewer xviewer;

      private MassEditorMenuListener(MassXViewer xviewer) {
         this.xviewer = xviewer;
      }

      @Override
      public void menuAboutToShow(IMenuManager manager) {
         MenuManager menuManager = xviewer.getMenuManager();
         if (menuManager.find(XViewer.MENU_GROUP_PRE) != null) {
            menuManager.insertBefore(XViewer.MENU_GROUP_PRE, new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
         }
      }
   }

   public IOseeBranch getBranch() {
      if (((MassArtifactEditorInput) getEditorInput()).getArtifacts().isEmpty()) {
         return null;
      }
      return ((MassArtifactEditorInput) getEditorInput()).getArtifacts().iterator().next().getBranchToken();
   }

   @Override
   public void onDirtied() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            firePropertyChange(PROP_DIRTY);
         }
      });
   }

   public String getCurrentStateName() {
      return "";
   }

   public IDirtiableEditor getEditor() {
      return this;
   }

   public boolean isArtifactsEditable() {
      return true;
   }

   /**
    * @return the artifacts
    */
   public Collection<? extends Artifact> getArtifacts() {
      return xViewer.getArtifacts();
   }

   public static Collection<MassArtifactEditor> getEditors() {
      final List<MassArtifactEditor> editors = new ArrayList<>();
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               editors.add((MassArtifactEditor) editor.getEditor(false));
            }
         }
      });
      return editors;
   }

   public static void closeAll() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               AWorkbench.getActivePage().closeEditor(editor.getEditor(false), false);
            }
         }
      });
   }

}
