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
package org.eclipse.osee.ats.api.data.enums.token;

import org.eclipse.osee.ats.api.data.enums.token.PointAttributeType.PointEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class PointAttributeType extends AttributeTypeEnum<PointEnum> {

   public final PointEnum P_1 = new PointEnum(0, "1");
   public final PointEnum P_2 = new PointEnum(1, "2");
   public final PointEnum P_3 = new PointEnum(2, "3");
   public final PointEnum P_4 = new PointEnum(3, "4");
   public final PointEnum P_5 = new PointEnum(4, "5");
   public final PointEnum P_8 = new PointEnum(5, "8");
   public final PointEnum P_13 = new PointEnum(6, "13");
   public final PointEnum P_20 = new PointEnum(7, "20");
   public final PointEnum P_40 = new PointEnum(8, "40");
   public final PointEnum P_80 = new PointEnum(9, "80");
   public final PointEnum P_150 = new PointEnum(10, "150");
   public final PointEnum Epic = new PointEnum(11, "Epic");

   public PointAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847178L, namespace, "ats.Points", mediaType, "", taggerType, 12);
   }

   public class PointEnum extends EnumToken {
      public PointEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}