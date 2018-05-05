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
package org.eclipse.osee.orcs.core.ds;

import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsTypesDataStore {

   IResource getOrcsTypesLoader(OrcsSession session);

   Callable<Void> purgeArtifactsByArtifactType(OrcsSession session, Collection<? extends IArtifactType> artifactTypes);

   Callable<Void> purgeAttributesByAttributeType(OrcsSession session, Collection<? extends AttributeTypeId> attributeTypes);

   Callable<Void> purgeRelationsByRelationType(OrcsSession session, Collection<? extends IRelationType> relationTypes);

}
