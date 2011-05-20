/*
 * Created on Jun 9, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validator;

import java.util.Collection;
import java.util.Date;

public class MockDateValueProvider extends MockValueProvider {

   private final Collection<Date> dateValues;

   public MockDateValueProvider(Collection<Date> dateValues) {
      this(null, dateValues);
   }

   public MockDateValueProvider(String name, Collection<Date> dateValues) {
      super(name, null);
      this.dateValues = dateValues;
   }

   @Override
   public Collection<Date> getDateValues() {
      return dateValues;
   }

}
