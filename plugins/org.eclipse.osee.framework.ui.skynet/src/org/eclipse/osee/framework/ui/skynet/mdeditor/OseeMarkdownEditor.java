/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.ui.skynet.mdeditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.mdeditor.edit.OmeEditTab;
import org.eclipse.osee.framework.ui.skynet.mdeditor.html.OmeHtmlTab;
import org.eclipse.osee.framework.ui.skynet.mdeditor.model.AbstractOmeData;
import org.eclipse.osee.framework.ui.skynet.mdeditor.model.ArtOmeData;
import org.eclipse.osee.framework.ui.skynet.mdeditor.model.FileOmeData;
import org.eclipse.osee.framework.ui.skynet.util.SelectionProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Donald G. Dunne
 */
public class OseeMarkdownEditor extends AbstractArtifactEditor {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.markedit.OseeMarkdownEditor";
   private final Integer startPage = 0;
   private int lastPageSelected = -1;
   private OmeEditTab editTab;
   private OmeHtmlTab htmlTab;
   private AbstractOmeData omeData;

   @Override
   public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
      super.init(site, editorInput);
      defaultSelectionProvider = new SelectionProvider();
      getSite().setSelectionProvider(defaultSelectionProvider);
      if (editorInput instanceof OseeMarkdownEditorInput) {
         omeData = new ArtOmeData((OseeMarkdownEditorInput) editorInput);
      } else {
         omeData = new FileOmeData((FileEditorInput) editorInput);
      }
   }

   @Override
   protected void pageChange(int newPageIndex) {
      super.pageChange(newPageIndex);
      setSelectionListenerOn(newPageIndex);
   }

   private synchronized void setSelectionListenerOn(int pageIndex) {
      if (lastPageSelected > -1) {
         //         ResultsXViewer oldViewer = getViewerForPage(lastPageSelected);
         //         if (oldViewer != null) {
         //            oldViewer.removeSelectionChangedListener(selectionListener);
         //         }
      }
      lastPageSelected = pageIndex;
   }

   @Override
   protected void addPages() {
      try {
         OseeStatusContributionItemFactory.addTo(this, true);
         editTab = new OmeEditTab(this, omeData);
         addPage(editTab);

         htmlTab = new OmeHtmlTab(this, omeData);
         addPage(htmlTab);

         setPartName(omeData.getEditorName());
         setActivePage(startPage);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public String getEditorId() {
      return EDITOR_ID;
   }

   public void setEditorTitle(final String str) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            setPartName(str);
            firePropertyChange(IWorkbenchPart.PROP_TITLE);
         }
      });
   }

   @Override
   public boolean isSaveOnCloseNeeded() {
      return isDirty();
   }

   public void refreshTitle() {
      firePropertyChange(IWorkbenchPart.PROP_TITLE);
   }

   @Override
   public boolean isDirty() {
      return omeData.isDirty();
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
   public void doSave(IProgressMonitor monitor) {
      try {
         omeData.doSave();
         firePropertyChange(PROP_DIRTY);
      } catch (OseeCoreException ex) {
         onDirtied();
         omeData.onSaveException(ex);
      }
   }

   @Override
   public void dispose() {
      try {
         omeData.dispose();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      } finally {
         super.dispose();
      }
   }

   public static Collection<OseeMarkdownEditor> getEditors() {
      final List<OseeMarkdownEditor> editors = new ArrayList<>();
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               editors.add((OseeMarkdownEditor) editor.getEditor(false));
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

   public static void open(Artifact artifact) {
      open(Collections.singleton(artifact));
   }

   public static void open(Collection<Artifact> artifacts) {
      open(artifacts, false);
   }

   public static void open(Collection<Artifact> artifacts, boolean forcePend) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               for (Artifact artifact : artifacts) {
                  OseeMarkdownEditorInput input = new OseeMarkdownEditorInput(artifact);
                  page.openEditor(input, EDITOR_ID);
               }
            } catch (PartInitException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }, forcePend);
   }

}
