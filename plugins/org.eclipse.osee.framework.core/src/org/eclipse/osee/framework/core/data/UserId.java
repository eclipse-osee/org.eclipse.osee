/*******************************************************************************
 * Copyright (c) 2017 Boeing.
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
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;

/**
 * @author Ryan D. Brooks
 */
@JsonSerialize(using = IdSerializer.class)
public interface UserId extends ArtifactId {
   UserId SENTINEL = valueOf(Id.SENTINEL);

   public static UserId valueOf(String id) {
      if (id == null) {
         return SystemUser.Anonymous;
      }
      return valueOf(Long.valueOf(id));
   }

   public static UserId valueOf(Long id) {
      final class UserIdImpl extends BaseId implements UserId {
         public UserIdImpl(Long id) {
            super(id);
         }

         @Override
         public String getGuid() {
            return null;
         }
      }
      return new UserIdImpl(id);
   }
}