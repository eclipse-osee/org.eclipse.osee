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
package org.eclipse.osee.orcs.core.internal.types;

import java.io.OutputStream;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.RelationTypes;
import org.eclipse.osee.orcs.utility.ObjectProvider;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsTypes {

   //TODO: Move this interface to org.eclipse.osee.orcs api

   ArtifactTypes getArtifactTypes();

   AttributeTypes getAttributeTypes();

   RelationTypes getRelationTypes();

   Callable<?> loadTypes(IResource resource, boolean isInitializing);

   Callable<?> writeTypes(ObjectProvider<? extends OutputStream> supplier);

   Callable<?> purgeArtifactsByArtifactType(Collection<? extends IArtifactType> artifactTypes);

   Callable<?> purgeAttributesByAttributeType(Collection<? extends IAttributeType> attributeTypes);

   Callable<?> purgeRelationsByRelationType(Collection<? extends IRelationType> relationTypes);

   void invalidateAll();
}
