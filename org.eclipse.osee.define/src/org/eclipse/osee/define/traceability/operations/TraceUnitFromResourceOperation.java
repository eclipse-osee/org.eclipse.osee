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
import java.util.List;
import java.util.Set;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.define.traceability.TraceUnitExtensionManager;
import org.eclipse.osee.define.traceability.TraceUnitExtensionManager.TraceHandler;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Roberto E. Escobar
 */
public class TraceUnitFromResourceOperation {

   public static Set<String> getTraceUnitHandlerIds() throws OseeCoreException {
      return TraceUnitExtensionManager.getInstance().getTraceUnitHandlerIds();
   }

   private static ResourceToTraceUnit getResourceToTestUnit(URI source, boolean isRecursive, boolean isFileWithMultiplePaths, String... testUnitTraceIds) throws OseeCoreException {
      checkSourceArgument(source);
      checkTraceUnitHandlerIdsArgument(testUnitTraceIds);

      ResourceToTraceUnit operation = new ResourceToTraceUnit(source, isRecursive, isFileWithMultiplePaths);
      TraceUnitExtensionManager traceManager = TraceUnitExtensionManager.getInstance();
      for (String traceUnitHandlerId : testUnitTraceIds) {

         TraceHandler handler = traceManager.getTraceUnitHandlerById(traceUnitHandlerId);
         if (handler != null) {
            operation.addTraceUnitHandler(handler.getLocator(), handler.getParser());
         }
      }
      return operation;
   }

   public static void printTraceFromTestUnits(IProgressMonitor monitor, URI source, boolean isRecursive, boolean isFileWithMultiplePaths, String... traceUnitHandlerIds) throws OseeCoreException {
      ResourceToTraceUnit operation =
            getResourceToTestUnit(source, isRecursive, isFileWithMultiplePaths, traceUnitHandlerIds);
      if (monitor == null) {
         monitor = new NullProgressMonitor();
      }
      operation.addTraceProcessor(new TraceUnitReportProcessor());
      operation.execute(monitor);
   }

   public static void importTraceFromTestUnits(IProgressMonitor monitor, URI source, boolean isRecursive, boolean isFileWithMultiplePaths, Branch importToBranch, String... traceUnitHandlerIds) throws OseeCoreException {
      checkBranchArguments(importToBranch);

      ResourceToTraceUnit operation =
            getResourceToTestUnit(source, isRecursive, isFileWithMultiplePaths, traceUnitHandlerIds);
      if (monitor == null) {
         monitor = new NullProgressMonitor();
      }
      operation.addTraceProcessor(new TraceUnitToArtifactProcessor(importToBranch));
      operation.execute(monitor);
   }

   private static void checkTraceUnitHandlerIdsArgument(String... traceUnitHandlerIds) throws OseeCoreException {
      if (traceUnitHandlerIds == null) {
         throw new OseeArgumentException("Test unit trace ids was null");
      }
      if (traceUnitHandlerIds.length == 0) {
         throw new OseeArgumentException("Test unit trace ids was empty");
      }

      try {
         Set<String> ids = getTraceUnitHandlerIds();
         List<String> notFound = Collections.setComplement(Arrays.asList(traceUnitHandlerIds), ids);
         if (!notFound.isEmpty()) {
            throw new OseeArgumentException(String.format("Invalid test unit trace id(s) [%s]", notFound));
         }
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private static void checkSourceArgument(URI source) throws OseeArgumentException {
      if (source == null) {
         throw new OseeArgumentException("Source was null");
      }
      try {
         IFileStore fileStore = EFS.getStore(source);
         IFileInfo fileInfo = fileStore.fetchInfo();
         if (!fileInfo.exists()) {
            throw new OseeArgumentException(String.format("Unable to access source: [%s]", source));
         }
      } catch (CoreException ex) {
         throw new OseeArgumentException(String.format("Unable to access source: [%s]", source));
      }
   }

   private static void checkBranchArguments(Branch importToBranch) throws OseeArgumentException {
      if (importToBranch == null) {
         throw new OseeArgumentException("Branch to import into was null");
      }
      if (!importToBranch.getBranchType().isOfType(BranchType.WORKING)) {
         throw new OseeArgumentException(String.format("Branch to import into was not a working branch: [%s]",
               importToBranch));
      }
   }
}
