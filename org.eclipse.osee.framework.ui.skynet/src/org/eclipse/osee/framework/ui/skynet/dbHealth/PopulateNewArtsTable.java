package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.swt.SWT;

/**
 * @author Ryan D. Brooks
 */
public class PopulateNewArtsTable extends DatabaseHealthOperation {
   private static final String SELECT_ARTS =
         "select art.*, gamma_id from osee_artifact art, osee_artifact_version arv where art.art_id = arv.art_id and not exists (select 1 from osee_arts ats where art.art_id = ats.art_id) order by art.art_id, gamma_id";

   private static final String INSERT_ARTS =
         "insert into osee_arts(gamma_id, art_id, art_type_id, guid, human_readable_id) values (?, ?, ?, ?, ?)";
   private final List<Object[]> insertData = new ArrayList<Object[]>();
   private ResultsEditorTableTab resultsTab;

   public PopulateNewArtsTable() {
      super("PopulateNewArtsTable ");
   }

   private void log(int artId) {
      resultsTab.addRow(new ResultsXViewerRow(new String[] {String.valueOf(artId)}));
   }

   private void fixIssues(IProgressMonitor monitor) throws OseeDataStoreException {
      if (isFixOperationEnabled()) {
         checkForCancelledStatus(monitor);
         ConnectionHandler.runBatchUpdate(INSERT_ARTS, insertData);
      }
      monitor.worked(calculateWork(0.1));
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      getResultsProvider().clearTabs();

      checkForCancelledStatus(monitor);

      resultsTab = new ResultsEditorTableTab(getName());
      getResultsProvider().addResultsTab(resultsTab);
      resultsTab.addColumn(new XViewerColumn("1", "Artifact Id", 220, SWT.LEFT, true, SortDataType.Integer, false, ""));

      IOseeStatement chStmt = SkynetGuiPlugin.getInstance().getOseeDatabaseService().getStatement();

      try {
         chStmt.runPreparedQuery(10000, SELECT_ARTS);
         monitor.worked(calculateWork(0.40));
         int previousArtId = -1;
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            int artId = chStmt.getInt("art_id");
            if (previousArtId != artId) {
               log(artId);
               insertData.add(new Object[] {chStmt.getInt("gamma_id"), chStmt.getInt("art_id"),
                     chStmt.getInt("art_type_id"), chStmt.getString("guid"), chStmt.getString("human_readable_id")});
               monitor.worked(calculateWork(0.5));
               previousArtId = artId;
            }
         }
      } finally {
         chStmt.close();
      }
      fixIssues(monitor);
      insertData.clear();
   }

   @Override
   public String getCheckDescription() {
      return "Find all art_id that need to be added to osee_arts";
   }

   @Override
   public String getFixDescription() {
      return "Find all art_id that need to be added to osee_arts and insert them";
   }
}