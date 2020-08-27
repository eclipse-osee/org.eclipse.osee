/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.enums.token;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.TisTestTypeAttributeType.TisTestTypeEnum;

/**
 * @author Stephen J. Molaro
 */
public class TisTestTypeAttributeType extends AttributeTypeEnum<TisTestTypeEnum> {

   public final TisTestTypeEnum StationaryVehicle = new TisTestTypeEnum(0, "Stationary Vehicle");
   public final TisTestTypeEnum InOperation = new TisTestTypeEnum(1, "In Operation");

   public TisTestTypeAttributeType(NamespaceToken namespace, int enumCount) {
      super(1152921504606847118L, namespace, "TIS Test Type", MediaType.TEXT_PLAIN, "TIS Test Type",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public TisTestTypeAttributeType() {
      this(NamespaceToken.OSEE, 2);
   }

   public class TisTestTypeEnum extends EnumToken {
      public TisTestTypeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}