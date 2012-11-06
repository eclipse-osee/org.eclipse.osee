/*
 * Created on Oct 8, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.workflow;

import java.util.Collection;
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
   public Collection<Object> getAttributeValues(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException {
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

}
