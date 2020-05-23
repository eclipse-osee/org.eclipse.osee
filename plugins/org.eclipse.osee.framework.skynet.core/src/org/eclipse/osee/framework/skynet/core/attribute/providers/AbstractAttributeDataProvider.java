/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.attribute.providers;

import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractAttributeDataProvider<T> implements IAttributeDataProvider {
   private final Attribute<T> attribute;

   public AbstractAttributeDataProvider(Attribute<T> attribute) {
      super();
      this.attribute = attribute;
   }

   /**
    * @return the attribute
    */
   protected Attribute<T> getAttribute() {
      return attribute;
   }
}