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
package org.eclipse.osee.define.ide.traceability.data;

import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.define.ide.internal.Activator;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public abstract class BaseTraceDataCache {

   private boolean isInitialized;
   private final BranchId branch;
   private final String traceType;

   public BaseTraceDataCache(String traceType, BranchId branch) {
      isInitialized = false;
      this.traceType = traceType;
      this.branch = branch;
   }

   public boolean isInitialized() {
      return isInitialized;
   }

   public BranchId getBranch() {
      return branch;
   }

   public final IStatus initialize(IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      try {
         reset();
         monitor.subTask(String.format("Loading %s from: [%s]", traceType, getBranch()));

         doBulkLoad(monitor);

         if (monitor.isCanceled() != true) {
            toReturn = Status.OK_STATUS;
         }
         isInitialized = true;
      } catch (Exception ex) {
         toReturn = new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format("Loading %s ", traceType), ex);
      }
      return toReturn;
   }

   protected abstract void doBulkLoad(IProgressMonitor monitor) throws Exception;

   protected void populateTraceMap(IProgressMonitor monitor, List<Artifact> artList, Map<String, Artifact> toPopulate) {
      for (Artifact artifact : artList) {
         toPopulate.put(asTraceMapKey(artifact), artifact);
      }
   }

   protected String asTraceMapKey(Artifact artifact) {
      return artifact.getName();
   }

   public void reset() {
      isInitialized = false;
   }
}
