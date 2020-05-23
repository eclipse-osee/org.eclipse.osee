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

package org.eclipse.osee.framework.skynet.core.utility;

import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author David W. Miller
 */
public class JsonAttributeRepresentation {
   private long attributeTypeId;
   private String value;

   public JsonAttributeRepresentation() {
      attributeTypeId = Id.SENTINEL;
      value = null;
   }

   public JsonAttributeRepresentation(long attributeTypeId, String value) {
      this.attributeTypeId = attributeTypeId;
      this.value = value;
   }

   public long getAttributeTypeId() {
      return attributeTypeId;
   }

   public void setAttributeTypeId(long attributeTypeId) {
      this.attributeTypeId = attributeTypeId;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }
}
