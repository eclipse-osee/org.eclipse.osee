/*********************************************************************
 * Copyright (c) 2017 Boeing
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

import java.util.List;

/**
 * @author David W. Miller
 */

public class JsonArtifact {
   private String name;
   private ArtifactId id;
   private String typeName;
   private ArtifactTypeToken artifactType;
   private boolean hasChildren;

   private List<JsonAttribute> attrs;

   public void setHasChildren(boolean hasChildren) {
      this.hasChildren = hasChildren;
   }

   public boolean getHasChildren() {
      return this.hasChildren;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<JsonAttribute> getAttrs() {
      return attrs;
   }

   public void setAttrs(List<JsonAttribute> attrs) {
      this.attrs = attrs;
   }

   public ArtifactId getId() {
      return id;
   }

   public void setId(ArtifactId id) {
      this.id = id;
   }

   public String getTypeName() {
      return typeName;
   }

   public void setTypeName(String typeName) {
      this.typeName = typeName;
   }

   public ArtifactTypeToken getType() {
      return artifactType;
   }

   public void setType(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }

}
