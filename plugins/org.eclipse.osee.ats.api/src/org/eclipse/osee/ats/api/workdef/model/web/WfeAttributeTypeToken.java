/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.api.workdef.model.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * Suppress the extra get methods and include the name which isn't included for AttributeTypeToken
 *
 * @author Donald G. Dunne
 */
public class WfeAttributeTypeToken extends NamedIdBase {

   public WfeAttributeTypeToken() {
      // for jax-rs
   }

   public WfeAttributeTypeToken(AttributeTypeToken attrType) {
      super(attrType.getId(), attrType.getName());
   }

   @JsonIgnore
   @Override
   public int getIdIntValue() {
      return super.getIdIntValue();
   }

   @JsonIgnore
   @Override
   public String getIdString() {
      return super.getIdString();
   }

}
