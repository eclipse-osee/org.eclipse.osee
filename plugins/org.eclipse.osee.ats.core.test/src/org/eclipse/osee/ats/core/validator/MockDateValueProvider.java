/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
