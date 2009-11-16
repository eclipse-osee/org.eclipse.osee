/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.internal.fields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AbstractOseeField;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.ChangeUtil;

/**
 * @author Roberto E. Escobar
 */
public class AliasesField extends AbstractOseeField<Collection<String>> {

   private final List<String> aliases;

   public AliasesField() {
      super();
      this.aliases = new ArrayList<String>();
   }

   @Override
   public Collection<String> get() throws OseeCoreException {
      return aliases;
   }

   @Override
   public void set(Collection<String> aliases) throws OseeCoreException {
      Collection<String> newList = normalize(aliases);
      boolean wasDifferent = ChangeUtil.isDifferent(get(), newList);
      if (wasDifferent) {
         aliases.clear();
         aliases.addAll(newList);
      }
      isDirty |= wasDifferent;
   }

   private Collection<String> normalize(Collection<String> aliases) throws OseeCoreException {
      if (aliases == null) {
         throw new OseeArgumentException("aliases cannot be null");
      }
      Collection<String> newList = new ArrayList<String>();
      for (String alias : aliases) {
         newList.add(alias.toLowerCase());
      }
      return newList;
   }
}
