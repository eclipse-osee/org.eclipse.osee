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

public class MergeData {
   private ArtifactId artId;
   private ArtifactTypeToken artType;
   private String name;
   private List<AttributeMergeData> attrs = new ArrayList<>();
   public ArtifactId getArtId() {
      return artId;
   }

   public void setArtId(ArtifactId artId) {
      this.artId = artId;
   }

   public ArtifactTypeToken getArtType() {
      return artType;
   }

   public void setArtType(ArtifactTypeToken artType) {
      this.artType = artType;
   }

   public List<AttributeMergeData> getAttrs() {
      return attrs;
   }

   public void setAttrs(List<AttributeMergeData> attrs) {
      this.attrs = attrs;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

}
