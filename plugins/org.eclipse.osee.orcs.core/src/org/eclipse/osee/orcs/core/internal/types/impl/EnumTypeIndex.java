/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.internal.types.impl;

import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.orcs.data.EnumType;

/**
 * @author Roberto E. Escobar
 */
public class EnumTypeIndex extends TokenTypeIndex<EnumType, XOseeEnumType> {
   public EnumTypeIndex() {
      super(new EnumTypeImpl(Id.SENTINEL, Named.SENTINEL, null));
   }
}