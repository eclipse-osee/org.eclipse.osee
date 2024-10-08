/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.define.artifacts;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.ote.define.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class OteArtifactFetcher<T extends Artifact> {
   private final ArtifactTypeToken oteArtifactType;

   protected OteArtifactFetcher(ArtifactTypeToken oteArtifactType) {
      this.oteArtifactType = oteArtifactType;
   }

   /**
    * Creates a new Artifact in the specified branch
    */
   @SuppressWarnings("unchecked")
   public T getNewArtifact(BranchToken branch) {
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
   public T searchForUniqueArtifactMatching(AttributeTypeId attributeType, String attributeValue, BranchId branch) {
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
   public Set<T> getAllArtifacts(BranchId branch) {
      checkForNull(branch);
      Set<T> toReturn = new HashSet<>();
      try {
         Collection<Artifact> artifacts = ArtifactQuery.getArtifactListFromType(oteArtifactType, branch);
         for (Artifact artifact : artifacts) {
            toReturn.add((T) artifact);
         }
      } catch (OseeCoreException ex) {
         OseeLog.logf(Activator.class, Level.WARNING, ex, "Search for all artifacts failed [%s] on branch [%s]",
            oteArtifactType.getName(), branch.getId());
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
   public Map<String, T> getAllArtifactsIndexedByName(BranchId branch) {
      Map<String, T> toReturn = new HashMap<>();
      Set<T> testScripts = getAllArtifacts(branch);
      for (T artifact : testScripts) {
         toReturn.put(artifact.getName(), artifact);
      }
      return toReturn;
   }

   private void checkForNull(Object object) {
      if (object == null) {
         throw new OseeArgumentException("Object was null");
      }
   }
}
