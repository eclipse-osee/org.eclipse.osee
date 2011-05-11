/*
 * Created on Jan 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import java.io.File;

public class WorkDefinitionSheet {

   public File file;
   public String name;
   public String legacyOverrideId;

   public WorkDefinitionSheet(String name, String legacyOverrideId, File file) {
      super();
      this.file = file;
      this.name = name;
      this.legacyOverrideId = legacyOverrideId;
   }

   public File getFile() {
      return file;
   }

   public String getName() {
      return name;
   }

   public String getLegacyOverrideId() {
      return legacyOverrideId;
   }

   @Override
   public String toString() {
      return String.format("%s filename[%s] overrideId[%s]", name, file, legacyOverrideId);
   }
}
