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
package org.eclipse.osee.define.blam.operation;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;

/**
 * @author Ryan D. Brooks
 */
public class ExperimentalBlam extends AbstractBlam {
   private static final String UpdateRelationModType =
         "UPDATE osee_define_rel_link SET modification_id = 2 WHERE gamma_id = ?";
   private static final String UpdateTxsCurrent = "UPDATE osee_define_txs SET tx_current = 1 WHERE gamma_id = ?";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      int[] gammaIds = new int[Gamma1.gammaIds1.length + Gamma2.gammaIds2.length];
      System.arraycopy(Gamma1.gammaIds1, 0, gammaIds, 0, Gamma1.gammaIds1.length);
      System.arraycopy(Gamma2.gammaIds2, 0, gammaIds, Gamma1.gammaIds1.length, Gamma2.gammaIds2.length);
      List<Object[]> updateParameters = new ArrayList<Object[]>(gammaIds.length);

      for (int gammaId : gammaIds) {
         updateParameters.add(new Object[] {SQL3DataType.INTEGER, gammaId});
      }

      ConnectionHandler.runPreparedUpdateBatch(UpdateRelationModType, updateParameters);
      ConnectionHandler.runPreparedUpdateBatch(UpdateTxsCurrent, updateParameters);
   }
}