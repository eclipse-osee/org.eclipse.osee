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
package org.eclipse.osee.framework.skynet.core.types.impl;

import org.eclipse.osee.framework.core.data.IOseeDataAccessor;
import org.eclipse.osee.framework.core.data.IOseeStorableType;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractDatabaseAccessor<T extends IOseeStorableType> implements IOseeDataAccessor<T> {

   private final IOseeTypeFactory factory;

   protected AbstractDatabaseAccessor(IOseeTypeFactory factory) {
      this.factory = factory;
   }

   protected IOseeTypeFactory getFactory() {
      return factory;
   }
}
