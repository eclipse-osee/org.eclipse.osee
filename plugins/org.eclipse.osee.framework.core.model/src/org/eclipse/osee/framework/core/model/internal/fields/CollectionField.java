/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.core.model.internal.fields;

import java.util.Collection;
import org.eclipse.osee.framework.core.model.AbstractOseeField;
import org.eclipse.osee.framework.jdk.core.util.Compare;

/**
 * @author Roberto E. Escobar
 */
public class CollectionField<T> extends AbstractOseeField<Collection<T>> {

   private Collection<T> items;

   public CollectionField(Collection<T> items) {
      super();
      this.items = items;
   }

   @Override
   public Collection<T> get() {
      return items;
   }

   @Override
   public void set(Collection<T> input) {
      Collection<T> checked = checkInput(input);
      boolean isDifferent = Compare.isDifferent(get(), checked);
      if (isDifferent) {
         items = checked;
      }
      isDirty |= isDifferent;
   }

   //OseeCoreException is thrown by inherited class
   protected Collection<T> checkInput(Collection<T> input) {
      return input;
   }
}