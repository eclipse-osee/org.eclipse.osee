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
package org.eclipse.osee.define.traceability.operations;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class ImportTraceUnitsOperation extends AbstractOperation {
   private final BranchId importToBranch;
   private final boolean isRecursive;
   private final boolean isPersistChanges;
   private final boolean fileWithMultiPaths;
   private final boolean addGuidToSourceFile;
   private final Iterable<URI> sources;
   private final String[] traceTypeIds;
   private final boolean includeImpd;

   public ImportTraceUnitsOperation(String jobName, BranchId importToBranch, Iterable<URI> sources, boolean isRecursive, boolean isPersistChanges, boolean fileWithMultiPaths, boolean addGuidToSourceFile, boolean includeImpd, String... traceHandlerIds) {
      super("ImportTraceUnitsOperation", Activator.PLUGIN_ID);
      this.importToBranch = importToBranch;
      this.sources = sources;
      this.isRecursive = isRecursive;
      this.isPersistChanges = isPersistChanges;
      this.fileWithMultiPaths = fileWithMultiPaths;
      this.addGuidToSourceFile = addGuidToSourceFile;
      this.includeImpd = includeImpd;
      if (traceHandlerIds == null) {
         traceTypeIds = new String[0];
      } else {
         Set<String> traceSet = new HashSet<>(Arrays.asList(traceHandlerIds));
         traceTypeIds = traceSet.toArray(new String[traceSet.size()]);
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor)  {
      if (isPersistChanges) {
         TraceUnitFromResourceOperation.importTraceFromTestUnits(monitor, sources, isRecursive, fileWithMultiPaths,
            importToBranch, addGuidToSourceFile, includeImpd, traceTypeIds);
      } else {
         TraceUnitFromResourceOperation.printTraceFromTestUnits(monitor, sources, isRecursive, fileWithMultiPaths,
            addGuidToSourceFile, includeImpd, traceTypeIds);
      }
   }

}
