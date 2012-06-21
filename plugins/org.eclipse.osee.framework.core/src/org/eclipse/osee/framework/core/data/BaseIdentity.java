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

/**
 * @author Roberto E. Escobar
 */
public class BaseIdentity<T> extends AbstractIdentity<T> {

   private final T guid;

   public BaseIdentity(T guid) {
      super();
      this.guid = guid;
      if (guid == null) {
         throw new IllegalArgumentException("uuid cannot be null");
      }
   }

   @Override
   public T getGuid() {
      return guid;
   }

}