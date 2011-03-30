/*
 * Created on Mar 29, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.copy;

public class CopyAtsUtil {

   public static String getConvertedName(ConfigData configData, String name) {
      return name.replaceFirst(configData.getSearchStr(), configData.getReplaceStr());
   }

}
