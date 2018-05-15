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
package org.eclipse.osee.ats.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import org.eclipse.osee.ats.api.util.IValueProvider;

/**
 * @author Donald G. Dunne
 */
public class StringValueProvider implements IValueProvider {

   private final Collection<String> values;
   private final String name;

   public StringValueProvider(Collection<String> values) {
      this("test", values);
   }

   public StringValueProvider(String name, Collection<String> values) {
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

   @Override
   public Collection<Date> getDateValues() {
      return Collections.emptyList();
   }

}
