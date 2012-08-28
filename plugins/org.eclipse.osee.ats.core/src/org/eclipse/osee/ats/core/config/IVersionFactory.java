/*
 * Created on Aug 27, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.version.IAtsVersion;

public interface IVersionFactory {

   IAtsVersion createVersion(String title, String create, String generate);

   IAtsVersion getOrCreate(String guid, String name);

   IAtsVersion createVersion(String name);

}
