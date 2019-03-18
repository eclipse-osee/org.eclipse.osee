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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.NamedIdSerializer;

/**
 * @author Angel Avila
 */
@JsonSerialize(using = NamedIdSerializer.class)
public class ApplicabilityToken extends NamedIdBase implements ApplicabilityId {
   public static final ApplicabilityToken BASE = new ApplicabilityToken(ApplicabilityId.BASE.getId(), "Base");

   public ApplicabilityToken(long applId, String name) {
      super(applId, name);
   }

   public ApplicabilityToken(Long applId, String name) {
      super(applId, name);
   }

   public static ApplicabilityToken create(@JsonProperty("id") long id, @JsonProperty("name") String name) {
      return new ApplicabilityToken(id, name);
   }
}
