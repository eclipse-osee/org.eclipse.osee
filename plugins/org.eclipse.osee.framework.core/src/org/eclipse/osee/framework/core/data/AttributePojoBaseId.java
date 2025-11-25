/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0/
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * A base class for AttributePojo with an ID.
 *
 * @param <T> the type of the value held by the AttributePojo
 * @author Jaden W. Puckett
 */
public class AttributePojoBaseId<T> extends BaseId {
   private AttributePojo<T> attributePojo;

   public static <T> AttributePojoBaseId<T> valueOf(Long id, AttributePojo<T> attributePojo) {
      return new AttributePojoBaseId<>(id, attributePojo);
   }

   public AttributePojoBaseId(Long id, AttributePojo<T> attributePojo) {
      super(id);
      this.attributePojo = attributePojo;
   }

   public AttributePojoBaseId(int id, AttributePojo<T> attributePojo) {
      super(Long.valueOf(id));
      this.attributePojo = attributePojo;
   }

   public AttributePojoBaseId() {
      super(Id.SENTINEL);
      this.attributePojo = new AttributePojo<>();
   }

   public AttributePojo<T> getAttributePojo() {
      return attributePojo;
   }

   public void setAttributePojo(AttributePojo<T> attributePojo) {
      this.attributePojo = attributePojo;
   }

   @Override
   public String toString() {
      return attributePojo == null ? super.toString() : attributePojo.toString();
   }

   public static <T extends BaseId> T valueOf(Long id, T[] tokens) {
      for (T token : tokens) {
         if (token.getId().equals(id)) {
            return token;
         }
      }
      throw new OseeArgumentException("Value with id [%s] does not exist", id);
   }
}