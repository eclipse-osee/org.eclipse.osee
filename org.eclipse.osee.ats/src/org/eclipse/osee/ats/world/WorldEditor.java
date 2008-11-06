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

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Donald G. Dunne
 */
public class WorldEditor extends AbstractArtifactEditor implements IDirtiableEditor, IAtsMetricsProvider {
   public static final String EDITOR_ID = "org.eclipse.osee.ats.world.WorldEditor";
   private int mainPageIndex, metricsPageIndex;
   private WorldComposite worldComposite;
   private AtsMetricsComposite metricsComposite;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void doSave(IProgressMonitor monitor) {
   }

   public static void open(String name, Collection<? extends Artifact> arts, CustomizeData customizeData, TableLoadOption... tableLoadOptions) {
      WorldEditorInput worldEditorInput = new WorldEditorInput(name, arts, customizeData, tableLoadOptions);
      if (worldEditorInput != null) {
         IWorkbenchPage page = AWorkbench.getActivePage();
         try {
            page.openEditor(worldEditorInput, WorldEditor.EDITOR_ID);
         } catch (PartInitException ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
      }
   }

   public static void open(WorldSearchItem searchItem, CustomizeData customizeData, TableLoadOption... tableLoadOptions) throws OseeCoreException {
      WorldEditorInput worldEditorInput =
            new WorldEditorInput(searchItem, SearchType.Search, customizeData, tableLoadOptions);
      if (worldEditorInput != null) {
         IWorkbenchPage page = AWorkbench.getActivePage();
         try {
            page.openEditor(worldEditorInput, WorldEditor.EDITOR_ID);
         } catch (PartInitException ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
      }
   }

   @Override
   public boolean isSaveOnCloseNeeded() {
      return isDirty();
   }

   @Override
   public void dispose() {
      if (worldComposite != null) worldComposite.disposeComposite();
      if (metricsComposite != null) metricsComposite.disposeComposite();
      super.dispose();
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

         IEditorInput editorInput = getEditorInput();
         WorldEditorInput worldEditorInput = null;
         if (editorInput instanceof WorldEditorInput) {
            worldEditorInput = (WorldEditorInput) editorInput;
         } else
            throw new IllegalArgumentException("Editor Input not WorldEditorInput");

         setPartName(editorInput.getName());

         // Create Main tab
         worldComposite = new WorldComposite(EDITOR_ID, null, getContainer(), SWT.NONE);
         mainPageIndex = addPage(worldComposite);
         setPageText(mainPageIndex, "Actions");

         metricsComposite = new AtsMetricsComposite(this, getContainer(), SWT.NONE);
         metricsPageIndex = addPage(metricsComposite);
         setPageText(metricsPageIndex, "Metrics");

         setActivePage(mainPageIndex);
         if (worldEditorInput.getCustomizeData() != null) {
            worldComposite.setCustomizeData(worldEditorInput.getCustomizeData());
         }

         if (worldEditorInput.getSearchItem() != null && worldEditorInput.getSearchType() != null) {
            worldComposite.loadTable(worldEditorInput.getSearchItem(), worldEditorInput.getSearchType(),
                  worldEditorInput.getTableLoadOptions());
         } else if (worldEditorInput.getSearchItem() != null) {
            worldComposite.loadTable(worldEditorInput.getSearchItem(), SearchType.Search,
                  worldEditorInput.getTableLoadOptions());
         } else if (worldEditorInput.getArts() != null) {
            worldComposite.load(worldEditorInput.getName(), worldEditorInput.getArts(),
                  worldEditorInput.getTableLoadOptions());
         } else
            throw new OseeArgumentException("Unknown WorldEditorInput values.");

         // Until WorldEditor has different help, just use WorldView's help
         AtsPlugin.getInstance().setHelp(worldComposite.getControl(), WorldView.HELP_CONTEXT_ID);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsMetricsProvider#getArtifacts()
    */
   @Override
   public Collection<? extends Artifact> getMetricsArtifacts() {
      return worldComposite.getLoadedArtifacts();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsMetricsProvider#getMetricsVersionArtifact()
    */
   @Override
   public VersionArtifact getMetricsVersionArtifact() {
      if (worldComposite.getLastSearchItem() instanceof VersionTargetedForTeamSearchItem) {
         return ((VersionTargetedForTeamSearchItem) worldComposite.getLastSearchItem()).getSearchVersionArtifact();
      }
      return null;
   }

}
