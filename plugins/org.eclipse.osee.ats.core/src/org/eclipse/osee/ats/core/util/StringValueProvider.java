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
