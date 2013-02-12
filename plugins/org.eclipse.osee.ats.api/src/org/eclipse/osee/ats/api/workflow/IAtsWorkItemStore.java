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
package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItemStore {

   IAtsWorkData getWorkData(IAtsWorkItem workItem) throws OseeCoreException;

   IArtifactType getArtifactType(IAtsWorkItem workItem) throws OseeCoreException;

   Collection<Object> getAttributeValues(IAtsObject workItem, IAttributeType attributeType) throws OseeCoreException;

   boolean isOfType(IAtsWorkItem item, IArtifactType matchType) throws OseeCoreException;

   IAtsWorkItem getParentTeamWorkflow(IAtsWorkItem workItem) throws OseeCoreException;

}
