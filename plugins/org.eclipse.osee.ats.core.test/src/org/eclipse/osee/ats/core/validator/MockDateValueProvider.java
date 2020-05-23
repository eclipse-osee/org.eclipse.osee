/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.validator;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.core.util.StringValueProvider;

/**
 * @author Donald G. Dunne
 */
public class MockDateValueProvider extends StringValueProvider {

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
