/*******************************************************************************
 * Copyright (c) 2011 Boeing.
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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;

/**
 * Facilitates importing of artifacts with {@link ArtifactImportWizard }. <br/>
 * Refer to {@link ArtifactImporter.initialized()} to determine if importer has been properly setup.
 *
 * @author Karol M Wilk
 */
public final class ArtifactImporter {

   private File importResource;
   private Artifact defaultDestinationArtifact;

   public ArtifactImporter() {
      this(null, null);
   }

   public ArtifactImporter(File importResource, Artifact defaultDestinationArtifact) {
      this.importResource = importResource;
      this.defaultDestinationArtifact = defaultDestinationArtifact;
   }

   public boolean initialized() {
      return Conditions.notNull(importResource, defaultDestinationArtifact);
   }

   public void setInputResource(Object firstElement) {
      if (firstElement instanceof IAdaptable) {
         Object resource = ((IAdaptable) firstElement).getAdapter(IResource.class);
         if (resource instanceof IResource) {
            importResource = ((IResource) resource).getLocation().toFile();
         }
      }
      if (firstElement instanceof Artifact) {
         defaultDestinationArtifact = (Artifact) firstElement;
      }
   }

   public Artifact getDestinationArtifact() {
      return defaultDestinationArtifact;
   }

   public File getFile() {
      return importResource;
   }

   public boolean startImportJob(Artifact finalDestinationArtifact, boolean isDeleteUnmatchedSelected, RoughArtifactCollector roughItems, IArtifactImportResolver resolver) {
      boolean initialized = initialized();
      if (initialized) {
         defaultDestinationArtifact = finalDestinationArtifact;

         final String opName = String.format("Importing Artifacts onto: [%s]", defaultDestinationArtifact);
         IOperation operation =
            ArtifactImportOperationFactory.createRoughToRealOperation(opName, defaultDestinationArtifact, resolver,
               false, roughItems, isDeleteUnmatchedSelected);
         Operations.executeAsJob(operation, true);
      }
      return initialized;
   }
}
