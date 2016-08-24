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

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;
import org.eclipse.osee.framework.jdk.core.type.Identity;

/**
 * @author Ryan D. Brooks
 */
@JsonSerialize(using = IdSerializer.class)
public interface BranchId extends Identity<Long>, Id {
   BranchId SENTINEL = valueOf(Id.SENTINEL);

   default Long getUuid() {
      return getId();
   }

   @Override
   default Long getGuid() {
      return getId();
   };

   public static BranchId valueOf(String id) {
      return valueOf(Long.valueOf(id));
   }

   @JsonCreator
   public static BranchId valueOf(long id) {
      return TokenFactory.createBranch(id);
   }
}