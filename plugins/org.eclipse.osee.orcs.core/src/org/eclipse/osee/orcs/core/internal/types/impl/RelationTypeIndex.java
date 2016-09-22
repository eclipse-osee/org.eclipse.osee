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

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
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
      super();
      this.artifactTypeIndex = artifactTypeIndex;
   }

   public IArtifactType getArtifactType(IRelationType relation, RelationSide relationSide) {
      XRelationType type = getDslTypeByToken(relation);
      XArtifactType artifactType =
         relationSide == RelationSide.SIDE_A ? type.getSideAArtifactType() : type.getSideBArtifactType();
      return artifactTypeIndex.getTokenByDslType(artifactType);
   }

   public boolean isArtifactTypeAllowed(IRelationType relation, RelationSide relationSide, IArtifactType artifactType) {
      IArtifactType allowedType = getArtifactType(relation, relationSide);
      return artifactTypeIndex.inheritsFrom(artifactType, allowedType);
   }
}