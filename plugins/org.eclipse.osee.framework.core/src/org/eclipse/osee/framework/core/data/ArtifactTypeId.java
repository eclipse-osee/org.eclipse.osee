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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;

/**
 * @author Ryan D. Brooks
 */
@JsonSerialize(using = IdSerializer.class)
public interface ArtifactTypeId extends Id {
   ArtifactTypeId SENTINEL = valueOf(Id.SENTINEL);

   public static ArtifactTypeId valueOf(String id) {
      return Id.valueOf(id, ArtifactTypeId::valueOf);
   }

   public static ArtifactTypeId valueOf(Long id) {
      final class ArtifactTypeIdImpl extends BaseId implements ArtifactTypeId {
         public ArtifactTypeIdImpl(Long id) {
            super(id);
         }
      }
      return new ArtifactTypeIdImpl(id);
   }
}