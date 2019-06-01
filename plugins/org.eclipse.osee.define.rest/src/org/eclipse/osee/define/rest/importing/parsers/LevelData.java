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
package org.eclipse.osee.define.rest.importing.parsers;

import org.eclipse.define.api.importing.RoughArtifact;

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
