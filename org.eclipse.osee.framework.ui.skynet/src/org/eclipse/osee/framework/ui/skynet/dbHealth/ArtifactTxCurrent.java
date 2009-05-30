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
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.util.HashSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.AHTML;

/**
 * @author Theron Virgin
 */
public class ArtifactTxCurrent extends DatabaseHealthOperation {
   private HashSet<LocalTxData> multipleSet = null;
   private HashSet<Pair<Integer, Integer>> noneSet = null;

   public ArtifactTxCurrent() {
      super("Tx_Current Artifact Errors");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#doHealthCheck(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      String[] columnHeaders = new String[] {"Count", "Art id", "Branch id"};
      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      if (isShowDetailsEnabled()) {
         sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
         sbFull.append(AHTML.addRowSpanMultiColumnTable("Artifacts with no tx_current set", columnHeaders.length));
      }

      if (!isFixOperationEnabled() || noneSet == null) {
         noneSet = HealthHelper.getNoTxCurrentSet("art_id", "osee_artifact_version", getAppendable(), " Artifacts");
         monitor.worked(calculateWork(0.15));
         checkForCancelledStatus(monitor);
      }
      if (isShowDetailsEnabled()) {
         HealthHelper.dumpDataNone(sbFull, noneSet);
         columnHeaders = new String[] {"Count", "Art id", "Branch id", "Num TX_Currents"};
         sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
         sbFull.append(AHTML.addRowSpanMultiColumnTable("Artifacts with multiple tx_currents set", columnHeaders.length));
      }

      if (!isFixOperationEnabled() || multipleSet == null) {
         //Multiple TX Currents Set
         multipleSet =
               HealthHelper.getMultipleTxCurrentSet("art_id", "osee_artifact_version", getAppendable(), " Artifacts");
      }
      if (isShowDetailsEnabled()) {
         HealthHelper.dumpDataMultiple(sbFull, multipleSet);
      }

      if (isFixOperationEnabled()) {
         /** Duplicate TX_current Cleanup **/
         monitor.worked(10);
         monitor.subTask("Cleaning up multiple Tx_currents");
         HealthHelper.cleanMultipleTxCurrent("art_id", "osee_artifact_version", getAppendable(), multipleSet);
         monitor.worked(20);
         monitor.subTask("Cleaning up no Tx_currents");
         HealthHelper.cleanNoTxCurrent("art_id", "osee_artifact_version", getAppendable(), noneSet);
         multipleSet = null;
         noneSet = null;
      }

      if (isShowDetailsEnabled()) {
         HealthHelper.endTable(sbFull, getVerifyTaskName());
      }
   }
}
