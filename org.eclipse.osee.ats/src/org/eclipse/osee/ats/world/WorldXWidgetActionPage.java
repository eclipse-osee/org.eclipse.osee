/*
 * Created on Nov 16, 2008
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
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class WorldXWidgetActionPage extends AtsXWidgetActionFormPage {

   private final WorldEditor worldEditor;
   private WorldComposite worldComposite;

   /**
    * @return the worldComposite
    */
   public WorldComposite getWorldComposite() {
      return worldComposite;
   }

   /**
    * @param editor
    */
   public WorldXWidgetActionPage(WorldEditor worldEditor) {
      super(worldEditor, "org.eclipse.osee.ats.actionPage", "Actions");
      this.worldEditor = worldEditor;
   }

   @Override
   public Section createResultsSection(Composite body) {
      resultsSection = toolkit.createSection(body, Section.NO_TITLE);
      resultsSection.setText("Results");
      resultsSection.setLayoutData(new GridData(GridData.FILL_BOTH));

      resultsContainer = toolkit.createClientContainer(resultsSection, 1);
      worldComposite = new WorldComposite(worldEditor, resultsContainer, SWT.BORDER, toolBar);
      return resultsSection;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.editor.FormPage#createPartControl(org.eclipse.swt.widgets.Composite)
    */
   @Override
   public void createPartControl(Composite parent) {
      super.createPartControl(parent);
      try {
         loadTable(SearchType.Search);
      } catch (OseeCoreException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.AtsXWidgetActionFormPage#getXWidgetsXml()
    */
   @Override
   public String getXWidgetsXml() {
      return "";
      //      return "<xWidgets>" +
      //      //
      //      "<XWidget xwidgetType=\"XHyperlabelTeamDefinitionSelection\" displayName=\"Team Definitions(s)\" horizontalLabel=\"true\"/>" +
      //      //
      //      "<XWidget beginComposite=\"8\" xwidgetType=\"XCombo()\" displayName=\"Version\" horizontalLabel=\"true\"/>" +
      //      //
      //      "<XWidget xwidgetType=\"XCombo(Both,Released,UnReleased)\" displayName=\"Released\" horizontalLabel=\"true\"/>" +
      //      //
      //      "<XWidget xwidgetType=\"XMembersCombo\" displayName=\"Assignee\" horizontalLabel=\"true\"/>" +
      //      //
      //      "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Completed/Cancelled\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //      //
      //      "</xWidgets>";
   }

   private void loadTable(SearchType searchType) throws OseeCoreException {
      WorldEditorInput aei = (WorldEditorInput) getEditorInput();
      IWorldEditorProvider provider = aei.getIWorldEditorProvider();
      setPartName(provider.getWorldEditorLabel(SearchType.Search));

      Result result = AtsPlugin.areOSEEServicesAvailable();
      if (result.isFalse()) {
         AWorkbench.popup("ERROR", "DB Connection Unavailable");
         return;
      }

      if (!provider.getTableLoadOptions().contains(TableLoadOption.NoUI) && searchType == SearchType.Search) {
         provider.performUI(searchType);
      }
      if (provider.isCancelled()) return;

      if (provider instanceof WorldEditorSearchItemProvider) {
         LoadTableJob job = null;
         job = new LoadTableJob(((WorldEditorSearchItemProvider) provider).getWorldSearchItem(), searchType);
         job.setUser(false);
         job.setPriority(Job.LONG);
         job.schedule();
         if (provider.getTableLoadOptions().contains(TableLoadOption.ForcePend)) {
            try {
               job.join();
            } catch (InterruptedException ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      } else if (provider.getWorldEditorArtifacts(SearchType.Search).size() > 0) {
         worldComposite.load(provider.getWorldEditorLabel(SearchType.Search),
               provider.getWorldEditorArtifacts(SearchType.Search));
      }
   }
   private class LoadTableJob extends Job {

      private final WorldSearchItem searchItem;
      private boolean cancel = false;
      private final SearchType searchType;

      public LoadTableJob(WorldSearchItem searchItem, SearchType searchType) {
         super("Loading \"" + searchItem.getSelectedName(searchType) + "\"...");
         this.searchItem = searchItem;
         this.searchType = searchType;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {

         worldComposite.setTableTitle(
               "Loading \"" + (searchItem.getSelectedName(searchType) != null ? searchItem.getSelectedName(searchType) : "") + "\"...",
               false);
         cancel = false;
         searchItem.setCancelled(cancel);
         final Collection<Artifact> artifacts;
         worldComposite.getXViewer().clear();
         try {
            artifacts = searchItem.performSearchGetResults(false, searchType);
            if (artifacts.size() == 0) {
               if (searchItem.isCancelled()) {
                  monitor.done();
                  worldComposite.setTableTitle("CANCELLED - " + searchItem.getSelectedName(searchType), false);
                  return Status.CANCEL_STATUS;
               } else {
                  monitor.done();
                  worldComposite.setTableTitle("No Results Found - " + searchItem.getSelectedName(searchType), true);
                  return Status.OK_STATUS;
               }
            }
            worldComposite.load(searchItem,
                  (searchItem.getSelectedName(searchType) != null ? searchItem.getSelectedName(searchType) : ""),
                  artifacts);
         } catch (final Exception ex) {
            String str = "Exception occurred. Network may be down.";
            if (ex.getLocalizedMessage() != null && !ex.getLocalizedMessage().equals("")) str +=
                  " => " + ex.getLocalizedMessage();
            worldComposite.setTableTitle("Searching Error - " + searchItem.getSelectedName(searchType), false);
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            monitor.done();
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, str, null);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   public void reSearch() throws OseeCoreException {
      loadTable(SearchType.ReSearch);
   }

}
