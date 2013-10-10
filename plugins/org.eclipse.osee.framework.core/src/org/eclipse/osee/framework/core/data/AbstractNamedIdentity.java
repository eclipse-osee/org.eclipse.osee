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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractNamedIdentity<T> extends BaseIdentity<T> implements FullyNamed, HasDescription {

   public AbstractNamedIdentity(T uid) {
      super(uid);
   }

   @Override
   public String getUnqualifiedName() {
      String name = getName();
      if (Strings.isValid(name)) {
         int index = name.lastIndexOf('.');
         name = name.substring(index + 1);
      }
      return name;
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public int compareTo(Named other) {
      if (other != null && other.getName() != null && getName() != null) {
         return getName().compareTo(other.getName());
      }
      return -1;
   }
}