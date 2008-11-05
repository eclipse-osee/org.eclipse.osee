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
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Theron Virgin
 */
public class RelLinkTxCurrent extends DatabaseHealthTask {
   private HashSet<LocalTxData> multipleSet = null;
   private HashSet<Pair<Integer, Integer>> noneSet = null;

   public String getFixTaskName() {
      return "Fix TX_Current Relation Link Errors";
   }

   public String getVerifyTaskName() {
      return "Check for TX_Current Relation Link Errors";
   }

   public void run(VariableMap variableMap, IProgressMonitor monitor, Operation operation, StringBuilder builder, boolean showDetails) throws Exception {
      monitor.beginTask("Verify TX_Current Relation Link Errors", 100);
      String[] columnHeaders = new String[] {"Count", "Rel Link Id", "Branch id"};
      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      if (showDetails) {
         sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
         sbFull.append(AHTML.addRowSpanMultiColumnTable("Relation Links with no tx_current set", columnHeaders.length));
      }
      if (operation.equals(Operation.Verify) || noneSet == null) {
         noneSet = HealthHelper.getNoTxCurrentSet("rel_link_id", "osee_relation_link", builder, " Relation Links");
         monitor.worked(15);
         if (monitor.isCanceled()) return;
      }
      if (showDetails) {
         HealthHelper.dumpDataNone(sbFull, noneSet);
         columnHeaders = new String[] {"Count", "Relation Link id", "Branch id", "Num TX_Currents"};
         sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
         sbFull.append(AHTML.addRowSpanMultiColumnTable("Relation Links with multiple tx_currents set",
               columnHeaders.length));
      }
      if (operation.equals(Operation.Verify) || multipleSet == null) {
         //Multiple TX Currents Set
         multipleSet =
               HealthHelper.getMultipleTxCurrentSet("rel_link_id", "osee_relation_link", builder, " Relation Links");
      }
      if (showDetails) {
         HealthHelper.dumpDataMultiple(sbFull, multipleSet);
      }

      if (operation.equals(Operation.Fix)) {
         /** Duplicate TX_current Cleanup **/
         monitor.worked(10);
         monitor.subTask("Cleaning up multiple Tx_currents");
         HealthHelper.cleanMultipleTxCurrent("rel_link_id", "osee_relation_link", builder, multipleSet);
         monitor.worked(20);
         monitor.subTask("Cleaning up multiple Tx_currents");
         HealthHelper.cleanNoTxCurrent("rel_link_id", "osee_relation_link", builder, noneSet);
         multipleSet = null;
         noneSet = null;
      }

      if (showDetails) {
         HealthHelper.endTable(sbFull, getVerifyTaskName());
      }
   }
}
