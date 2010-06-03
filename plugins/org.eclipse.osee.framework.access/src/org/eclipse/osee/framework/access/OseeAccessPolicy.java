/*
 * Created on Jun 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.access;

import java.util.Collection;
import org.eclipse.osee.framework.access.internal.OseeAccessPoint;

public class OseeAccessPolicy {

   public <H extends OseeAccessHandler> Collection<H> getApplicable(OseeAccessPoint<H> accessPoint, Collection<H> data) {
      // Filter Out Rules;
      return null;
   }

}
