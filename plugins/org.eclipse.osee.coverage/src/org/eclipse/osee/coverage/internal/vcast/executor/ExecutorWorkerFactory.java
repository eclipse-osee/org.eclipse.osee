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
package org.eclipse.osee.coverage.internal.vcast.executor;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.vcast.CoverageImportData;
import org.eclipse.osee.framework.core.threading.ThreadedWorkerExecutor;
import org.eclipse.osee.framework.core.threading.ThreadedWorkerFactory;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.vcast.model.VcpResultsFile;
import org.eclipse.osee.vcast.model.VcpSourceFile;

/**
 * @author Donald G. Dunne
 */
public class ExecutorWorkerFactory {

   private final IProgressMonitor monitor;
   private final Map<String, CoverageUnit> fileNumToCoverageUnit;
   private final CoverageImport coverageImport;
   private final CoverageImportData input;

   private final XResultData logger;
   private final List<File> processedFiles;

   public ExecutorWorkerFactory(IProgressMonitor monitor, Map<String, CoverageUnit> fileNumToCoverageUnit, CoverageImport coverageImport, CoverageImportData input) {
      this.monitor = monitor;
      this.fileNumToCoverageUnit = fileNumToCoverageUnit;
      this.coverageImport = coverageImport;
      this.input = input;

      this.logger = coverageImport.getLog();
      this.processedFiles = coverageImport.getImportRecordFiles();
   }

   public void processSourceFilesProcessor(List<VcpSourceFile> sourceFiles) throws OseeCoreException {
      monitor.beginTask("Importing Source File Data", sourceFiles.size());

      ThreadedWorkerFactory<Object> factory = new VcpSourceFilesWorkerFactory(sourceFiles);
      process(factory);
   }

   public void processResultFilesProcessor(List<VcpResultsFile> resultFiles) throws OseeCoreException {
      monitor.beginTask("Importing Test Unit Data", resultFiles.size());

      ThreadedWorkerFactory<Object> factory = new VcpResultFilesWorkerFactory(resultFiles);
      process(factory);
   }

   private static <T> void process(ThreadedWorkerFactory<T> factory) throws OseeCoreException {
      ThreadedWorkerExecutor<T> executor = new ThreadedWorkerExecutor<T>(factory, true);
      executor.executeWorkersBlocking();
   }

   private class VcpResultFilesWorkerFactory implements ThreadedWorkerFactory<Object> {

      private final List<VcpResultsFile> vcpResultsFiles;

      public VcpResultFilesWorkerFactory(List<VcpResultsFile> vcpResultsFiles) {
         this.vcpResultsFiles = vcpResultsFiles;
      }

      @Override
      public int getWorkSize() {
         return vcpResultsFiles.size();
      }

      @Override
      public Callable<Object> createWorker(int startIndex, int endIndex) {
         AtomicInteger numberProcessed = new AtomicInteger();
         List<VcpResultsFile> toProcess = vcpResultsFiles.subList(startIndex, endIndex);
         return new VcpResultsFileWorker(logger, monitor, numberProcessed, getWorkSize(), toProcess, processedFiles,
            fileNumToCoverageUnit);
      }
   }

   private class VcpSourceFilesWorkerFactory implements ThreadedWorkerFactory<Object> {

      private final List<VcpSourceFile> filesToProcess;
      private final Map<VcpSourceFile, VcpSourceLisFile> sourceToFileList =
         new ConcurrentHashMap<VcpSourceFile, VcpSourceLisFile>();
      private final Map<VcpSourceFile, CoverageDataFileParser> sourceToDataParser =
         new ConcurrentHashMap<VcpSourceFile, CoverageDataFileParser>();

      public VcpSourceFilesWorkerFactory(List<VcpSourceFile> filesToProcess) {
         this.filesToProcess = filesToProcess;
      }

      @Override
      public int getWorkSize() {
         return filesToProcess.size();
      }

      @Override
      public Callable<Object> createWorker(int startIndex, int endIndex) {
         AtomicInteger numberProcessed = new AtomicInteger();
         List<VcpSourceFile> toProcess = filesToProcess.subList(startIndex, endIndex);
         return new VcpSourceFileWorker(logger, monitor, numberProcessed, getWorkSize(), toProcess, processedFiles,
            fileNumToCoverageUnit, coverageImport, input, sourceToFileList, sourceToDataParser);
      }

   }
}
