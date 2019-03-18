/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author Donald G. Dunne
 */
public class OrcsTypesSheet {

   @JsonSerialize(using = ToStringSerializer.class)
   private long artifactId;
   @JsonSerialize(using = ToStringSerializer.class)
   private long attrId;
   private String name;

   public long getArtifactId() {
      return artifactId;
   }

   public void setArtifactId(long artifactId) {
      this.artifactId = artifactId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public long getAttrId() {
      return attrId;
   }

   public void setAttrId(long attrId) {
      this.attrId = attrId;
   }

   @Override
   public String toString() {
      return "OrcsTypesSheet [artId=" + artifactId + ", attrId=" + attrId + ", name=" + name + "]";
   }

}
