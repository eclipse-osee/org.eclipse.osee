/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.HashMap;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Megumi Telles
 */
public class ChangeArtifactNameOperation extends AbstractOperation {

   private final String renamePairs;
   private final BranchId branch;

   public ChangeArtifactNameOperation(OperationLogger logger, String renamePairs, BranchId branch) {
      super("Rename Artifact Name", Activator.PLUGIN_ID, logger);
      this.renamePairs = renamePairs;
      this.branch = branch;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (renamePairs.isEmpty()) {
         throw new OseeArgumentException("Must specify at least one pair.");
      }
      HashMap<String, String> pairs = getPairs();
      SkynetTransaction tx = TransactionManager.createTransaction(branch, "Rename Artifact");
      for (Entry<String, String> entry : pairs.entrySet()) {
         Artifact artifact = ArtifactQuery.getArtifactFromIdOrNull(Integer.valueOf(entry.getKey()), branch,
            DeletionFlag.EXCLUDE_DELETED);
         if (artifact != null) {
            if (!artifact.getName().equals(entry.getValue())) {
               artifact.setName(entry.getValue());
               artifact.persist(tx);
            }
         }
      }
      tx.execute();
   }

   private HashMap<String, String> getPairs() {
      HashMap<String, String> pairs = new HashMap<>();
      String[] splitPairs = renamePairs.split("[\n\r]+");
      for (String pair : splitPairs) {
         String[] splitPair = pair.split("[\\s,]+");
         if (splitPair.length != 2) {
            throw new OseeArgumentException("Invalid pairs");
         }
         pairs.put(splitPair[0], splitPair[1]);
      }
      return pairs;
   }

}
