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
package org.eclipse.osee.framework.skynet.core.artifact.operation;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Ryan D. Brooks
 */
public abstract class ArtifactOperation extends Artifact implements WorkflowStep {
   public static final String PROVIDES_ACCEPTABLE_INPUT = "Provides Acceptable Input";
   public static final String ARTIFACT_NAME = "Artifact Operation";

   public ArtifactOperation(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) throws OseeDataStoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   /**
    * The list of artifacts will never be null, but may be empty. Must never throw NullPointerException, but rather a
    * more informative excpetion explaining the problem that would have caused a NullPointerException.
    * 
    * @param artifacts
    * @param monitor progress monitor for upating the GUI
    * @return may return null because the engine will use an empty list in this case
    * @throws IllegalArgumentException
    * @throws Exception
    */
   public abstract List<Artifact> applyToArtifacts(List<Artifact> artifacts, IProgressMonitor monitor) throws IllegalArgumentException, Exception;

   /**
    * @param operation
    * @return returns PROVIDES_ACCEPTABLE_INPUT if this operation can run after, i.e. accept input from, the given
    *         operation, otherwise returns a human readable reason why the input can not be accepted
    */
   public String canAcceptInputFrom(ArtifactOperation operation) {
      return PROVIDES_ACCEPTABLE_INPUT;
   }

   public List<Artifact> perform(List<Artifact> artifacts, IProgressMonitor monitor) throws IllegalArgumentException, Exception {
      return applyToArtifacts(artifacts, monitor);
   }
}