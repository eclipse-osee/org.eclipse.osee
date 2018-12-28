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
package org.eclipse.osee.ats.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.health.ValidateAtsDatabase;
import org.eclipse.osee.ats.health.ValidateResults;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class ValidateAtsConfiguration extends XNavigateItemAction {

   public ValidateAtsConfiguration(XNavigateItem parent) {
      super(parent, "Validate ATS Configuration", FrameworkImage.GEAR);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      XResultData rd = new XResultData();
      rd.log(getName());

      try {
         List<Artifact> configArts = ArtifactQuery.getArtifactListFromTypes(
            Arrays.asList(AtsArtifactTypes.TeamDefinition, AtsArtifactTypes.ActionableItem, AtsArtifactTypes.Version),
            AtsClientService.get().getAtsBranch(), DeletionFlag.EXCLUDE_DELETED);
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), getName());

         ValidateResults results = new ValidateResults();

         ValidateAtsDatabase.testAtsAttributeValues(transaction, results, true, configArts);
         ValidateAtsDatabase.testVersionArtifacts(configArts, results);
         ValidateAtsDatabase.testActionableItemToTeamDefinition(configArts, results);
         ValidateAtsDatabase.testTeamDefinitions(configArts, results);
         ValidateAtsDatabase.testParallelConfig(configArts, results);

         // Log counts of types checked
         logCounts(configArts, results);

         // Log results
         results.addResultsMapToResultData(rd);
         results.addTestTimeMapToResultData(rd);

         transaction.execute();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      XResultDataUI.report(rd, getName());
   }

   private void logCounts(List<Artifact> configArts, ValidateResults results) {
      CountingMap<String> typeToCount = new CountingMap<>();
      for (Artifact art : configArts) {
         typeToCount.put(art.getArtifactTypeName());
      }
      for (Entry<String, MutableInteger> type : typeToCount.getCounts()) {
         results.log("Type Counts", String.format("%d of type %s", type.getValue().getValue(), type.getKey()));
      }
   }
}
