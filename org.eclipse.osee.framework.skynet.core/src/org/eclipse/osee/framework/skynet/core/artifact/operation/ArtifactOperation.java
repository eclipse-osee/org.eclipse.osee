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

import java.sql.SQLException;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;

/**
 * @author Ryan D. Brooks
 */
public abstract class ArtifactOperation extends Artifact implements WorkflowStep {
   public static final String PROVIDES_ACCEPTABLE_INPUT = "Provides Acceptable Input";
   public static final String ARTIFACT_NAME = "Artifact Operation";

   /**
    * @param parentFactory
    * @param guid
    * @param tagId
    * @throws SQLException
    */
   public ArtifactOperation(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch tagId) throws SQLException {
      super(parentFactory, guid, humanReadableId, tagId);
   }

   /**
    * The list of artifacts will never be null, but may be empty. Must never throw NullPointerException, but rather a
    * more informative excpetion explaining the problem that would have caused a NullPointerException.
    * 
    * @param artifacts
    * @param monitor progress monitor for upating the GUI
    * @return may return null because the engine will use an empty list in this case
    * @throws IllegalArgumentException
    * @throws Exception TODO
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.operation.WorkflowStep#perform(java.util.List)
    */
   public List<Artifact> perform(List<Artifact> artifacts, IProgressMonitor monitor) throws IllegalArgumentException, Exception {
      return applyToArtifacts(artifacts, monitor);
   }
}