/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.parsers.ExcelArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.NativeDocumentExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WholeWordDocumentExtractor;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.AttributeBasedArtifactResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.GuidBasedArtifactResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * Encapsulating parameter class for creating Artifact Import {@link IOperation}s <br/>
 * <br/>
 * Example:
 *
 * <pre>
 * {@code
 * ...
 * ArtifactImportOperationParameter <b>importParams</b> = new ArtifactImportOperationParameter();
 * importParameters.setSourceFile(sourceFile);
 * importParameters.setDestinationArtifact(destinationFolder);
 * importParameters.setExtractor(new WholeWordDocumentExtractor());
 * importParameters.setResolver(resolver);
 * importParameters.setGoverningTransaction(transaction);
 * ...
 * IOperation operation = ArtifactImportOperationFactory.createOperation(<b>importParams</b>);
 * ...
 * }
 * </pre>
 */
public class ArtifactImportOperationParameter {
   private File sourceFile;
   private Artifact destinationArtifact;
   private OperationLogger logger;
   private IArtifactExtractor extractor;
   private IArtifactImportResolver resolver;
   private boolean stopOnError;
   private SkynetTransaction governingTransaction;
   private boolean executeTransaction;

   public ArtifactImportOperationParameter() {
      this(null, null, null, null, null, false, null, false);
   }

   public ArtifactImportOperationParameter(File sourceFile, Artifact destinationArtifact, OperationLogger logger, IArtifactExtractor extractor, IArtifactImportResolver resolver, boolean stopOnError, SkynetTransaction governingTransaction, boolean executeTransaction) {
      this.sourceFile = sourceFile;
      this.destinationArtifact = destinationArtifact;
      this.logger = logger;
      this.extractor = extractor;
      this.resolver = resolver;
      this.stopOnError = stopOnError;
      this.governingTransaction = governingTransaction;
      this.executeTransaction = executeTransaction;
   }

   public SkynetTransaction getGoverningTransaction() {
      return governingTransaction;
   }

   /**
    * @param governingTransaction transaction governing the import process, if null, operation will create and execute
    * its own.
    */
   public void setGoverningTransaction(SkynetTransaction governingTransaction) {
      this.governingTransaction = governingTransaction;
   }

   public boolean isExecuteTransaction() {
      return executeTransaction;
   }

   /**
    * @param executeTransaction manual flag to execute passed in {@link SkynetTransaction}
    */
   public void setExecuteTransaction(boolean executeTransaction) {
      this.executeTransaction = executeTransaction;
   }

   public File getSourceFile() {
      return sourceFile;
   }

   /**
    * @param sourceFile being imported
    */
   public void setSourceFile(File sourceFile) {
      this.sourceFile = sourceFile;
   }

   public Artifact getDestinationArtifact() {
      return destinationArtifact;
   }

   /**
    * <pre>
    * destinationArtifact
    * |
    * |- some artifact A from sourceFile
    * |
    * `- some artifact B from sourceFile
    * </pre>
    *
    * @param destinationArtifact parent artifact under which all imported artifact will "live"
    */
   public void setDestinationArtifact(Artifact destinationArtifact) {
      this.destinationArtifact = destinationArtifact;
   }

   public OperationLogger getLogger() {
      return logger;
   }

   /**
    * @param logger used during this operation
    */
   public void setLogger(OperationLogger logger) {
      this.logger = logger;
   }

   public IArtifactExtractor getExtractor() {
      return extractor;
   }

   /**
    * @param extractor specific artifact extractor {@link ExcelArtifactExtractor}, {@link NativeDocumentExtractor},
    * {@link WholeWordDocumentExtractor}
    */
   public void setExtractor(IArtifactExtractor extractor) {
      this.extractor = extractor;
   }

   public IArtifactImportResolver getResolver() {
      return resolver;
   }

   /**
    * @param resolver encapsulated logic helping to resolve artifacts, most common are
    * {@link AttributeBasedArtifactResolver} or {@link GuidBasedArtifactResolver}
    */
   public void setResolver(IArtifactImportResolver resolver) {
      this.resolver = resolver;
   }

   public boolean isStopOnError() {
      return stopOnError;
   }

   public void setStopOnError(boolean stopOnError) {
      this.stopOnError = stopOnError;
   }
}