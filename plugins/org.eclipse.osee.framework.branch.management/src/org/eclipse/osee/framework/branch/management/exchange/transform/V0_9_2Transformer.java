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
package org.eclipse.osee.framework.branch.management.exchange.transform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class V0_9_2Transformer implements IOseeExchangeVersionTransformer {

   public V0_9_2Transformer() {
   }

   @Override
   public String applyTransform(ExchangeDataProcessor processor) throws OseeCoreException {
      Set<Integer> branchIds = convertBranchTable(processor);

      Set<Long> artifactGammmIds = new HashSet<Long>();

      convertArtifactAndConflicts(processor, artifactGammmIds);
      consolidateTxsAddressing(processor, branchIds, artifactGammmIds);

      processor.deleteExportItem("osee.artifact.data.xml");
      processor.renameExportItem("osee.arts.data.xml", "osee.artifact.data.xml");
      processor.deleteExportItem("osee.artifact.version.data.xml");
      return "0.9.2";
   }

   @Override
   public boolean isApplicable(String exportVersion) throws OseeCoreException {
      return exportVersion.startsWith("0.9.0") || exportVersion.startsWith("0.9.1");
   }

   @Override
   public void finalizeTransform(ExchangeDataProcessor processor) throws Exception {
   }

   private Set<Integer> convertBranchTable(ExchangeDataProcessor processor) throws OseeCoreException {
      Map<Integer, Integer> branchToBaseTx = new HashMap<Integer, Integer>(10000);
      processor.parse(ExportItem.OSEE_TX_DETAILS_DATA, new V0_9_2TxDetailsHandler(branchToBaseTx));
      processor.transform(ExportItem.OSEE_BRANCH_DATA, new V0_9_2BranchTransformer(branchToBaseTx));
      return branchToBaseTx.keySet();
   }

   private void convertArtifactAndConflicts(ExchangeDataProcessor processor, Set<Long> netGammaIds) throws OseeCoreException {
      Map<Long, Long> obsoleteGammaToNetGammaId = new HashMap<Long, Long>();

      Map<Integer, Long> artIdToNetGammaId = new HashMap<Integer, Long>(14000);
      processor.parse("osee.artifact.version.data.xml", new V0_9_2ArtifactVersionHandler(artIdToNetGammaId,
            obsoleteGammaToNetGammaId));
      processor.copyExportItem("osee.artifact.data.xml", "osee.arts.data.xml");
      processor.transform("osee.arts.data.xml", new V0_9_2ArtifactDataTransformer(artIdToNetGammaId));

      processor.transform(ExportItem.OSEE_CONFLICT_DATA, new V0_9_2ConflictTransformer(artIdToNetGammaId));

      processor.transform(ExportItem.OSEE_TXS_DATA, new V0_9_2TxsNetGammaTransformer(obsoleteGammaToNetGammaId));

      netGammaIds.addAll(obsoleteGammaToNetGammaId.values());
   }

   private void consolidateTxsAddressing(ExchangeDataProcessor processor, Set<Integer> branchIds, Set<Long> artifactGammmIds) throws OseeCoreException {
      for (Integer branchId : branchIds) {
         processor.parse(ExportItem.OSEE_TXS_DATA, new V0_9_2TxsConsolidateParser(branchId, artifactGammmIds));
         //TODO  ? Write new File?
      }
   }
}
