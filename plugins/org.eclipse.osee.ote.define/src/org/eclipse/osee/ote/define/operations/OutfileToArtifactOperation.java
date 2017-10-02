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
package org.eclipse.osee.ote.define.operations;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.threading.ThreadedWorkerExecutor;
import org.eclipse.osee.framework.core.threading.ThreadedWorkerFactory;
import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.ote.define.artifacts.ArtifactTestRunOperator;
import org.eclipse.osee.ote.define.parser.BaseOutfileParser;
import org.eclipse.osee.ote.define.utilities.OutfileDataCollector;
import org.eclipse.osee.ote.define.utilities.OutfileParserExtensionManager;

/**
 * @author Roberto E. Escobar
 */
public class OutfileToArtifactOperation {
   private final BranchId branch;
   private final List<URI> filesToImport;
   private final List<Artifact> results;
   private final List<URI> filesWithErrors;

   public OutfileToArtifactOperation(BranchId branch, URI... filesToImport) {
      this.branch = branch;
      this.filesToImport = Arrays.asList(filesToImport);
      this.results = new ArrayList<>();
      this.filesWithErrors = new ArrayList<>();
   }

   public void execute(final IProgressMonitor monitor) throws Exception {
      this.results.clear();
      monitor.setTaskName("Outfiles to Artifact Conversion...");
      final Artifact parent = getParentArtifact();

      ThreadedWorkerFactory<Object> outfileToArtifactFactory = new ThreadedWorkerFactory<Object>() {

         @Override
         public int getWorkSize() {
            return filesToImport.size();
         }

         @Override
         public Callable<Object> createWorker(int startIndex, int endIndex) {
            return new OutfileToArtifactCallable(monitor, parent, filesToImport.subList(startIndex, endIndex));
         }

      };

      ThreadedWorkerExecutor<Object> executor = new ThreadedWorkerExecutor<>(outfileToArtifactFactory, false);
      executor.executeWorkersBlocking();
   }

   private Artifact getParentArtifact()  {
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
      Artifact testFolder = OseeSystemArtifacts.getOrCreateArtifact(CoreArtifactTypes.Folder, "Test", branch);
      if (!root.isRelated(CoreRelationTypes.Default_Hierarchical__Child, testFolder)) {
         root.addChild(testFolder);
         root.persist("New Test Folder");
      }
      Artifact scriptFolder =
         OseeSystemArtifacts.getOrCreateArtifact(CoreArtifactTypes.Folder, "Test Script Results", branch);
      if (!testFolder.isRelated(CoreRelationTypes.Default_Hierarchical__Child, scriptFolder)) {
         testFolder.addChild(scriptFolder);
         testFolder.persist("New Test Script Results Folder");
      }
      return scriptFolder;
   }

   private void addChecksum(ArtifactTestRunOperator operator, URL targetURL) throws Exception {
      InputStream inputStream = null;
      try {
         inputStream = targetURL.openStream();
         String checkSum = ChecksumUtil.createChecksumAsString(inputStream, ChecksumUtil.MD5);
         operator.setChecksum(checkSum);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }

   private OutfileDataCollector getOutfileData(IProgressMonitor monitor, URL fileToImport) throws Exception {
      OutfileDataCollector collector = new OutfileDataCollector();
      BaseOutfileParser outfileParser = OutfileParserExtensionManager.getInstance().getOutfileParserFor(fileToImport);
      synchronized (outfileParser) {
         outfileParser.registerListener(collector);
         outfileParser.execute(monitor, fileToImport);
         outfileParser.deregisterListener(collector);
      }
      return collector;
   }

   public Artifact[] getResults() {
      return results.toArray(new Artifact[results.size()]);
   }

   public URI[] getUnparseableFiles() {
      return filesWithErrors.toArray(new URI[filesWithErrors.size()]);
   }

   private class OutfileToArtifactCallable implements Callable<Object> {

      private final IProgressMonitor monitor;
      private final Artifact parent;
      private final List<URI> filesToImport;

      public OutfileToArtifactCallable(IProgressMonitor monitor, Artifact parent, List<URI> filesToImport) {
         this.monitor = monitor;
         this.parent = parent;
         this.filesToImport = filesToImport;
      }

      @Override
      public Object call() throws Exception {
         for (URI targetUri : filesToImport) {
            ArtifactTestRunOperator operator = null;
            try {
               operator = ArtifactTestRunOperator.getNewArtifactWithOperator(branch);

               OutfileDataCollector collector = getOutfileData(monitor, targetUri.toURL());
               collector.populate(operator.getTestRunArtifact(), parent);

               String path = targetUri.toURL().toString();
               operator.setLocalOutfileURI(path);
               operator.setOutfileExtension(Lib.getExtension(path));
               addChecksum(operator, targetUri.toURL());
               synchronized (results) {
                  results.add(operator.getTestRunArtifact());
               }

               if (monitor.isCanceled() == true) {
                  break;
               }
            } catch (Exception ex) {
               if (operator == null || operator.getTestRunArtifact() == null) {
                  throw new Exception(
                     "Unable to create Test Run Artifact. Make sure type information exists in the selected branch.");
               }
               synchronized (filesWithErrors) {
                  filesWithErrors.add(targetUri);
               }
            }
            operator = null;
            monitor.worked(1);
         }
         return null;
      }

   }
}
