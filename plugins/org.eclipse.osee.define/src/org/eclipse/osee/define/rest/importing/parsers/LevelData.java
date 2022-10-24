/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.define.rest.importing.parsers;

import org.eclipse.osee.define.api.importing.RoughArtifact;

/**
 * @author David W. Miller
 */
public class LevelData {
   private RoughArtifact parent;
   private Integer level;

   public LevelData(Integer level, RoughArtifact parent) {
      this.level = level;
      this.parent = parent;
   }

   public RoughArtifact getParent() {
      return parent;
   }

   public void setParent(RoughArtifact parent) {
      this.parent = parent;
   }

   public Integer getLevel() {
      return level;
   }

   public void setLevel(Integer level) {
      this.level = level;
   }
}
