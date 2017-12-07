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

import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;

/**
 * @author Roberto E. Escobar
 */
public interface RelationTypes extends IdCollection<RelationTypeToken> {

   RelationTypeMultiplicity getMultiplicity(RelationTypeId relation);

   ArtifactTypeId getArtifactTypeSideA(IRelationType relation);

   ArtifactTypeId getArtifactTypeSideB(IRelationType relation);

   ArtifactTypeId getArtifactType(RelationTypeId relation, RelationSide relationSide);

   String getSideName(IRelationType relation, RelationSide relationSide);

   boolean isArtifactTypeAllowed(RelationTypeId relation, RelationSide relationSide, ArtifactTypeId artifactType);

   String getSideAName(IRelationType relation);

   String getSideBName(IRelationType relation);

   boolean isSideAName(IRelationType relation, String sideName);

   boolean isOrdered(IRelationType relation);

   RelationSorter getDefaultOrderTypeGuid(IRelationType relation);
}