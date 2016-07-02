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
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Ryan D. Brooks
 */
public interface ApplicabilityId extends Id {
   public static final ApplicabilityId BASE = ApplicabilityId.valueOf(1L);

   default Long getUuid() {
      return getId();
   }

   public static ApplicabilityId valueOf(String id) {
      return valueOf(Long.valueOf(id));
   }

   @JsonCreator
   public static ApplicabilityId valueOf(long id) {
      final class ApplicabilityToken extends BaseId implements ApplicabilityId {
         public ApplicabilityToken(Long txId) {
            super(txId);
         }
      }
      return new ApplicabilityToken(id);
   }

}