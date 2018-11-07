/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.dsl;

import org.eclipse.osee.ats.api.workdef.IAttributeResolver;

/**
 * @author Donald G. Dunne
 */
public class AttributeResolverService {

   public static AttributeResolverService instance = null;
   private IAttributeResolver attributeResolver;

   public static AttributeResolverService get() {
      return instance;
   }

   public AttributeResolverService() {
      instance = this;
   }

   public void setAttributeResolverService(IAttributeResolver attributeResolver) {
      this.attributeResolver = attributeResolver;
   }

   public IAttributeResolver getAttributeResolver() {
      return attributeResolver;
   }

}
