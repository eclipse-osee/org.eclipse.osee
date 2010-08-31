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
package org.eclipse.osee.ote.define.artifacts;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.ote.define.OteDefinePlugin;

/**
 * @author Roberto E. Escobar
 */
public class OteArtifactFetcher<T extends Artifact> {
   private final IArtifactType oteArtifactType;

   protected OteArtifactFetcher(IArtifactType oteArtifactType) {
      this.oteArtifactType = oteArtifactType;
   }

   /**
    * Creates a new Artifact in the specified branch
    */
   @SuppressWarnings("unchecked")
   public T getNewArtifact(Branch branch) throws OseeCoreException {
      checkForNull(branch);
      return (T) ArtifactTypeManager.addArtifact(oteArtifactType, branch);
   }

   /**
    * Retrieves a unique artifact matching input parameter
    * 
    * @param typeName Attribute type name
    * @param value attribute value to match
    * @param branch to search in
    * @return the unique artifact
    */
   @SuppressWarnings("unchecked")
   public T searchForUniqueArtifactMatching(IAttributeType attributeType, String attributeValue, Branch branch) throws OseeCoreException {
      Conditions.checkNotNull(attributeType, "attributeType");
      Conditions.checkNotNull(attributeValue, "attributeValue");
      Conditions.checkNotNull(branch, "branch");
      return (T) ArtifactQuery.getArtifactFromTypeAndAttribute(oteArtifactType, attributeType, attributeValue, branch);
   }

   /**
    * Returns all artifact instances found in branch matching the type <b>T</b>
    * 
    * @param branch to search in
    * @return artifact instances
    */
   @SuppressWarnings("unchecked")
   public Set<T> getAllArtifacts(Branch branch) throws OseeArgumentException {
      checkForNull(branch);
      Set<T> toReturn = new HashSet<T>();
      try {
         Collection<Artifact> artifacts = ArtifactQuery.getArtifactListFromType(oteArtifactType, branch);
         for (Artifact artifact : artifacts) {
            toReturn.add((T) artifact);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(OteDefinePlugin.class, Level.WARNING,
            String.format("Search for all artifacts failed [%s, %s]", oteArtifactType.getName(), branch.getName()), ex);
      }
      return toReturn;
   }

   /**
    * Returns all artifact instances found in branch matching the type <b>T</b> Results are indexed by artifact
    * descriptive name.
    * 
    * @param branch to search in
    * @return artifact instances indexed by descriptive name
    */
   public Map<String, T> getAllArtifactsIndexedByName(Branch branch) throws OseeArgumentException {
      Map<String, T> toReturn = new HashMap<String, T>();
      Set<T> testScripts = getAllArtifacts(branch);
      for (T artifact : testScripts) {
         toReturn.put(artifact.getName(), artifact);
      }
      return toReturn;
   }

   private void checkForNull(Object object) throws OseeArgumentException {
      if (object == null) {
         throw new OseeArgumentException("Object was null");
      }
   }
}
