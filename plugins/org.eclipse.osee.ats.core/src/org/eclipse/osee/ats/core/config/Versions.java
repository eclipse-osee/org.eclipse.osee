/*
 * Created on Jun 4, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.config;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ats.core.model.IAtsVersion;

public class Versions {

   public static Collection<String> getNames(Collection<? extends IAtsVersion> versions) {
      ArrayList<String> names = new ArrayList<String>();
      for (IAtsVersion version : versions) {
         names.add(version.getName());
      }
      return names;
   }

}
