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
public class Applicability {

   @JsonSerialize(using = ToStringSerializer.class)
   private long artId;
   private ApplicabilityId applicability;

   public Applicability() {
      // For Jax-RS instantiation
   }

   public Applicability(long artId, ApplicabilityId applId) {
      this.artId = artId;
      this.applicability = applId;
   }

   public long getArtId() {
      return artId;
   }

   public void setArtId(long artId) {
      this.artId = artId;
   }

   public ApplicabilityId getApplicability() {
      return applicability;
   }

   public void setApplicability(ApplicabilityId applicability) {
      this.applicability = applicability;
   }

}
