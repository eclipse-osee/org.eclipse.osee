/*
 * Created on Apr 11, 2014
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

public abstract class AbstractOseeIdType<KEY> extends AbstractOseeType<KEY> {

   protected AbstractOseeIdType(KEY guid, String key) {
      super(guid, key);
   }

   public long getId() {
      return getFieldValueLogException(IOseeStorable.UNPERSISTED_VALUE, UNIQUE_ID_FIELD_KEY);
   }

   public void setId(long uniqueId) throws OseeCoreException {
      setField(UNIQUE_ID_FIELD_KEY, uniqueId);
   }

}
