/*
 * Created on Jun 6, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.config.impl.Version;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;

/**
 * @author Donald G. Dunne
 */
public class VersionFactory {

   public static IAtsVersion createVersion(String title) {
      return createVersion(title, GUID.create(), HumanReadableId.generate());
   }

   public static IAtsVersion createVersion(String guid, String title) {
      return createVersion(title, guid, HumanReadableId.generate());
   }

   public static IAtsVersion createVersion(String title, String guid, String humanReadableId) {
      IAtsVersion version = new Version(title, guid, humanReadableId);
      AtsConfigCache.cache(version);
      return version;
   }

   public static IAtsVersion getOrCreate(String guid, String name) {
      IAtsVersion version = AtsConfigCache.getSoleByGuid(guid, IAtsVersion.class);
      if (version == null) {
         version = createVersion(guid, name);
      }
      return version;
   }

}
