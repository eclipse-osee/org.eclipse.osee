/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.proxy;

import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeWriteable;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("rawtypes")
public class AttributeProxyFactory extends ProxyFactory<Attribute, AttributeReadable, AttributeWriteable> {

   public AttributeProxyFactory(InvocationHandlerFactory<Attribute> invocationHandler) {
      super(invocationHandler, Attribute.class, AttributeReadable.class, AttributeWriteable.class);
   }
}
