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

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;

/**
 * @author Ryan D. Brooks
 */
@JsonSerialize(using = IdSerializer.class)
public interface RelationId extends Id {
   RelationId SENTINEL = valueOf(Id.SENTINEL);

   public static RelationId valueOf(String id) {
      return valueOf(Long.valueOf(id));
   }

   public static RelationId valueOf(long id) {
      final class RelationIdImpl extends BaseId implements RelationId {
         public RelationIdImpl(Long txId) {
            super(txId);
         }
      }
      return new RelationIdImpl(id);
   }
}