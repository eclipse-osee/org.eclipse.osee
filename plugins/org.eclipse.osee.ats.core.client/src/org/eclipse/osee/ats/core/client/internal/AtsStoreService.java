/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;

/**
 * @author Donald G. Dunne
 */
public class AtsStoreService implements IAtsStoreService {
   private final IAtsWorkItemFactory workItemFactory;

   public AtsStoreService(IAtsWorkItemFactory workItemFactory) {
      this.workItemFactory = workItemFactory;
   }

   @Override
   public IAtsChangeSet createAtsChangeSet(String comment, IAtsUser asUser) {
      return new AtsChangeSet(comment, asUser);
   }

   @Override
   public List<IAtsWorkItem> reload(Collection<IAtsWorkItem> workItems) {
      List<IAtsWorkItem> results = new ArrayList<>();
      try {
         List<Artifact> artifacts = new LinkedList<Artifact>();
         for (IAtsWorkItem workItem : workItems) {
            if (workItem.getStoreObject() != null && workItem.getStoreObject() instanceof Artifact) {
               artifacts.add((Artifact) workItem.getStoreObject());
            }
         }
         for (Artifact art : ArtifactQuery.reloadArtifacts(artifacts)) {
            if (!art.isDeleted()) {
               IAtsWorkItem workItem = workItemFactory.getWorkItem(art);
               if (workItem != null) {
                  results.add(workItem);
               }
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
      return results;
   }

   @Override
   public boolean isDeleted(IAtsObject atsObject) {
      return ((Artifact) atsObject.getStoreObject()).isDeleted();
   }

   @Override
   public String getGuid(IAtsObject atsObject) {
      return ((Artifact) atsObject.getStoreObject()).getGuid();
   }

   /**
    * Uses artifact type inheritance to retrieve all TeamWorkflow artifact types
    */
   @Override
   public Set<IArtifactType> getTeamWorkflowArtifactTypes() throws OseeCoreException {
      Set<IArtifactType> artifactTypes = new HashSet<>();
      getTeamWorkflowArtifactTypesRecursive(ArtifactTypeManager.getType(AtsArtifactTypes.TeamWorkflow), artifactTypes);
      return artifactTypes;
   }

   private static void getTeamWorkflowArtifactTypesRecursive(ArtifactType artifactType, Set<IArtifactType> allArtifactTypes) throws OseeCoreException {
      allArtifactTypes.add(artifactType);
      for (IArtifactType child : artifactType.getFirstLevelDescendantTypes()) {
         getTeamWorkflowArtifactTypesRecursive(ArtifactTypeManager.getType(child), allArtifactTypes);
      }
   }

   @Override
   public boolean isAttributeTypeValid(IAtsObject atsObject, IAttributeType attributeType) {
      return isAttributeTypeValid(atsObject.getStoreObject(), attributeType);
   }

   @Override
   public boolean isAttributeTypeValid(ArtifactId artifact, IAttributeType attributeType) {
      return ((Artifact) artifact).isAttributeTypeValid(attributeType);
   }

   @Override
   public IAttributeType getAttributeType(long attrTypeId) {
      return AttributeTypeManager.getTypeByGuid(attrTypeId);
   }

   @Override
   public IAttributeType getAttributeType(String attrTypeName) {
      return AttributeTypeManager.getType(attrTypeName);
   }

   @Override
   public IArtifactType getArtifactType(ArtifactId artifact) {
      return ((Artifact) artifact).getArtifactType();
   }

   @Override
   public boolean isDateType(IAttributeType attributeType) {
      return AttributeTypeManager.isBaseTypeCompatible(DateAttribute.class, attributeType);
   }

   @Override
   public boolean isOfType(ArtifactId artifact, IArtifactType... artifactType) {
      return ((Artifact) artifact).isOfType(artifactType);
   }

}
