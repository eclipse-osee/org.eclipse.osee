/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Ryan D. Brooks
 */
public interface ArtifactTypeToken extends NamedId, ArtifactTypeId {
   ArtifactTypeToken SENTINEL = valueOf(Id.SENTINEL, Named.SENTINEL);

   public static ArtifactTypeToken valueOf(String id) {
      return valueOf(Long.valueOf(id), Named.SENTINEL);
   }

   public static ArtifactTypeToken valueOf(long id, String name) {
      final class ArtifactTypeTokenImpl extends NamedIdBase implements ArtifactTypeToken {

         public ArtifactTypeTokenImpl(Long id, String name) {
            super(id, name);
         }
      }
      return new ArtifactTypeTokenImpl(id, name);
   }
}