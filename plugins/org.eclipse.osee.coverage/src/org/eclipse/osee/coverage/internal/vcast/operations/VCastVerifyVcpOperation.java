/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.internal.vcast.operations;

import java.io.File;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.vcast.CoverageImportData;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;
import org.eclipse.osee.vcast.model.VCastVcp;

public class VCastVerifyVcpOperation extends AbstractOperation {

   private final XResultData logger;
   private final CoverageImportData input;
   private final VCastVcp vCastVcp;

   public VCastVerifyVcpOperation(XResultData logger, CoverageImportData input, VCastVcp vCastVcp) {
      super("Load VcastVcp", Activator.PLUGIN_ID);
      this.logger = logger;
      this.input = input;
      this.vCastVcp = vCastVcp;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      monitor.setTaskName("Verifing Number of vcast.vcp results files and .dat files");
      // Verifying number results files reported in vcast.vcp with vcast/results/*.dat files
      logger.log("\nVerifying number results files reported in vcast.vcp with vcast/results/*.dat files");
      int numVcastVcpDatFiles = vCastVcp.getResultsFiles().size();
      List<String> filenames =
         Lib.readListFromDir(new File(
            input.getVCastDirectory() + File.separator + "vcast" + File.separator + "results" + File.separator),
            new MatchFilter(".*\\.(DAT||dat)"), false);
      if (numVcastVcpDatFiles != filenames.size()) {
         logger.error(String.format(
            "Number of results files in Vcast.vcp [%d] doesn't match number of vcast/results/*.dat files [%d]",
            numVcastVcpDatFiles, filenames.size()));
      } else {
         logger.log("Ok");
      }
   }
}
