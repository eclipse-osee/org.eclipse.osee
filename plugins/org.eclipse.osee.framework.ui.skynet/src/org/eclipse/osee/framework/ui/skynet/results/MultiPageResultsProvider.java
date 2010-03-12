/*
 * Created on Oct 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation;

/**
 * @author Ryan D. Brooks
 */
public class MultiPageResultsProvider implements IResultsEditorProvider {
   private final DatabaseHealthOperation healthOperation;
   private final List<IResultsEditorTab> resultsTabs = new ArrayList<IResultsEditorTab>(4);

   public MultiPageResultsProvider(DatabaseHealthOperation healthOperation) {
      super();
      this.healthOperation = healthOperation;
   }

   @Override
   public String getEditorName() throws OseeCoreException {
      return healthOperation.getName();
   }

   @Override
   public List<IResultsEditorTab> getResultsEditorTabs() throws OseeCoreException {
      return resultsTabs;
   }

   public void addResultsTab(IResultsEditorTab resultsTab) {
      resultsTabs.add(resultsTab);
   }

   public void clearTabs() {
      resultsTabs.clear();
   }
}