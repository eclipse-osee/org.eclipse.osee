/*
 * Created on Apr 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

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

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus status = Status.CANCEL_STATUS;
      try {
         XResultData resultData = new XResultData();
         if (isPersistChanges) {
            resultData.log("Persisting Changes");
            TraceUnitFromResourceOperation.importTraceFromTestUnits(monitor, source, isRecursive, fileWithMultiPaths,
                  resultData, importToBranch, traceTypeIds);
         } else {
            resultData.log("Report-Only, Changes are not persisted");
            TraceUnitFromResourceOperation.printTraceFromTestUnits(monitor, source, isRecursive, fileWithMultiPaths,
                  resultData, traceTypeIds);
         }
         resultData.report(getName());
         if (!monitor.isCanceled()) {
            status = Status.OK_STATUS;
         }
      } catch (Exception ex) {
         status = new Status(Status.ERROR, DefinePlugin.PLUGIN_ID, "", ex);
      }
      return status;
   }

}
