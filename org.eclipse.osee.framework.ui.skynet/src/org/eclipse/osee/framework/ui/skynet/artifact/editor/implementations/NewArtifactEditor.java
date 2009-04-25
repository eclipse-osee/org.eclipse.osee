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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.implementations;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractEventArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorActionBarContributor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.BaseArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.IActionContributor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.pages.ArtifactEditorOutlinePage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.pages.ArtifactFormPage;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Roberto E. Escobar
 */
public class NewArtifactEditor extends AbstractEventArtifactEditor {
   private static final String NEW_EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.artifact.editor.NewArtifactEditor";

   private IActionContributor actionBarContributor;
   private ArtifactFormPage formPage;
   private ArtifactEditorOutlinePage outlinePage;

   public NewArtifactEditor() {
      super();
   }

   public IActionContributor getActionBarContributor() {
      if (actionBarContributor == null) {
         actionBarContributor = new ArtifactEditorActionBarContributor(this);
      }
      return actionBarContributor;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.EditorPart#getEditorInput()
    */
   @Override
   public BaseArtifactEditorInput getEditorInput() {
      return (BaseArtifactEditorInput) super.getEditorInput();
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.editor.FormEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
    */
   @Override
   public void init(IEditorSite site, IEditorInput input) throws PartInitException {
      super.init(site, input);
      ISelectionProvider provider = new ArtifactEditorSelectionProvider();
      provider.setSelection(new StructuredSelection(new Object[] {getEditorInput().getArtifact()}));
      getSite().setSelectionProvider(provider);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractEventArtifactEditor#onDirtied()
    */
   @Override
   public void onDirtied() {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            firePropertyChange(PROP_DIRTY);
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractEventArtifactEditor#onDirty()
    */
   @Override
   protected void onDirty() {
      // Do Nothing
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
    */
   @Override
   public boolean isDirty() {
      return computeIsDirty().isTrue();
   }

   private Result computeIsDirty() {
      Artifact artifact = getEditorInput().getArtifact();
      if (artifact.isDeleted()) {
         return Result.FalseResult;
      }
      try {
         if (artifact.isReadOnly()) {
            return Result.FalseResult;
         }
         Result result = artifact.reportIsDirty(true);
         if (result.isTrue()) {
            return result;
         }

         ArtifactFormPage page = getFormPage();
         if (page != null) {
            result = page.isDirty() ? Result.TrueResult : Result.FalseResult;
         }
         System.out.println("New Attribute Composite - isDirt => " + result);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return Result.FalseResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void doSave(IProgressMonitor monitor) {
      try {
         getFormPage().showBusy(true);
         Artifact artifact = getEditorInput().getArtifact();
         artifact.persistAttributesAndRelations();
         firePropertyChange(PROP_DIRTY);
      } catch (OseeCoreException ex) {
         onDirtied();
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      } finally {
         getFormPage().showBusy(false);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractEventArtifactEditor#dispose()
    */
   @Override
   public void dispose() {
      try {
         // If the artifact is dirty when the editor get's disposed, then it needs to be reverted
         Artifact artifact = getEditorInput().getArtifact();
         if (!artifact.isDeleted() && (artifact.isDirty(true))) {
            try {
               artifact.reloadAttributesAndRelations();
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
         ArtifactFormPage formPage = getFormPage();
         if (formPage != null) {
            formPage.dispose();
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      } finally {
         super.dispose();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractEventArtifactEditor#checkEnabledTooltems()
    */
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractEventArtifactEditor#closeEditor()
    */
   @Override
   protected void closeEditor() {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            AWorkbench.getActivePage().closeEditor(NewArtifactEditor.this, false);
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractEventArtifactEditor#refreshDirtyArtifact()
    */
   @Override
   protected void refreshDirtyArtifact() {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            Artifact artifact = getEditorInput().getArtifact();
            setPartName(getEditorInput().getName());
            setTitleImage(artifact.getImage());
            ArtifactFormPage page = getFormPage();
            if (page != null) {
               page.showBusy(true);
               page.refresh();
               page.showBusy(false);
            }
            ArtifactEditorOutlinePage outlinePage = getOutlinePage();
            if (outlinePage != null) {
               outlinePage.refresh();
            }
            onDirtied();
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractEventArtifactEditor#refreshRelationsComposite()
    */
   @Override
   protected void refreshRelations() {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            ArtifactFormPage page = getFormPage();
            if (page != null) {
               RelationsComposite relationsComposite = page.getRelationsComposite();
               if (relationsComposite != null && !relationsComposite.isDisposed()) {
                  relationsComposite.refresh();
                  onDirtied();
               }
            }
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
    */
   @Override
   protected void addPages() {
      OseeContributionItem.addTo(this, true);
      Artifact artifact = getEditorInput().getArtifact();
      setPartName(getEditorInput().getName());
      setTitleImage(artifact.getImage());

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

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.MultiPageEditorPart#getAdapter(java.lang.Class)
    */
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

   private final class ArtifactEditorSelectionProvider implements ISelectionProvider {
      private ISelection selection;

      @Override
      public void addSelectionChangedListener(ISelectionChangedListener listener) {
      }

      @Override
      public ISelection getSelection() {
         return selection;
      }

      @Override
      public void removeSelectionChangedListener(ISelectionChangedListener listener) {
      }

      @Override
      public void setSelection(ISelection selection) {
         this.selection = selection;
      }
   }

   public static void editArtifacts(final Collection<Artifact> artifacts) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               for (Artifact artifact : artifacts) {
                  if (!AccessControlManager.checkObjectPermission(artifact, PermissionEnum.READ)) {
                     OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP,
                           "The user " + UserManager.getUser() + " does not have read access to " + artifact);
                  } else
                     AWorkbench.getActivePage().openEditor(new NewArtifactEditorInput(artifact), NEW_EDITOR_ID);
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
               if (!AccessControlManager.checkObjectPermission(artifact, PermissionEnum.READ)) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP,
                        "The user " + UserManager.getUser() + " does not have read access to " + artifact);
               } else if (artifact != null) {
                  AWorkbench.getActivePage().openEditor(new NewArtifactEditorInput(artifact), NEW_EDITOR_ID);
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }
}
