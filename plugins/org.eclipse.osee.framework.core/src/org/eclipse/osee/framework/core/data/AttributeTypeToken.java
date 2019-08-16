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

import org.eclipse.osee.framework.jdk.core.type.FullyNamed;
import org.eclipse.osee.framework.jdk.core.type.HasDescription;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdDescription;

/**
 * @author Ryan D. Brooks
 */
public interface AttributeTypeToken extends AttributeTypeId, FullyNamed, HasDescription, NamedId {
   AttributeTypeToken SENTINEL = valueOf(Id.SENTINEL, Named.SENTINEL);

   public static AttributeTypeToken valueOf(String id) {
      return valueOf(Long.valueOf(id), Named.SENTINEL);
   }

   public static AttributeTypeToken valueOf(int id, String name) {
      return valueOf(Long.valueOf(id), name, "");
   }

   public static AttributeTypeToken valueOf(Long id, String name) {
      return valueOf(id, name, "");
   }

   public static AttributeTypeToken valueOf(Long id, String name, String description) {
      final class AttributeTypeImpl extends NamedIdDescription implements AttributeTypeToken {
         public AttributeTypeImpl(Long txId, String name, String description) {
            super(txId, name, description);
         }
      }
      return new AttributeTypeImpl(id, name, description);
   }
}