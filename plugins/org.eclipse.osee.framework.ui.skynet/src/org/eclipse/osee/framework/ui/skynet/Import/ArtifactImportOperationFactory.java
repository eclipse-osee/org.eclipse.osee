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

package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.core.operation.OperationBuilder;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.CompleteArtifactImportOperation;
import org.eclipse.osee.framework.skynet.core.importing.operations.FilterArtifactTypesByAttributeTypes;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughToRealArtifactOperation;
import org.eclipse.osee.framework.skynet.core.importing.operations.SourceToRoughArtifactOperation;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.ArtifactValidationCheckOperation;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public final class ArtifactImportOperationFactory {

   private ArtifactImportOperationFactory() {
      super();
   }

   /**
    * <p>
    * Create a CompositeOperation, full import sequence.<br/>
    * <ol>
    * <li>SourceToRoughArtifactOperation</li>
    * <li>RoughToRealArtifactOperation</li>
    * <li>ArtifactValidationCheckOperation</li>
    * <li>CompleteArtifactImportOperation</li>
    * </ol>
    * </p>
    * <br/>
    *
    * @param param
    * @return
    */
   public static IOperation completeOperation(ArtifactImportOperationParameter param) {
      return completeOperation(param.getSourceFile(), param.getDestinationArtifact(), param.getLogger(),
         param.getExtractor(), param.getResolver(), param.isStopOnError(), param.getGoverningTransaction(),
         param.isExecuteTransaction());
   }

   public static IOperation completeOperation(File sourceFile, Artifact destinationArtifact, OperationLogger logger, IArtifactExtractor extractor, IArtifactImportResolver resolver, boolean stopOnError, SkynetTransaction governingTransaction, boolean executeTransaction) {
      CheckAndThrow(sourceFile, destinationArtifact, extractor, resolver);

      RoughArtifactCollector collector = new RoughArtifactCollector(new RoughArtifact(RoughArtifactKind.PRIMARY));

      if (logger == null) {
         logger = NullOperationLogger.getSingleton();
      }

      SkynetTransaction transaction = governingTransaction;
      if (transaction == null) {
         executeTransaction = true;
         transaction = TransactionManager.createTransaction(destinationArtifact.getBranch(),
            "ArtifactImportOperationFactory: Artifact Import Wizard transaction");
      }

      OperationBuilder builder = Operations.createBuilder("Artifact Import");
      builder.addOp(new SourceToRoughArtifactOperation(logger, extractor, sourceFile, collector));
      builder.addOp(
         new RoughToRealArtifactOperation(transaction, destinationArtifact, collector, resolver, false, extractor));
      builder.addOp(new ArtifactValidationCheckOperation(destinationArtifact.getDescendants(), stopOnError));
      if (executeTransaction) {
         builder.addOp(new CompleteArtifactImportOperation(transaction, destinationArtifact));
      }
      return builder.build();
   }

   private static void CheckAndThrow(Object... objects) {
      for (Object object : objects) {
         Assert.isNotNull(object);
      }
   }

   /**
    * Creates a full import process.
    * <ol>
    * <li>SourceToRoughArtifactOperation</li>
    * <li>FilterArtifactTypesByAttributeTypes</li> if runFilterByAttributes == true
    * <li>RoughToRealArtifactOperation</li>
    * <li>FetchAndAddDescendantsOperation</li>
    * <li>ArtifactValidationCheckOperation</li>
    * <li>CompleteArtifactImportOperation</li>
    * </ol>
    */
   public static IOperation createOperation(File sourceFile, Artifact destinationArtifact, OperationLogger logger, IArtifactExtractor extractor, IArtifactImportResolver resolver, RoughArtifactCollector collector, Collection<ArtifactTypeToken> selectionArtifactTypes, boolean stopOnError, boolean deleteUnMatched, boolean runFilterByAttributes) {
      OperationBuilder builder =
         Operations.createBuilder("Artifact Import - ArtifactAndRoughToRealOperation, RoughToRealOperation");
      builder.addOp(createArtifactsCompOperation(
         "Artifact Import - SourceToRoughArtifact, FilterArtifactTypesByAttributeTypes", sourceFile,
         destinationArtifact, logger, extractor, collector, selectionArtifactTypes, runFilterByAttributes));
      builder.addOp(createRoughToRealOperation(
         "Artifact Import - RoughToRealArtifactOperation, ArtifactValidationCheckOperation, CompleteArtifactImportOperation",
         destinationArtifact, resolver, stopOnError, collector, deleteUnMatched, null));
      return builder.build();
   }

   /**
    * @see ArtifactImportPage
    */
   public static IOperation createArtifactsCompOperation(String opDescription, File sourceFile, Artifact destinationArtifact, OperationLogger logger, IArtifactExtractor extractor, RoughArtifactCollector collector, Collection<ArtifactTypeToken> selectionArtifactTypes, boolean runFilterByAttributes) {
      OperationBuilder builder = Operations.createBuilder(opDescription);
      builder.addOp(new SourceToRoughArtifactOperation(logger, extractor, sourceFile, collector));
      if (runFilterByAttributes) {
         builder.addOp(new FilterArtifactTypesByAttributeTypes(destinationArtifact.getBranch(), collector,
            selectionArtifactTypes));
      }
      return builder.build();
   }

   /**
    * @see ArtifactImportWizard
    */
   public static IOperation createRoughToRealOperation(String opName, final Artifact destinationArtifact, IArtifactImportResolver resolver, boolean stopOnError, RoughArtifactCollector collector, boolean deleteUnmatchedArtifacts, IArtifactExtractor extractor) {
      SkynetTransaction transaction = TransactionManager.createTransaction(destinationArtifact.getBranch(),
         "Artifact Import Wizard transaction " + opName);

      OperationBuilder builder = Operations.createBuilder(opName);
      builder.addOp(new RoughToRealArtifactOperation(transaction, destinationArtifact, collector, resolver,
         deleteUnmatchedArtifacts, extractor));

      final List<Artifact> children = new ArrayList<>();
      builder.addOp(new FetchAndAddDescendantsOperation(children, destinationArtifact));
      builder.addOp(new ArtifactValidationCheckOperation(children, stopOnError));
      builder.addOp(new CompleteArtifactImportOperation(transaction, destinationArtifact));

      return builder.build();
   }

   private static class FetchAndAddDescendantsOperation extends AbstractOperation {

      private final List<Artifact> children;
      private final Artifact destination;

      /**
       * @param children list to add result of <code>destination.getDescendants()</code> to
       * @param destination
       */
      public FetchAndAddDescendantsOperation(List<Artifact> children, Artifact destination) {
         super("Fetch and Add Descendants", Activator.PLUGIN_ID);
         this.children = children;
         this.destination = destination;
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         try {
            children.addAll(this.destination.getDescendants());
         } catch (OseeCoreException ex) {
            throw new OseeCoreException(String.format("Unable to get artifact children: artifact:[%s] branch:[%s]",
               this.destination.getId(), this.destination.getBranch()), ex);
         }
      }
   }
}
