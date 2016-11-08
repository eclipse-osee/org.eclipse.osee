/*
 * Created on Nov 8, 2016
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.jdk.core.type;

public interface NamedId extends Named, Id {

   default String toStringWithId() {
      return String.format("[%s][%s]", getName(), getId());
   }

}
