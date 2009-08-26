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
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.CompleteArtifactImportOperation;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughToRealArtifactOperation;
import org.eclipse.osee.framework.skynet.core.importing.operations.SourceToRoughArtifactOperation;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.ArtifactValidationCheckOperation;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class ArtifactImportOperationFactory {

   private ArtifactImportOperationFactory() {
   }

   public static IOperation createOperation(File sourceFile, Artifact destinationArtifact, IArtifactExtractor extractor, IArtifactImportResolver resolver, boolean stopOnError) throws OseeCoreException {
      RoughArtifactCollector collector = new RoughArtifactCollector(new RoughArtifact(RoughArtifactKind.PRIMARY));
      SkynetTransaction transaction = new SkynetTransaction(destinationArtifact.getBranch());

      List<IOperation> ops = new ArrayList<IOperation>();
      ops.add(new SourceToRoughArtifactOperation(extractor, sourceFile, collector));
      ops.add(new RoughToRealArtifactOperation(transaction, destinationArtifact, collector, resolver));
      ops.add(new ArtifactValidationCheckOperation(destinationArtifact.getDescendants(), stopOnError));
      ops.add(new CompleteArtifactImportOperation(transaction, destinationArtifact));
      return new CompositeOperation("Artifact Import", SkynetGuiPlugin.PLUGIN_ID, ops);
   }
}