/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.define;

import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.providers.MappedAttributeDataProvider;

/**
 * @author Ryan D. Brooks
 */
public class MappedAttributeDataProviderOld extends MappedAttributeDataProvider {
   public MappedAttributeDataProviderOld(Attribute<?> attribute) {
      super(attribute);
   }
}
