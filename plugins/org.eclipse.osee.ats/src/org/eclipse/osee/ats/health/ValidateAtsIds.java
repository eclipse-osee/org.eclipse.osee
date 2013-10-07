/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.health;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author John Misinco
 */
// this whole class is temp code
public class ValidateAtsIds extends AbstractBlam {

   private static final String SET_ATTRIBUTE = "Persist attribute on missing?";
   private static final String RUN_ON_STATEMACHINE = "Run on state machine types?";
   private static final String RUN_ON_ACTION = "Run on action types?";
   private static final String START_ON_ART_NUMBER = "Start artifact number";
   private static final String PARTITION_SIZE = "Divide artifacts into partitions of";

   @Override
   public String getName() {
      return "Validate AtsIds";
   }

   @Override
   public Collection<String> getCategories() {
      return Collections.singleton("ATS.ADMIN");
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder sb = new StringBuilder();
      sb.append("<xWidgets>");
      sb.append(String.format(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"%s\" labelAfter=\"true\" horizontalLabel=\"true\" defaultValue=\"false\"/>",
         SET_ATTRIBUTE));
      sb.append(String.format(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"%s\" labelAfter=\"true\" horizontalLabel=\"true\" defaultValue=\"false\"/>",
         RUN_ON_STATEMACHINE));
      sb.append(String.format(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"%s\" labelAfter=\"true\" horizontalLabel=\"true\" defaultValue=\"false\"/>",
         RUN_ON_ACTION));
      sb.append(String.format(
         "<XWidget xwidgetType=\"XText\" displayName=\"%s\" labelAfter=\"true\" horizontalLabel=\"true\" defaultValue=\"1000\"/>",
         PARTITION_SIZE));
      sb.append(String.format(
         "<XWidget xwidgetType=\"XText\" displayName=\"%s\" labelAfter=\"true\" horizontalLabel=\"true\" defaultValue=\"0\"/>",
         START_ON_ART_NUMBER));
      sb.append("</xWidgets>");
      return sb.toString();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      IArtifactType stateMachine = TokenFactory.createArtifactType(0x0000000000000047L, "State Machine");
      boolean persist = variableMap.getBoolean(SET_ATTRIBUTE);
      int partitionSize = Integer.parseInt(variableMap.getString(PARTITION_SIZE));
      int startNumber = Integer.parseInt(variableMap.getString(START_ON_ART_NUMBER));
      if (variableMap.getBoolean(RUN_ON_ACTION)) {
         checkArtifactType(AtsArtifactTypes.Action, persist, partitionSize, startNumber);
      }
      if (variableMap.getBoolean(RUN_ON_STATEMACHINE)) {
         checkArtifactType(stateMachine, persist, partitionSize, startNumber);
      }
      log("Complete");
   }

   private void checkArtifactType(IArtifactType toCheck, boolean persist, int partitionSize, int startNumber) throws OseeCoreException {
      SkynetTransaction tx = null;
      QueryBuilderArtifact builder = ArtifactQuery.createQueryBuilder(CoreBranches.COMMON);
      builder.andIsOfType(toCheck);
      List<Integer> result = builder.getSearchResult().getIds();
      for (int i = startNumber; i < result.size(); i += partitionSize) {
         if (persist) {
            tx = TransactionManager.createTransaction(CoreBranches.COMMON, "Update AtsId Attribute");
         }
         int toIdx = Math.min(result.size(), i + partitionSize);
         List<Integer> subList = result.subList(i, toIdx);
         List<Artifact> artifactListFromIds = null;
         try {
            artifactListFromIds = ArtifactQuery.getArtifactListFromIds(subList, CoreBranches.COMMON);
            for (Artifact art : artifactListFromIds) {
               String artHrid = art.getHrid();
               String atsId = art.getSoleAttributeValueAsString(AtsAttributeTypes.AtsId, "");
               if (!atsId.equals(artHrid)) {
                  if (persist) {
                     art.setSoleAttributeFromString(AtsAttributeTypes.AtsId, artHrid);
                     art.persist(tx);
                  }
               }
            }
         } catch (Exception ex) {
            // do nothing
         }
         if (persist) {
            int size = tx.getArtifactReferences().size();
            log("Persisting: " + size + " artifacts in range: " + i + " to " + toIdx);
            tx.execute();
            tx = null;
         }
         if (Conditions.hasValues(artifactListFromIds)) {
            for (Artifact art : artifactListFromIds) {
               ArtifactCache.deCache(art);
            }
            System.gc();
         }
      }
   }

}
