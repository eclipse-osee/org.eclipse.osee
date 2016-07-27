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
package org.eclipse.osee.orcs.data;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface RelationTypes extends IdCollection<IRelationType> {

   RelationTypeMultiplicity getMultiplicity(IRelationType relation) throws OseeCoreException;

   IArtifactType getArtifactTypeSideA(IRelationType relation) throws OseeCoreException;

   IArtifactType getArtifactTypeSideB(IRelationType relation) throws OseeCoreException;

   IArtifactType getArtifactType(IRelationType relation, RelationSide relationSide) throws OseeCoreException;

   String getSideName(IRelationType relation, RelationSide relationSide) throws OseeCoreException;

   boolean isArtifactTypeAllowed(IRelationType relation, RelationSide relationSide, IArtifactType artifactType) throws OseeCoreException;

   String getSideAName(IRelationType relation) throws OseeCoreException;

   String getSideBName(IRelationType relation) throws OseeCoreException;

   boolean isSideAName(IRelationType relation, String sideName) throws OseeArgumentException, OseeCoreException;

   boolean isOrdered(IRelationType relation) throws OseeCoreException;

   String getDefaultOrderTypeGuid(IRelationType relation) throws OseeCoreException;
}