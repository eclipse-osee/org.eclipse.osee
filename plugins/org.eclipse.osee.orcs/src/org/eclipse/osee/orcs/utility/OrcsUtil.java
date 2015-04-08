/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.utility;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdentity;

/**
 * @author Angel Avila
 */
public final class OrcsUtil {

   private OrcsUtil() {
      // Utility class
   }

   public static ArtifactId newArtifactId(long uuid, String guid, String name) {
      return new ArtifactToken(uuid, guid, name);
   }

   private static class ArtifactToken extends NamedIdentity<String> implements ArtifactId {
      private final long uuid;

      public ArtifactToken(long uuid, String guid, String name) {
         super(guid, name);
         this.uuid = uuid;
      }

      @Override
      public long getUuid() {
         return uuid;
      }
   }

}
