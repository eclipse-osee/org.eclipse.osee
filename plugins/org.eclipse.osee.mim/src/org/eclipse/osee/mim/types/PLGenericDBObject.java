/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.mim.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * Generic class object to make common operations (i.e. generic POST/PUT) operations easier
 *
 * @author Luciano T. Vaglienti
 */
public class PLGenericDBObject extends NamedIdBase {
   public static final PLGenericDBObject SENTINEL = new PLGenericDBObject();

   private ArtifactReadable artifactReadable = ArtifactReadable.SENTINEL;

   public PLGenericDBObject(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public PLGenericDBObject(ArtifactReadable art) {
      this(art.getId(), art.getName());
      this.artifactReadable = art;
   }

   public PLGenericDBObject(Long id, String name) {
      this();
      this.setId(id);
      this.setName(name);
   }

   public PLGenericDBObject() {
      super(ArtifactId.SENTINEL.getId(), "");
      // Not doing anything
   }

   @JsonIgnore
   public ArtifactReadable getArtifactReadable() {
      return artifactReadable;
   }

   @JsonIgnore
   public ArtifactId getArtifactId() {
      return ArtifactId.valueOf(super.getId());
   }

   @Override
   @JsonIgnore
   public String getIdString() {
      return super.getIdString();
   }

   @Override
   @JsonIgnore
   public int getIdIntValue() {
      return super.getIdIntValue();
   }

}