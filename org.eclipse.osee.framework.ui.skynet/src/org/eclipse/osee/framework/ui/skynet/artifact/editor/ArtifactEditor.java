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

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.pages.ArtifactEditorOutlinePage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.pages.ArtifactFormPage;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
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

   public ArtifactEditor() {
      super();
   }

   public IActionContributor getActionBarContributor() {
      if (actionBarContributor == null) {
         actionBarContributor = new ArtifactEditorActionBarContributor(this);
      }
      return actionBarContributor;
   }

   @Override
   public BaseArtifactEditorInput getEditorInput() {
      return (BaseArtifactEditorInput) super.getEditorInput();
   }

   @Override
   public void onDirtied() {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            firePropertyChange(PROP_DIRTY);
         }
      });
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      ArtifactFormPage page = getFormPage();
      if (page != null) {
         page.showBusy(busy);
      }
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      try {
         getFormPage().doSave(monitor);
         Artifact artifact = getEditorInput().getArtifact();
         artifact.persist();
         firePropertyChange(PROP_DIRTY);
      } catch (OseeCoreException ex) {
         onDirtied();
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void dispose() {
      try {
         // If the artifact is dirty when the editor gets disposed, then it needs to be reverted
         Artifact artifact = getEditorInput().getArtifact();
         if (!artifact.isDeleted() && artifact.isDirty()) {
            try {
               artifact.reloadAttributesAndRelations();
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      } finally {
         super.dispose();
      }
   }

   @Override
   protected void checkEnabledTooltems() {
      //      if (!attributeComposite.isDisposed()) {
      //         Display.getDefault().asyncExec(new Runnable() {
      //            public void run() {
      //               boolean isEditAllowed = artifact.isReadOnly() != true;
      //
      //               if (attributeComposite.getToolBar() == null || attributeComposite.getToolBar().isDisposed()) {
      //                  return;
      //               }
      //               attributeComposite.getToolBar().getItem(REVEAL_ARTIFACT_INDEX).setEnabled(true);
      //               attributeComposite.getToolBar().getItem(EDIT_ARTIFACT_INDEX).setEnabled(isEditAllowed);
      //               attributeComposite.getToolBar().update();
      //
      //               relationsComposite.getToolBar().getItem(REVEAL_ARTIFACT_INDEX).setEnabled(true);
      //               relationsComposite.getToolBar().getItem(EDIT_ARTIFACT_INDEX).setEnabled(isEditAllowed);
      //               relationsComposite.getToolBar().update();
      //            }
      //         });
      //      }
   }

   @Override
   protected void closeEditor() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            AWorkbench.getActivePage().closeEditor(ArtifactEditor.this, false);
         }
      });
   }

   @Override
   protected void refreshDirtyArtifact() {
      Jobs.startJob(new RefreshDirtyArtifactJob());
   }

   @Override
   protected void refreshRelations() {
      Jobs.startJob(new RefreshRelations());
   }

   @Override
   protected void addPages() {
      OseeContributionItem.addTo(this, true);
      setPartName(getEditorInput().getName());
      setTitleImage(getEditorInput().getImage());

      formPage = new ArtifactFormPage(this, "ArtifactFormPage", null);
      try {
         addPage(formPage);
      } catch (PartInitException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private ArtifactFormPage getFormPage() {
      return formPage;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      if (adapter == IActionable.class) {
         return new IActionable() {
            @Override
            public String getActionDescription() {
               return "";
            }
         };
      } else if (adapter == IContentOutlinePage.class) {
         ArtifactEditorOutlinePage page = getOutlinePage();
         page.setInput(this);
         return page;
      } else if (adapter == RelationsComposite.class) {
         return getFormPage().getRelationsComposite();
      }
      return super.getAdapter(adapter);
   }

   protected ArtifactEditorOutlinePage getOutlinePage() {
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
            setTitleImage(getEditorInput().getImage());
            ArtifactEditorOutlinePage outlinePage = getOutlinePage();
            if (outlinePage != null) {
               outlinePage.refresh();
            }
            ArtifactFormPage page = getFormPage();
            if (page != null && Widgets.isAccessible(page.getPartControl())) {
               page.refresh();
            }
            onDirtied();
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
         return Status.OK_STATUS;
      }
   }

   public static void editArtifacts(final Collection<Artifact> artifacts) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               for (Artifact artifact : artifacts) {
                  if (!AccessControlManager.hasPermission(artifact, PermissionEnum.READ)) {
                     OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP,
                           "The user " + UserManager.getUser() + " does not have read access to " + artifact);
                  } else {
                     AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact), EDITOR_ID);
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   public static void editArtifact(final Artifact artifact) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               if (!AccessControlManager.hasPermission(artifact, PermissionEnum.READ)) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP,
                        "The user " + UserManager.getUser() + " does not have read access to " + artifact);
               } else if (artifact != null) {
                  AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact), EDITOR_ID);
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }
}
