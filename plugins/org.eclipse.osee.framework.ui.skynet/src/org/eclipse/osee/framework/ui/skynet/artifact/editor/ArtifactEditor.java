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

package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.access.AccessControlArtifactUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.pages.ArtifactEditorOutlinePage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.pages.ArtifactEditorReloadTab;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.pages.ArtifactFormPage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.AttributesFormSection;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.ArtEdAttrTab;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.details.ArtEdDetailsTab;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.rel.ArtEdRelationsTab;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.markedit.OseeMarkdownEditorInput;
import org.eclipse.osee.framework.ui.skynet.markedit.edit.OmeEditTab;
import org.eclipse.osee.framework.ui.skynet.markedit.html.OmeHtmlTab;
import org.eclipse.osee.framework.ui.skynet.markedit.model.ArtOmeData;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
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
   private ArtEdAttrTab attrTab;
   private ArtEdRelationsTab relTab;
   private ArtEdDetailsTab detailsTab;

   private OmeEditTab mdEditTab;
   private OmeHtmlTab mdHtmlTab;

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
         if (mdEditTab != null) {
            mdEditTab.doSave();
         }
         getFormPage().doSave(monitor);
         Artifact artifact = getEditorInput().getArtifact();
         artifact.persist(String.format("%s - %s", getClass().getSimpleName(), artifact.toStringWithId()));
         firePropertyChange(PROP_DIRTY);
      } catch (OseeCoreException ex) {
         onDirtied();
         XResultData rd =
            AccessControlArtifactUtil.getXResultAccessHeader("Artifact Editor - Save", getEditorInput().getArtifact());
         rd.logf("\n\n%s", Lib.exceptionToString(ex));
         XResultDataUI.report(rd, "Artifact Editor - Save");
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
      formPage = new ArtifactFormPage(this, "ArtifactFormPage", "Attributes");
      try {
         addPage(formPage);
         setPartName(getEditorInput().getName());
      } catch (PartInitException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      createMarkdownTabs();
      createAttributesTab();
      createRelationsTab();
      createDetailsTab();
   }

   @Override
   protected void pageChange(int newPageIndex) {
      super.pageChange(newPageIndex);
      if (mdHtmlTab == null) {
         return;
      }
      if (newPageIndex != -1 && pages.size() > newPageIndex) {
         Object page = pages.get(newPageIndex);
         if (page != null && page.equals(mdHtmlTab)) {
            mdHtmlTab.handleRefreshAction();
         }
      }
   }

   private void createMarkdownTabs() {
      Artifact art = getArtifactFromEditorInput();
      if (art.isOfType(CoreArtifactTypes.Markdown)) {
         ArtOmeData omeData = new ArtOmeData(new OseeMarkdownEditorInput(art));
         try {
            mdEditTab = new OmeEditTab(this, omeData);
            addPage(mdEditTab);
            mdHtmlTab = new OmeHtmlTab(this, omeData);
            addPage(mdHtmlTab);
         } catch (PartInitException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   private void createAttributesTab() {
      if (ServiceUtil.accessControlService().isOseeAdmin()) {
         attrTab = new ArtEdAttrTab(this, getArtifactFromEditorInput());
         try {
            addPage(attrTab);
         } catch (PartInitException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   private void createRelationsTab() {
      relTab = new ArtEdRelationsTab(this, getArtifactFromEditorInput());
      try {
         addPage(relTab);
      } catch (PartInitException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createDetailsTab() {
      detailsTab = new ArtEdDetailsTab(this, getArtifactFromEditorInput());
      try {
         addPage(detailsTab);
      } catch (PartInitException ex) {
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
      }
      return super.getAdapter(type);
   }

   public ArtifactEditorOutlinePage getOutlinePage() {
      if (outlinePage == null) {
         outlinePage = new ArtifactEditorOutlinePage();
      }
      return outlinePage;
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
      if (attrTab != null) {
         attrTab.refresh();
      }
      if (mdEditTab != null) {
         mdEditTab.refresh();
      }
      if (mdHtmlTab != null) {
         mdHtmlTab.refresh();
      }
      if (relTab != null) {
         relTab.refresh();
      }
      if (detailsTab != null) {
         detailsTab.refresh();
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
            } catch (PartInitException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   public AttributesFormSection getAttributeFormSection() {
      return formPage.getAttrFormSection();
   }

}