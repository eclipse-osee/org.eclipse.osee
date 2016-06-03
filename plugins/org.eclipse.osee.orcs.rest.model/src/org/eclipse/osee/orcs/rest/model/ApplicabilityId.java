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
package org.eclipse.osee.orcs.rest.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class ApplicabilityId {

   @JsonSerialize(using = ToStringSerializer.class)
   private long applId;
   private String name = "";

   public ApplicabilityId() {
      // For JAX-RS insantiation
   }

   public ApplicabilityId(long applId, String name) {
      this.applId = applId;
      this.name = name;
   }

   public long getApplId() {
      return applId;
   }

   public void setApplId(long applId) {
      this.applId = applId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return name;
   }

}
