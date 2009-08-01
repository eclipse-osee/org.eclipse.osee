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
package org.eclipse.osee.define.traceability.jobs;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.define.traceability.operations.TraceUnitFromResourceOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Roberto E. Escobar
 */
public class ImportTraceUnitsJob extends Job {
   private final Branch importToBranch;
   private final boolean isRecursive;
   private final boolean isPersistChanges;
   private final boolean fileWithMultiPaths;
   private final URI source;
   private final String[] traceTypeIds;

   public ImportTraceUnitsJob(String jobName, Branch importToBranch, URI source, boolean isRecursive, boolean isPersistChanges, boolean fileWithMultiPaths, String... traceHandlerIds) {
      super(jobName);
      this.importToBranch = importToBranch;
      this.source = source;
      this.isRecursive = isRecursive;
      this.isPersistChanges = isPersistChanges;
      this.fileWithMultiPaths = fileWithMultiPaths;
      if (traceHandlerIds == null) {
         traceTypeIds = new String[0];
      } else {
         Set<String> traceSet = new HashSet<String>(Arrays.asList(traceHandlerIds));
         traceTypeIds = traceSet.toArray(new String[traceSet.size()]);
      }
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus status = Status.CANCEL_STATUS;
      try {
         monitor.beginTask(getName(), Integer.MAX_VALUE);
         if (isPersistChanges) {
            TraceUnitFromResourceOperation.importTraceFromTestUnits(monitor, source, isRecursive, fileWithMultiPaths,
                  importToBranch, traceTypeIds);
         } else {
            TraceUnitFromResourceOperation.printTraceFromTestUnits(monitor, source, isRecursive, fileWithMultiPaths,
                  traceTypeIds);
         }
         if (!monitor.isCanceled()) {
            status = Status.OK_STATUS;
         }
      } catch (Exception ex) {
         status = new Status(Status.ERROR, DefinePlugin.PLUGIN_ID, "", ex);
      } finally {
         monitor.done();
      }
      return status;
   }

}
