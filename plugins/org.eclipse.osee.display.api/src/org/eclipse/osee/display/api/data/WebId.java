/*
 * Created on Sep 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.display.api.data;

import org.eclipse.osee.framework.core.data.NamedIdentity;

public class WebId extends NamedIdentity<String> {

   public WebId(String guid, String name) {
      super(guid, name);
   }

}
