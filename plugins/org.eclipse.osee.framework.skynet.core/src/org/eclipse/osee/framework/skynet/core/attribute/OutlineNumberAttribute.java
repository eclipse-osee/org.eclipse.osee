/*
 * Created on Jan 27, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

public class OutlineNumberAttribute extends CharacterBackedAttribute<String> {
   @Override
   public String getValue() throws OseeCoreException {
      return getAttributeDataProvider().getValueAsString();
   }

   @Override
   public boolean subClassSetValue(String value) throws OseeCoreException {
      return getAttributeDataProvider().setValue(value);
   }

   @Override
   protected String convertStringToValue(String value) {
      return value;
   }
}
