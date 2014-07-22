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
package org.eclipse.osee.ats.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsUtilService;
import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Angel Avila
 */
public class AtsIdCheckBlam extends AbstractBlam {
   private static final String CHANGE_INVALID_IDS = "Correct Invalid ATS IDs?";

   @Override
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {

            boolean isPersist = variableMap.getBoolean(CHANGE_INVALID_IDS);

            IAtsClient iAtsClient = AtsClientService.get();
            IAtsUtilService utilService = iAtsClient.getUtilService();
            ISequenceProvider sequenceProvider = AtsClientService.get().getSequenceProvider();

            SkynetTransaction transaction = null;

            //replace next lines with commented out lines when ATS81395 is completed
            List<Artifact> workflowArtifacts =
               ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.TeamWorkflow, CoreBranches.COMMON,
                  DeletionFlag.INCLUDE_DELETED);
            //QueryBuilderArtifact builder = ArtifactQuery.createQueryBuilder(CoreBranches.COMMON);
            //            builder.andIsOfType(AtsArtifactTypes.AbstractWorkflowArtifact);/
            //            builder.and(AtsAttributeTypes.AtsId, Operator.EQUAL, "0");
            //ResultSet<Artifact> results = builder.getResults();

            for (Artifact art : workflowArtifacts) {
               // remove when ATS81395 is completed
               String atsIdValue = art.getSoleAttributeValueAsString(AtsAttributeTypes.AtsId, "");
               if (atsIdValue.equals("0")) {
                  // END Remove.

                  if (isPersist) {
                     // Don't wanna waste IDs unless we're gonna actually persist
                     TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) art;
                     IAtsTeamDefinition teamDefinition = teamArt.getTeamDefinition();
                     String nextAtsId = utilService.getNextAtsId(sequenceProvider, null, teamDefinition);

                     art.setSoleAttributeFromString(AtsAttributeTypes.AtsId, nextAtsId);
                     if (transaction == null) {
                        transaction =
                           TransactionManager.createTransaction(CoreBranches.COMMON, "Fix ATS Workflows with ID ATS0");
                     }
                     transaction.addArtifact(art);
                     logf(String.format("New ID for [%s] is [%s].", art.getName(), nextAtsId));
                  } else {
                     logf(String.format("%s Has an invalid ID = 0", art.getName()));
                  }
               }
            }

            if (transaction != null && isPersist) {
               transaction.execute();
            }
         };
      });
   }

   @Override
   public String getName() {
      return "ATS ID Check";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS");
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets>" +
      //
      "<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + CHANGE_INVALID_IDS + "\" horizontalLabel=\"true\" defaultValue=\"false\"/>" +
      //
      "</xWidgets>";
   }
}
