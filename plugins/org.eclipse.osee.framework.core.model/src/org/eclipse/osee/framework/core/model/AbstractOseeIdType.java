/*
 * Created on Apr 11, 2014
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model;

public abstract class AbstractOseeIdType<KEY> extends AbstractOseeType<KEY> {

   protected AbstractOseeIdType(Long guid, String key) {
      super(guid, key);
   }

   public Long getGuid() {
      return getId();
   }
}
