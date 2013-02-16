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
package org.eclipse.osee.ats.core.config.internal;

import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.config.IVersionFactory;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;

/**
 * @author Donald G. Dunne
 */
public class VersionFactory implements IVersionFactory {

   private final AtsConfigCache cache;

   public VersionFactory(AtsConfigCache cache) {
      this.cache = cache;
   }

   public IAtsVersion createVersion(String title) {
      return createVersion(title, GUID.create(), HumanReadableId.generate());
   }

   public IAtsVersion createVersion(String guid, String title) {
      return createVersion(title, guid, HumanReadableId.generate());
   }

   public IAtsVersion createVersion(String title, String guid, String humanReadableId) {
      IAtsVersion version = new Version(title, guid, humanReadableId);
      cache.cache(version);
      return version;
   }

   public IAtsVersion getOrCreate(String guid, String name) {
      IAtsVersion version = cache.getSoleByGuid(guid, IAtsVersion.class);
      if (version == null) {
         version = createVersion(guid, name);
      }
      return version;
   }

}
