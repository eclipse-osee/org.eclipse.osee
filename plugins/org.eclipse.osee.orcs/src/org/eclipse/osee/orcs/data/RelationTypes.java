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
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
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

   ArtifactTypeId getArtifactTypeSideA(RelationTypeToken relation);

   ArtifactTypeId getArtifactTypeSideB(RelationTypeToken relation);

   ArtifactTypeId getArtifactType(RelationTypeId relation, RelationSide relationSide);

   String getSideName(RelationTypeToken relation, RelationSide relationSide);

   boolean isArtifactTypeAllowed(RelationTypeId relation, RelationSide relationSide, ArtifactTypeToken artifactType);

   String getSideAName(RelationTypeToken relation);

   String getSideBName(RelationTypeToken relation);

   boolean isSideAName(RelationTypeToken relation, String sideName);

   boolean isOrdered(RelationTypeToken relation);

   RelationSorter getDefaultOrderTypeGuid(RelationTypeToken relation);
}