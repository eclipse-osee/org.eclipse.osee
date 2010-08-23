/*
 * Created on Aug 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.util;

import org.apache.commons.httpclient.methods.PostMethod;

public class DeleteMethod extends PostMethod {

   public DeleteMethod(String uri) {
      super(uri);
   }

   @Override
   public String getName() {
      return "DELETE";
   }

}
