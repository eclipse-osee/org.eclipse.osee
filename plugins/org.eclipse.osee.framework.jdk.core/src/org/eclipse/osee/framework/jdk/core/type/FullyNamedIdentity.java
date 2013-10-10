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

package org.eclipse.osee.framework.jdk.core.type;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 */
public class FullyNamedIdentity<T> extends NamedIdentity<T> implements FullyNamed, HasDescription {
   private final String description;

   public FullyNamedIdentity(T guid, String name) {
      this(guid, name, "");
   }

   public FullyNamedIdentity(T guid, String name, String description) {
      super(guid, name);
      this.description = description;
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
   public String getDescription() {
      return description;
   }
}