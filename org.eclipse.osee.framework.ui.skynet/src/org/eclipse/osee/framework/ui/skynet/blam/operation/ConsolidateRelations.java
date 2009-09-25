/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.JoinUtility.IdJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.AIFile;
import org.eclipse.osee.framework.skynet.core.utility.OseeData;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class ConsolidateRelations extends AbstractBlam {
   private static final String SELECT_RELATIONS =
         "select * from osee_relation_link order by rel_link_type_id, a_art_id, b_art_id, gamma_id";

   private static final String UPDATE_TXS_GAMMAS = "update osee_txs set gamma_id = ? where gamma_id = ?";

   private final ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
   private final List<Object[]> gammaMap = new ArrayList<Object[]>(10000);
   private final List<Integer> obsoleteGammas = new ArrayList<Integer>();
   private int previousRelationTypeId;
   private int previousArtifactAId;
   private int previousArtiafctBId;
   private int soleGamma;
   private int soleOrderA;
   private int soleOrderB;
   private String soleRationale;
   boolean materiallyDifferent;
   private static final int OBSOLETE_INDEX = 1;
   private static final int SOLE_INDEX = 0;

   @Override
   public String getName() {
      return "Consolidate Relations";
   }

   private void init() {
      previousRelationTypeId = -1;
      previousArtifactAId = -1;
      previousArtiafctBId = -1;
      materiallyDifferent = true;
      gammaMap.clear();
      obsoleteGammas.clear();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      init();

      try {
         chStmt.runPreparedQuery(10000, SELECT_RELATIONS);
         while (chStmt.next()) {
            int relationTypeId = chStmt.getInt("rel_link_type_id");
            int artifactAId = chStmt.getInt("a_art_id");
            int artiafctBId = chStmt.getInt("b_art_id");

            if (isNextConceptualRelation(relationTypeId, artifactAId, artiafctBId)) {
               consolidate();
               initNextConceptualRelation(relationTypeId, artifactAId, artiafctBId);
            } else {
               obsoleteGammas.add(chStmt.getInt("gamma_id"));
               checkIfMateriallyDifferent(chStmt);
            }
         }
      } finally {
         chStmt.close();
      }
      updateGammas();
   }

   private void consolidate() {
      if (!materiallyDifferent) {
         for (Integer obsoleteGamma : obsoleteGammas) {
            gammaMap.add(new Object[] {soleGamma, obsoleteGamma});
         }
      }
   }

   private void updateGammas() throws Exception {
      IdJoinQuery gammaJoin = JoinUtility.createIdJoinQuery();
      Map<Integer, Integer> tempGammaMap = new HashMap<Integer, Integer>();

      for (Object[] gammas : gammaMap) {
         gammaJoin.add((Integer) gammas[OBSOLETE_INDEX]);
         tempGammaMap.put((Integer) gammas[OBSOLETE_INDEX], (Integer) gammas[SOLE_INDEX]);
      }
      gammaJoin.store();

      StringBuilder builder = new StringBuilder(100000);
      gammaMap.clear();
      try {
         chStmt.runPreparedQuery(
               10000,
               "select distinct * from osee_join_id idj, osee_txs txs where idj.query_id = ? and idj.id = txs.gamma_id order by gamma_id, transaction_id",
               gammaJoin.getQueryId());
         while (chStmt.next()) {
            Integer obsoleteGammaId = chStmt.getInt("gamma_id");
            Integer transactionId = chStmt.getInt("transaction_id");

            builder.append(obsoleteGammaId.toString());
            builder.append(",");
            builder.append(transactionId.toString());
            builder.append(",");
            Integer soleGamma = tempGammaMap.get(obsoleteGammaId);
            builder.append(soleGamma.toString());
            builder.append("\n");

            gammaMap.add(new Object[] {soleGamma, obsoleteGammaId});
         }
      } finally {
         chStmt.close();
      }

      gammaJoin.delete();

      System.out.println("number of updates: " + gammaMap.size());

      IFile iFile = OseeData.getIFile("consolidateRelations_" + Lib.getDateTimeString() + ".csv");
      AIFile.writeToFile(iFile, Lib.stringToInputStream(builder.toString()));

      System.out.println("Number of txs rows updated: " + ConnectionHandler.runBatchUpdate(UPDATE_TXS_GAMMAS, gammaMap));
   }

   private boolean isNextConceptualRelation(int relationTypeId, int artifactAId, int artiafctBId) throws OseeCoreException {
      return previousRelationTypeId != relationTypeId || previousArtifactAId != artifactAId || previousArtiafctBId != artiafctBId;
   }

   private void checkIfMateriallyDifferent(ConnectionHandlerStatement chStmt) throws OseeCoreException {
      if (materiallyDifferent) {
         String currentRationale = chStmt.getString("rationale");
         materiallyDifferent |= Strings.isValid(currentRationale) && !currentRationale.equals(soleRationale);
         if (RelationTypeManager.getType(chStmt.getInt("rel_link_type_id")).isOrdered()) {
            materiallyDifferent |= chStmt.getInt("a_order") != 0 && soleOrderA != chStmt.getInt("a_order");
            materiallyDifferent |= chStmt.getInt("b_order") != 0 && soleOrderB != chStmt.getInt("b_order");
         }
      }
   }

   private void initNextConceptualRelation(int relationTypeId, int artifactAId, int artiafctBId) throws OseeCoreException {
      obsoleteGammas.clear();
      previousRelationTypeId = relationTypeId;
      previousArtifactAId = artifactAId;
      previousArtiafctBId = artiafctBId;
      soleGamma = chStmt.getInt("gamma_id");
      soleOrderA = chStmt.getInt("a_order");
      soleOrderB = chStmt.getInt("b_order");
      soleRationale = chStmt.getString("rationale");
      materiallyDifferent = false;
   }

   @Override
   public String getDescriptionUsage() {
      return "Consolidate Relations";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}