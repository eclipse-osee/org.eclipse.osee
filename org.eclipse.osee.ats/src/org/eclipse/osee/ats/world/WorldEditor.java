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
package org.eclipse.osee.ats.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author Donald G. Dunne
 */
public class WorldEditor extends AbstractArtifactEditor implements IDirtiableEditor, IAtsMetricsProvider, IActionable {
   public static final String EDITOR_ID = "org.eclipse.osee.ats.world.WorldEditor";
   private int mainPageIndex, metricsPageIndex;
   private WorldXWidgetActionPage actionPage;
   private AtsMetricsComposite metricsComposite;
   public static final String HELP_CONTEXT_ID = "atsWorldView";

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void doSave(IProgressMonitor monitor) {
   }

   public static void open(final IWorldEditorProvider provider) throws OseeCoreException {
      open(provider, false);
   }

   public static void open(final IWorldEditorProvider provider, boolean forcePend) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(new WorldEditorInput(provider), EDITOR_ID);
            } catch (PartInitException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }, forcePend);
   }

   public void closeEditor() {
      final MultiPageEditorPart editor = this;
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            AWorkbench.getActivePage().closeEditor(editor, false);
         }
      });
   }

   public static Collection<WorldEditor> getEditors() {
      final List<WorldEditor> editors = new ArrayList<WorldEditor>();
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               editors.add((WorldEditor) editor.getEditor(false));
            }
         }
      }, true);
      return editors;
   }

   public static void closeAll() {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               AWorkbench.getActivePage().closeEditor((editor.getEditor(false)), false);
            }
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
   public void dispose() {
      if (actionPage != null && actionPage.getWorldComposite() != null) {
         actionPage.getWorldComposite().disposeComposite();
      }
      if (metricsComposite != null) metricsComposite.disposeComposite();
      super.dispose();
   }

   public String getCurrentTitleLabel() {
      return actionPage.getCurrentTitleLabel();
   }

   public void setTableTitle(final String title, final boolean warning) {
      actionPage.setTableTitle(title, warning);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
    */
   @Override
   public boolean isDirty() {
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
    */
   @Override
   protected void addPages() {

      try {
         OseeContributionItem.addTo(this, true);

         IWorldEditorProvider provider = getWorldEditorProvider();

         createMainTab();
         createMetricsTab();

         setPartName(provider.getSelectedName(SearchType.Search));
         setActivePage(mainPageIndex);

         // Until WorldEditor has different help, just use WorldView's help
         AtsPlugin.getInstance().setHelp(actionPage.getWorldComposite().getControl(), HELP_CONTEXT_ID);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   /**
    * @return the actionPage
    */
   public WorldXWidgetActionPage getActionPage() {
      return actionPage;
   }

   public void setEditorTitle(final String str) {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            setPartName(str);
            firePropertyChange(IWorkbenchPart.PROP_TITLE);
         }
      });
   }

   public IWorldEditorProvider getWorldEditorProvider() {
      IEditorInput editorInput = getEditorInput();
      if (!(editorInput instanceof WorldEditorInput)) {
         throw new IllegalArgumentException("Editor Input not WorldEditorInput");
      }
      WorldEditorInput worldEditorInput = (WorldEditorInput) editorInput;
      return worldEditorInput.getIWorldEditorProvider();
   }

   public void reSearch() throws OseeCoreException {
      actionPage.reSearch();
   }

   private void createMainTab() throws OseeCoreException, PartInitException {
      actionPage = new WorldXWidgetActionPage(this);
      mainPageIndex = addPage(actionPage);
   }

   private void createMetricsTab() throws OseeCoreException {
      Composite comp = AtsLib.createCommonPageComposite(getContainer());
      AtsLib.createCommonToolBar(comp);
      metricsComposite = new AtsMetricsComposite(this, comp, SWT.NONE);
      metricsPageIndex = addPage(comp);
      setPageText(metricsPageIndex, "Metrics");
   }

   public ArrayList<Artifact> getLoadedArtifacts() {
      return actionPage.getWorldComposite().getLoadedArtifacts();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsMetricsProvider#getArtifacts()
    */
   @Override
   public Collection<? extends Artifact> getMetricsArtifacts() throws OseeCoreException {
      return getLoadedArtifacts();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsMetricsProvider#getMetricsVersionArtifact()
    */
   @Override
   public VersionArtifact getMetricsVersionArtifact() throws OseeCoreException {
      return getWorldEditorProvider().getTargetedVersionArtifact();
   }

   public WorldComposite getWorldComposite() {
      return actionPage.getWorldComposite();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ats.IActionable#getActionDescription()
    */
   @Override
   public String getActionDescription() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsMetricsProvider#getManHoursPerDayPreference()
    */
   @Override
   public double getManHoursPerDayPreference() throws OseeCoreException {
      return actionPage.getWorldComposite().getManHoursPerDayPreference();
   }
}
