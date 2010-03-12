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
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.ote.define.artifacts.TestRunOperator;
import org.eclipse.osee.ote.define.parser.BaseOutfileParser;
import org.eclipse.osee.ote.define.utilities.OutfileDataCollector;
import org.eclipse.osee.ote.define.utilities.OutfileParserExtensionManager;

/**
 * @author Roberto E. Escobar
 */
public class OutfileToArtifactOperation {
   private Branch branch;
   private URI[] filesToImport;
   private List<Artifact> results;
   private List<URI> filesWithErrors;

   public OutfileToArtifactOperation(Branch branch, URI... filesToImport) {
      this.branch = branch;
      this.filesToImport = filesToImport;
      this.results = new ArrayList<Artifact>();
      this.filesWithErrors = new ArrayList<URI>();
   }

   public void execute(IProgressMonitor monitor) throws Exception {
      this.results.clear();
      monitor.setTaskName("Outfiles to Artifact Conversion...");
      for (URI targetUri : filesToImport) {
         TestRunOperator operator = null;
         try {
            operator = TestRunOperator.getNewArtifactWithOperator(branch);

            OutfileDataCollector collector = getOutfileData(monitor, targetUri.toURL());
            collector.populate(operator.getTestRunArtifact());

            String path = targetUri.toURL().toString();
            operator.setLocalOutfileURI(path);
            operator.setOutfileExtension(Lib.getExtension(path));
            addChecksum(operator, targetUri.toURL());
            results.add(operator.getTestRunArtifact());

            if (monitor.isCanceled() == true) {
               break;
            }
         } catch (Exception ex) {
            if (operator.getTestRunArtifact() == null) {
               throw new Exception(
                     "Unable to create Test Run Artifact. Make sure type information exists in the selected branch.");
            }
            filesWithErrors.add(targetUri);
         }
         operator = null;
         monitor.worked(1);
      }
   }

   private void addChecksum(TestRunOperator operator, URL targetURL) throws Exception {
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
      outfileParser.registerListener(collector);
      outfileParser.execute(monitor, fileToImport);
      outfileParser.deregisterListener(collector);
      return collector;
   }

   public Artifact[] getResults() {
      return results.toArray(new Artifact[results.size()]);
   }

   public URI[] getUnparseableFiles() {
      return filesWithErrors.toArray(new URI[filesWithErrors.size()]);
   }
}
