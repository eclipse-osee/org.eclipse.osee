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
