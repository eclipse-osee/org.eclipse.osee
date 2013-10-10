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

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Ryan D. Brooks
 */
public class FullyNamedIdentity<T> extends AbstractNamedIdentity<T> {
   private String name;
   private final String description;

   public FullyNamedIdentity(T guid, String name) {
      this(guid, name, "");
   }

   public FullyNamedIdentity(T guid, String name, String description) {
      super(guid);
      this.name = name;
      this.description = description;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getDescription() {
      return description;
   }

   @SuppressWarnings("unused")
   public void setName(String name) throws OseeCoreException {
      this.name = name;
   }

}