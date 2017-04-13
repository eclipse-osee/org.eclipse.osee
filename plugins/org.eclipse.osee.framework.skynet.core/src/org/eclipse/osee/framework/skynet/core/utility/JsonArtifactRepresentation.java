/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.List;

/**
 * @author David W. Miller
 */
public class JsonArtifactRepresentation {
   private long artifactTypeId;
   private String name;
   private List<JsonAttributeRepresentation> attrs;

   public long getArtifactTypeId() {
      return artifactTypeId;
   }

   public void setArtifactTypeId(long artifactTypeId) {
      this.artifactTypeId = artifactTypeId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<JsonAttributeRepresentation> getAttrs() {
      return attrs;
   }

   public void setAttrs(List<JsonAttributeRepresentation> attrs) {
      this.attrs = attrs;
   }
}
