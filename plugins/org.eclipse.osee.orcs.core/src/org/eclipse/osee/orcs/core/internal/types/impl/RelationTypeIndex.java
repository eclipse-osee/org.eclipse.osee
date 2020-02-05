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
package org.eclipse.osee.orcs.core.internal.types.impl;

import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeIndex extends TokenTypeIndex<RelationTypeToken, XRelationType> {

   private final ArtifactTypeIndex artifactTypeIndex;

   public RelationTypeIndex(ArtifactTypeIndex artifactTypeIndex) {
      super(RelationTypeToken.SENTINEL);
      this.artifactTypeIndex = artifactTypeIndex;
   }

   public ArtifactTypeId getArtifactType(RelationTypeId relation, RelationSide relationSide) {
      XRelationType type = getDslTypeByToken(relation);
      XArtifactType artifactType =
         relationSide == RelationSide.SIDE_A ? type.getSideAArtifactType() : type.getSideBArtifactType();
      return artifactTypeIndex.getTokenByDslType(artifactType);
   }

   public boolean isArtifactTypeAllowed(RelationTypeId relation, RelationSide relationSide, ArtifactTypeToken artifactType) {
      ArtifactTypeId allowedType = getArtifactType(relation, relationSide);
      return artifactType.inheritsFrom(allowedType);
   }
}