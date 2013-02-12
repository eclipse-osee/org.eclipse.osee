/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsWorkData;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemStore;
import org.eclipse.osee.ats.core.client.util.WorkItemUtil;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemStore implements IAtsWorkItemStore {

   @Override
   public IAtsWorkData getWorkData(IAtsWorkItem workItem) throws OseeCoreException {
      return new AtsWorkData((AbstractWorkflowArtifact) WorkItemUtil.get(workItem));
   }

   @Override
   public IArtifactType getArtifactType(IAtsWorkItem workItem) throws OseeCoreException {
      return WorkItemUtil.get(workItem).getArtifactType();
   }

   @Override
   public Collection<Object> getAttributeValues(IAtsObject workItem, IAttributeType attributeType) throws OseeCoreException {
      Artifact artifact = WorkItemUtil.get(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", workItem.toString());

      IAttributeType attrType = AttributeTypeManager.getType(attributeType);
      if (attrType == null) {
         throw new OseeArgumentException(String.format("Can't resolve Attribute Type [%s]", attributeType));
      }

      return artifact.getAttributeValues(attributeType);
   }

   @Override
   public boolean isOfType(IAtsWorkItem workItem, IArtifactType matchType) throws OseeCoreException {
      Artifact artifact = WorkItemUtil.get(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", workItem.toString());
      return artifact.isOfType(matchType);
   }

   @Override
   public IAtsWorkItem getParentTeamWorkflow(IAtsWorkItem workItem) throws OseeCoreException {
      Artifact artifact = WorkItemUtil.get(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", workItem.toString());
      if (artifact instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
         return awa.getParentTeamWorkflow();
      }
      return null;
   }

}
