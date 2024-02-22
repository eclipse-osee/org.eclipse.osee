/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.OrcsTokenService;

/**
 * @author Jaden W. Puckett
 */
public class ArtifactRelatedDirectArtifact {
   private final ArtifactReadable artRead;
   private final List<ArtifactRelatedDirectAttribute> attributes = new ArrayList<>();

   public ArtifactRelatedDirectArtifact(ArtifactReadable artRead, OrcsTokenService tokenService) {
      this.artRead = artRead;
      List<IAttribute<?>> attrs = artRead.getAttributesHashCollection().getValues();
      List<ArtifactRelatedDirectAttribute> pojoAttributes = attrs.stream().filter(attr -> attr != null).map(
         attr -> new ArtifactRelatedDirectAttribute(attr, artRead.getArtifactType(), tokenService)).collect(
            Collectors.toList());
      this.attributes.addAll(pojoAttributes);
   }

   public List<ArtifactRelatedDirectAttribute> getAttributes() {
      return this.attributes;
   }

   public String getId() {
      return this.artRead.getIdString();
   }

   public String getName() {
      return this.artRead.getName();
   }

   public String getTypeId() {
      return this.artRead.getArtifactType().getIdString();
   }

   public String getTypeName() {
      return this.artRead.getArtifactType().getName();
   }

   public ArtifactTypeIcon getIcon() {
      return this.artRead.getArtifactType().getIcon();
   }
}