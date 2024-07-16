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

package org.eclipse.osee.framework.skynet.core.revision;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.KindType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;

/**
 * Collection of changes from working branch or transactionId from committed branch.
 *
 * @author Donald G. Dunne
 */
public class ChangeData {

   private static final String IMPL_DETAILS = " (Impl Details)";
   private static final String DELETED = " (Deleted)";

   private final Collection<Change> changes;

   public ChangeData(Collection<Change> changes) {
      this.changes = changes;
   }

   public boolean isEmpty() {
      return getChanges() == null || getChanges().isEmpty();
   }

   public Collection<Change> getChanges() {
      return changes;
   }

   public Collection<Change> getArtifactChangesById(ArtifactId art) {
      Set<Change> foundChanges = new HashSet<>();
      try {
         for (Change change : changes) {
            if (change instanceof ArtifactChange) {
               if (change.getChangeArtifact().equals(art)) {
                  foundChanges.add(change);
               }
            }
         }
         return foundChanges;
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   public Collection<Change> getArtifactChangesByName(String name, String appendedInfo) {
      Collection<Change> foundChanges = new HashSet<>();
      try {
         for (Change change : changes) {
            if (change instanceof ArtifactChange) {
               if (isDeleted(change) && isAppendedDeleted(appendedInfo)) {
                  if (change.getArtifactName().equals(name)) {
                     foundChanges.add(change);
                  }
               } else if (isImplDetails(change) && isAppendedImplDetails(appendedInfo)) {
                  if (change.getArtifactName().equals(name)) {
                     foundChanges.add(change);
                  }
               } else if (Strings.isInValid(appendedInfo) && //
                  !isDeleted(change) && !isImplDetails(change) && //
                  change.getArtifactName().equals(name)) {
                  foundChanges.add(change);
               }
            }
         }
         return foundChanges;
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private boolean isImplDetails(Change change) {
      return change.getArtifactType().inheritsFrom(CoreArtifactTypes.AbstractImplementationDetails);
   }

   private boolean isDeleted(Change change) {
      return change.getModificationType().equals(ModificationType.DELETED);
   }

   private boolean isAppendedImplDetails(String otherDetails) {
      return Strings.isValid(otherDetails) && otherDetails.equals(IMPL_DETAILS);
   }

   private boolean isAppendedDeleted(String otherDetails) {
      return Strings.isValid(otherDetails) && otherDetails.equals(DELETED);
   }

   /**
    * Return artifacts of kind and modType.
    */
   public Collection<Artifact> getArtifacts(KindType kindType, ModificationType... modificationType) {
      return getArtifacts(kindType, null, modificationType);
   }

   /**
    * Return artifacts of kind and modType.
    */
   public Collection<Artifact> getArtifacts(KindType kindType, Collection<AttributeTypeId> artifactTypesToIgnore, ModificationType... modificationType) {
      if (kindType == KindType.RelationOnly) {
         return getArtifactsRelationOnly(modificationType);
      }

      Collection<ModificationType> modTypes = Arrays.asList(modificationType);

      Set<Artifact> artifacts = new HashSet<>();
      if (kindType == KindType.Artifact || kindType == KindType.ArtifactOrRelation || kindType == KindType.Relation) {
         if (!isEmpty()) {
            HashMap<ArtifactId, Boolean> excludeArtifacts = new HashMap<>();
            for (Change change : changes) {
               Artifact artifact = change.getChangeArtifact();

               ModificationType modType = change.getModificationType();
               if (artifactTypesToIgnore != null && !artifactTypesToIgnore.isEmpty()) {
                  try {
                     if (change instanceof AttributeChange) {
                        long typeId = change.getItemTypeId().getId();
                        AttributeTypeToken attributeType = AttributeTypeManager.getAttributeType(typeId);
                        if (excludeArtifacts.containsKey(artifact)) {
                           if (!artifactTypesToIgnore.contains(attributeType)) {
                              if (excludeArtifacts.get(artifact)) {
                                 excludeArtifacts.put(artifact, false);
                              }
                           }
                        } else {
                           excludeArtifacts.put(artifact, artifactTypesToIgnore.contains(attributeType));
                        }
                     }
                  } catch (OseeTypeDoesNotExist ex) {
                     /*******************
                      * This is pseudo type that just states the artifact was changed. It does not effect the paragraph
                      * number computation
                      */
                  }
               }
               /**
                * Only way to determine if artifact is of type Merged is to check it's attributes cause the Artifact is
                * of type Modified while attribute is of type merged. Only check attribute change for this case.
                */

               if (modTypes.isEmpty() || modTypes.contains(modType)) {
                  if ((kindType == KindType.Artifact || kindType == KindType.ArtifactOrRelation) && isAttributeChangeMergeType(
                     change)) {
                     artifacts.add(artifact);
                  } else if ((kindType == KindType.Artifact || kindType == KindType.ArtifactOrRelation) && change instanceof ArtifactChange) {
                     artifacts.add(artifact);
                  } else if ((kindType == KindType.Relation || kindType == KindType.ArtifactOrRelation) && change instanceof RelationChange) {
                     artifacts.add(artifact);
                     RelationChange relChange = (RelationChange) change;
                     artifacts.add(relChange.getEndTxBArtifact());
                  }
               }
            }
            if (artifactTypesToIgnore != null && !artifactTypesToIgnore.isEmpty()) {
               Set<Artifact> excludeList = new HashSet<>();
               for (Artifact artifactToCheck : artifacts) {
                  Boolean remove = excludeArtifacts.get(artifactToCheck);
                  if (remove != null) {
                     if (remove) {
                        excludeList.add(artifactToCheck);
                     }
                  }
               }
               artifacts.removeAll(excludeList);
            }
         }
      }
      return artifacts;
   }

   private boolean isAttributeChangeMergeType(Change change) {
      if (change instanceof AttributeChange && change.getModificationType() == ModificationType.MERGED) {
         return true;
      }
      return false;
   }

   private Collection<Artifact> getArtifactsRelationOnly(ModificationType... modificationType) {
      Collection<Artifact> artMod = getArtifacts(KindType.Artifact, modificationType);
      Collection<Artifact> relMod = getArtifacts(KindType.Relation, modificationType);
      return Collections.setComplement(relMod, artMod);
   }

   @Override
   public String toString() {
      try {
         StringBuilder sb = new StringBuilder();
         for (KindType kindType : KindType.values()) {
            Collection<Artifact> artifacts = getArtifacts(kindType);
            sb.append(String.format("Kind: %s Num: %s\n", kindType, artifacts.size()));
         }
         return sb.toString();
      } catch (OseeCoreException ex) {
         return ex.toString();
      }
   }
}
