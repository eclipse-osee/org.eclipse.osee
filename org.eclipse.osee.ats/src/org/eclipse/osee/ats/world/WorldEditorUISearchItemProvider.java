/*
 * Created on Nov 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.world.search.NextVersionSearchItem;
import org.eclipse.osee.ats.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldUISearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorUISearchItemProvider extends WorldEditorProvider {

   private final WorldUISearchItem worldUISearchItem;

   public WorldEditorUISearchItemProvider(WorldUISearchItem worldUISearchItem) {
      this(worldUISearchItem, null, TableLoadOption.None);
   }

   public WorldEditorUISearchItemProvider(WorldUISearchItem worldUISearchItem, CustomizeData customizeData, TableLoadOption... tableLoadOptions) {
      super(customizeData, tableLoadOptions);
      this.worldUISearchItem = worldUISearchItem;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#copy(org.eclipse.osee.ats.world.IWorldEditorProvider)
    */
   @Override
   public IWorldEditorProvider copyProvider() {
      return new WorldEditorUISearchItemProvider((WorldUISearchItem) worldUISearchItem.copy(), customizeData,
            tableLoadOptions);
   }

   /**
    * @return the worldSearchItem
    */
   public WorldSearchItem getWorldSearchItem() {
      return worldUISearchItem;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#getName()
    */
   @Override
   public String getName() throws OseeCoreException {
      return worldUISearchItem.getName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#getSelectedName(org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType)
    */
   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return worldUISearchItem.getSelectedName(searchType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#run(org.eclipse.osee.ats.world.WorldEditor)
    */
   @Override
   public void run(WorldEditor worldEditor, SearchType searchType, boolean forcePend) throws OseeCoreException {

      Collection<TableLoadOption> options = Collections.getAggregate(tableLoadOptions);
      if (!options.contains(TableLoadOption.NoUI) && searchType == SearchType.Search) {
         worldUISearchItem.performUI(searchType);
      }
      if (worldUISearchItem.isCancelled()) {
         worldEditor.close(false);
         return;
      }

      LoadTableJob job = null;
      job = new LoadTableJob(worldEditor, worldUISearchItem, searchType);
      job.setUser(false);
      job.setPriority(Job.LONG);
      job.schedule();
      if (options.contains(TableLoadOption.ForcePend)) {
         try {
            job.join();
         } catch (InterruptedException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   private class LoadTableJob extends Job {

      private final WorldUISearchItem worldUISearchItem;
      private boolean cancel = false;
      private final SearchType searchType;
      private final WorldEditor worldEditor;

      public LoadTableJob(WorldEditor worldEditor, WorldUISearchItem worldUISearchItem, SearchType searchType) throws OseeCoreException {
         super("Loading \"" + worldUISearchItem.getSelectedName(searchType) + "\"...");
         this.worldEditor = worldEditor;
         this.worldUISearchItem = worldUISearchItem;
         this.searchType = searchType;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         String selectedName = "";
         try {
            selectedName = worldUISearchItem.getSelectedName(searchType);
            worldEditor.setEditorTitle(selectedName != null ? selectedName : worldUISearchItem.getName());
            worldEditor.setTableTitle("Loading \"" + (selectedName != null ? selectedName : "") + "\"...", false);
            cancel = false;
            worldUISearchItem.setCancelled(cancel);
            final Collection<Artifact> artifacts;
            worldEditor.getWorldComposite().getXViewer().clear();
            artifacts = worldUISearchItem.performSearchGetResults(false, searchType);
            if (artifacts.size() == 0) {
               if (worldUISearchItem.isCancelled()) {
                  worldEditor.setTableTitle("CANCELLED - " + selectedName, false);
                  return Status.CANCEL_STATUS;
               } else {
                  worldEditor.setTableTitle("No Results Found - " + selectedName, true);
                  return Status.OK_STATUS;
               }
            }
            worldEditor.getWorldComposite().load((selectedName != null ? selectedName : ""), artifacts, customizeData);
         } catch (final Exception ex) {
            worldEditor.getWorldComposite().setTableTitle("Searching Error - " + selectedName, false);
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
         } finally {
            monitor.done();
         }

         return Status.OK_STATUS;
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#getTargetedVersionArtifact()
    */
   @Override
   public VersionArtifact getTargetedVersionArtifact() throws OseeCoreException {
      if (worldUISearchItem instanceof VersionTargetedForTeamSearchItem) {
         return ((VersionTargetedForTeamSearchItem) worldUISearchItem).getSearchVersionArtifact();
      } else if (worldUISearchItem instanceof NextVersionSearchItem) {
         return ((NextVersionSearchItem) worldUISearchItem).getSelectedVersionArt();
      }
      return null;
   }

}
