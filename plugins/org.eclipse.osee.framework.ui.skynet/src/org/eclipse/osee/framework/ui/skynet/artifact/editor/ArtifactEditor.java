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

package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.access.UserGroupService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.AttributesComposite;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.pages.ArtifactEditorOutlinePage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.pages.ArtifactEditorReloadTab;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.pages.ArtifactFormPage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.preferences.EditorsPreferencePage;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactEditor extends AbstractEventArtifactEditor {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor";

   private IActionContributor actionBarContributor;
   private ArtifactFormPage formPage;
   private ArtifactEditorOutlinePage outlinePage;

   private AttributesComposite attributesComposite;

   public IActionContributor getActionBarContributor() {
      if (actionBarContributor == null) {
         actionBarContributor = new ArtifactEditorActionBarContributor(getArtifactFromEditorInput());
      }
      return actionBarContributor;
   }

   @Override
   public ArtifactEditorInput getEditorInput() {
      return (ArtifactEditorInput) super.getEditorInput();
   }

   @Override
   public void editorDirtyStateChanged() {
      super.editorDirtyStateChanged();
      getOutlinePage().refresh();
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

   @Override
   public void showBusy(boolean busy) {
      ArtifactFormPage page = getFormPage();
      if (page != null) {
         page.showBusy(busy);
      }
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

   @Override
   public void doSave(IProgressMonitor monitor) {
      try {
         getFormPage().doSave(monitor);
         Artifact artifact = getEditorInput().getArtifact();
         artifact.persist(String.format("%s - %s", getClass().getSimpleName(), artifact.toStringWithId()));
         firePropertyChange(PROP_DIRTY);
      } catch (OseeCoreException ex) {
         onDirtied();
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void dispose() {
      try {
         // If the artifact is dirty when the editor gets disposed, then it needs to be reverted
         Artifact artifact = getEditorInput().getArtifact();
         if (artifact != null && !artifact.isDeleted() && artifact.isDirty()) {
            try {
               artifact.reloadAttributesAndRelations();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      } finally {
         super.dispose();
      }
   }

   @Override
   protected void checkEnabledTooltems() {
      // do nothing
   }

   @Override
   public void closeEditor() {
      super.closeEditor();
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            AWorkbench.getActivePage().closeEditor(ArtifactEditor.this, false);
         }
      });
   }

   @Override
   public void refreshDirtyArtifact() {
      Jobs.startJob(new RefreshDirtyArtifactJob());
   }

   @Override
   public void refreshRelations() {
      Jobs.startJob(new RefreshRelations());
   }

   @Override
   protected void addPages() {
      OseeStatusContributionItemFactory.addTo(this, true);
      setPartName(getEditorInput().getName());

      try {
         if (getEditorInput().isReload()) {
            createReloadTab();
         } else {
            performLoad();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

   private void createReloadTab() throws PartInitException {
      addPage(new ArtifactEditorReloadTab(this));
   }

   public void performLoad() {
      formPage = new ArtifactFormPage(this, "ArtifactFormPage", null);
      try {
         addPage(formPage);
         setPartName(getEditorInput().getName());
      } catch (PartInitException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      try {
         if (EditorsPreferencePage.isIncludeAttributeTabOnArtifactEditor()) {
            createAttributesTab();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

   private ToolBar createToolBar(Composite parent) {
      ToolBar toolBar = ALayout.createCommonToolBar(parent);
      new ToolItem(toolBar, SWT.SEPARATOR);
      Text artifactInfoLabel = new Text(toolBar.getParent(), SWT.END);
      artifactInfoLabel.setEditable(false);
      artifactInfoLabel.setText(
         "Type: \"" + getEditorInput().getArtifact().getArtifactTypeName() + "\"   GUID: " + getEditorInput().getArtifact().getGuid());
      artifactInfoLabel.setToolTipText("The database id for this artifact");

      return toolBar;
   }

   private void createAttributesTab() {
      try {
         if (!UserGroupService.getOseeAdmin().isCurrentUserMember()) {
            return;
         }

         // Create Attributes tab
         Composite composite = new Composite(getContainer(), SWT.NONE);
         GridLayout layout = new GridLayout(1, false);
         layout.marginHeight = 0;
         layout.marginWidth = 0;
         layout.verticalSpacing = 0;
         composite.setLayout(layout);
         ToolBar toolBar = createToolBar(composite);

         ToolItem item = new ToolItem(toolBar, SWT.PUSH);
         item.setImage(ImageManager.getImage(FrameworkImage.SAVE));
         item.setToolTipText("Save attributes changes only");
         item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               try {
                  getEditorInput().getArtifact().persist("ArtifactEditor attribute tab persist");
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

         ToolItem refresh = new ToolItem(toolBar, SWT.PUSH);
         refresh.setImage(ImageManager.getImage(FrameworkImage.REFRESH));
         refresh.setToolTipText("Reload Table");
         refresh.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               try {
                  attributesComposite.refreshArtifact(getArtifactFromEditorInput());
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

         Label label = new Label(composite, SWT.NONE);
         label.setText("  NOTE: Changes made on this page MUST be saved through save icon on this page");
         label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));

         attributesComposite = new AttributesComposite(this, composite, SWT.NONE, getEditorInput().getArtifact());
         int attributesPageIndex = addPage(composite);
         setPageText(attributesPageIndex, "Attributes");
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private ArtifactFormPage getFormPage() {
      return formPage;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getAdapter(Class<T> type) {
      if (type == IContentOutlinePage.class) {
         ArtifactEditorOutlinePage page = getOutlinePage();
         page.setInput(this);
         return (T) page;
      } else if (type == RelationsComposite.class) {
         return (T) getFormPage().getRelationsComposite();
      }
      return super.getAdapter(type);
   }

   public ArtifactEditorOutlinePage getOutlinePage() {
      if (outlinePage == null) {
         outlinePage = new ArtifactEditorOutlinePage();
      }
      return outlinePage;
   }

   private final class RefreshRelations extends UIJob {
      public RefreshRelations() {
         super("Refresh Relations");
      }

      @Override
      public IStatus runInUIThread(IProgressMonitor monitor) {
         ArtifactFormPage page = getFormPage();
         if (page != null) {
            page.showBusy(true);
            RelationsComposite relationsComposite = page.getRelationsComposite();
            if (relationsComposite != null && !relationsComposite.isDisposed()) {
               relationsComposite.refresh();
               onDirtied();
            }
            page.showBusy(false);
         }
         return Status.OK_STATUS;
      }
   }

   private final class RefreshDirtyArtifactJob extends UIJob {

      public RefreshDirtyArtifactJob() {
         super("Refresh Dirty Artifact");
      }

      @Override
      public IStatus runInUIThread(IProgressMonitor monitor) {
         try {
            setPartName(getEditorInput().getName());
            ArtifactEditorOutlinePage outlinePage = getOutlinePage();
            outlinePage.refresh();

            ArtifactFormPage page = getFormPage();
            if (page != null && Widgets.isAccessible(page.getPartControl())) {
               page.refresh();
            }
            onDirtied();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         return Status.OK_STATUS;
      }
   }

   @Override
   public boolean isDisposed() {
      return formPage == null || formPage.getPartControl() == null || formPage.getPartControl().isDisposed();
   }

   @Override
   public void refresh() {
      if (formPage != null) {
         formPage.refresh();
      }
   }

   public static ArtifactEditor getArtifactEditor(Artifact fArtifact) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      IEditorReference editors[] = page.getEditorReferences();
      for (int j = 0; j < editors.length; j++) {
         try {
            IEditorReference editor = editors[j];
            if (editor.getPart(false) instanceof ArtifactEditor) {
               // Try to get from editor's work item
               Artifact artItem = ((ArtifactEditor) editor.getPart(false)).getArtifactFromEditorInput();
               if (fArtifact.equals(artItem)) {
                  return (ArtifactEditor) editor.getPart(false);
               }
               // Else, try to load from saved work item id
               ArtifactId savedArtId = ((ArtifactEditorInput) editor.getEditorInput()).getSavedArtUuid();
               if (savedArtId.isValid() && fArtifact.equals(savedArtId)) {
                  return (ArtifactEditor) editor.getPart(false);
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.WARNING, Lib.exceptionToString(ex));
         }

      }
      return null;
   }

   public static void editArtifact(final Artifact artifact) {
      if (artifact == null) {
         return;
      }
      if (artifact.isDeleted()) {
         AWorkbench.popup("ERROR", "Artifact has been deleted");
         return;
      }
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(new ArtifactEditorInput(artifact), EDITOR_ID);
               //RecentlyVisitedNavigateItems.addVisited(artifact);
            } catch (PartInitException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

}