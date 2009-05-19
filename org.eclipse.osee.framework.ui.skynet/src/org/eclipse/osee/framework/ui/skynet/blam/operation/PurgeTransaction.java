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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeTransactionJob;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class PurgeTransaction extends AbstractBlam {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getName()
    */
   @Override
   public String getName() {
      return "Delete Transaction";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      List<Integer> txs = Lib.stringToIntegerList(variableMap.getString("Transaction List"));
      boolean force = variableMap.getBoolean("Force Delete");
      int[] txIds = new int[txs.size()];
      for (int index = 0; index < txs.size(); index++) {
         txIds[index] = txs.get(index);
      }
      Job job = new PurgeTransactionJob(force, txIds);
      Jobs.startJob(job);
      job.join();
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XText\" displayName=\"Transaction List\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Force Delete\" />");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}