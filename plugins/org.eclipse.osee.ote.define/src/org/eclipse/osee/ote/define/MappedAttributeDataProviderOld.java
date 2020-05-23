/*********************************************************************
 * Copyright (c) 2010 Boeing
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
