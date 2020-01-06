/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums.token;

import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.TisTestTypeAttributeType.TisTestTypeEnum;

/**
 * @author Stephen J. Molaro
 */
public class TisTestTypeAttributeType extends AttributeTypeEnum<TisTestTypeEnum> {

   // @formatter:off
	public final TisTestTypeEnum StationaryVehicle = new TisTestTypeEnum(0, "Stationary Vehicle");
	public final TisTestTypeEnum InOperation = new TisTestTypeEnum(1, "In Operation");
	// @formatter:on

   public TisTestTypeAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847118L, namespace, "TIS Test Type", mediaType, "TIS Test Type", taggerType);
   }

   public class TisTestTypeEnum extends EnumToken {
      public TisTestTypeEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
