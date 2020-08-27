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
import org.eclipse.osee.framework.core.enums.token.FeatureValueAttributeType.FeatureValueTypeEnum;

/**
 * @author Stephen J. Molaro
 */
public class FeatureValueAttributeType extends AttributeTypeEnum<FeatureValueTypeEnum> {

   public final FeatureValueTypeEnum String = new FeatureValueTypeEnum(0, "String");
   public final FeatureValueTypeEnum Boolean = new FeatureValueTypeEnum(1, "Boolean");
   public final FeatureValueTypeEnum Decimal = new FeatureValueTypeEnum(2, "Decimal");
   public final FeatureValueTypeEnum Integer = new FeatureValueTypeEnum(3, "Integer");

   public FeatureValueAttributeType(NamespaceToken namespace, int enumCount) {
      super(31669009535111027L, namespace, "Feature Value Type", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public FeatureValueAttributeType() {
      this(NamespaceToken.OSEE, 4);
   }

   public class FeatureValueTypeEnum extends EnumToken {
      public FeatureValueTypeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}