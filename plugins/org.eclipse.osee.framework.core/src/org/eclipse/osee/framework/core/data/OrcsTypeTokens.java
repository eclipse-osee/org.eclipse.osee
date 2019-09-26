/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.annotation.NonNull;

/**
 * OSEE type token providers should instantiate a static instance of this class and call the add methods for each type
 * token they create.
 *
 * @author Ryan D. Brooks
 */
public class OrcsTypeTokens {
   private final List<ArtifactTypeToken> artifactTypes = new ArrayList<>();
   private final List<AttributeTypeToken> attributeTypes = new ArrayList<>();
   private final List<RelationTypeToken> relationTypes = new ArrayList<>();

   public ArtifactTypeToken add(ArtifactTypeToken artifactType) {
      artifactTypes.add(artifactType);
      return artifactType;
   }

   public <V, @NonNull T extends AbstractAttributeType<V>> T add(T attributeType) {
      attributeTypes.add(attributeType);
      return attributeType;
   }

   public RelationTypeToken add(RelationTypeToken relationType) {
      relationTypes.add(relationType);
      return relationType;
   }

   public void registerTypes(OrcsTokenService tokenService) {
      artifactTypes.forEach(tokenService::registerArtifactType);
      attributeTypes.forEach(tokenService::registerAttributeType);
      relationTypes.forEach(tokenService::registerRelationType);
   }
}