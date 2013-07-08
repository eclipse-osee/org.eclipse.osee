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
package org.eclipse.osee.orcs;

import java.io.OutputStream;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.EnumTypes;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsTypes {

   ArtifactTypes getArtifactTypes();

   AttributeTypes getAttributeTypes();

   RelationTypes getRelationTypes();

   EnumTypes getEnumTypes();

   Callable<Void> loadTypes(IResource resource, boolean isInitializing);

   Callable<Void> writeTypes(OutputStream outputStream);

   Callable<Void> purgeArtifactsByArtifactType(Collection<? extends IArtifactType> artifactTypes);

   Callable<Void> purgeAttributesByAttributeType(Collection<? extends IAttributeType> attributeTypes);

   Callable<Void> purgeRelationsByRelationType(Collection<? extends IRelationType> relationTypes);

   void invalidateAll();

   Callable<Void> save();

}
