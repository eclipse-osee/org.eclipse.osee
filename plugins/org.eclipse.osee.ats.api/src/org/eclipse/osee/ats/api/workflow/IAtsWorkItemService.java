/*
 * Created on Oct 8, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IAtsWorkItemService {

   public abstract IAtsWorkData getWorkData(IAtsWorkItem workItem) throws OseeCoreException;

   public abstract IArtifactType getArtifactType(IAtsWorkItem workItem) throws OseeCoreException;

   public abstract Collection<Object> getAttributeValues(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException;

   public abstract boolean isOfType(IAtsWorkItem item, IArtifactType matchType) throws OseeCoreException;
}
