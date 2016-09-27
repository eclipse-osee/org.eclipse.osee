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
package org.eclipse.osee.ats.api.util;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;

public interface IAtsStoreService {

   IAtsChangeSet createAtsChangeSet(String comment, IAtsUser user);

   List<IAtsWorkItem> reload(Collection<IAtsWorkItem> workItems);

   boolean isDeleted(IAtsObject atsObject);

   String getTypeName(ArtifactId artifact);

   String getGuid(IAtsObject atsObject);

   boolean isAttributeTypeValid(IAtsObject atsObject, IAttributeType attributeType);

   boolean isAttributeTypeValid(ArtifactId artifact, IAttributeType attributeType);

   /**
    * Uses artifact type inheritance to retrieve all TeamWorkflow artifact types
    */
   Set<IArtifactType> getTeamWorkflowArtifactTypes();

   IAttributeType getAttributeType(long attrTypeId);

   IAttributeType getAttributeType(String attrTypeName);

   IArtifactType getArtifactType(ArtifactId artifact);

   boolean isDateType(IAttributeType attributeType);

   boolean isOfType(ArtifactId artifact, IArtifactType artifactType);

}
