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
import org.eclipse.osee.framework.core.exception.OseeCoreException;

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
      return WorkItemUtil.get(workItem).getAttributeValues(attributeType);
   }

   @Override
   public boolean isOfType(IAtsWorkItem item, IArtifactType matchType) throws OseeCoreException {
      return WorkItemUtil.get(item).getArtifactType().inheritsFrom(matchType);
   }

}
