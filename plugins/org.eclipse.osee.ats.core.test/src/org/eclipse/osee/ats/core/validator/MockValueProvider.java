/*
 * Created on May 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validator;

import java.util.Collection;

/**
 * @author Donald G. Dunne
 */
public class MockValueProvider implements IValueProvider {

   private final Collection<String> values;
   private final String name;

   public MockValueProvider(Collection<String> values) {
      this("test", values);
   }

   public MockValueProvider(String name, Collection<String> values) {
      this.name = name;
      this.values = values;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public boolean isEmpty() {
      return values.isEmpty();
   }

   @Override
   public Collection<String> getValues() {
      return values;
   }

}
