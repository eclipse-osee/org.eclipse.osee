/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import org.eclipse.osee.framework.core.enums.token.DataClassificationAttributeType.DataClassificationEnum;
import org.eclipse.osee.framework.core.publishing.CuiCategoryIndicator;
import org.eclipse.osee.framework.core.publishing.CuiTypeIndicator;

/**
 * @author Murshed Alam
 */
public class DataClassificationAttributeType extends AttributeTypeEnum<DataClassificationEnum> {

   public final DataClassificationEnum[] cuiCategoryCuiTypeEnumList =
      new DataClassificationEnum[CuiCategoryIndicator.values().length * CuiTypeIndicator.values().length];

   private final CuiCategoryIndicator[] categories = CuiCategoryIndicator.values();
   private final CuiTypeIndicator[] types = CuiTypeIndicator.values();
   private static int count = 0;

   public DataClassificationAttributeType(NamespaceToken namespace, int enumCount) {
      super(4024614255972662076L, namespace, "CUI Category And CUI Type", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);

      for (int i = 0; i < this.categories.length; i++) {
         for (int j = 0; j < this.types.length; j++) {
            //@formatter:off
            this.cuiCategoryCuiTypeEnumList[count] =
               new DataClassificationEnum(count,
                  this.categories[i].name()
                                    .concat(" - ")
                                    .concat(this.types[j].name()));
            //@formatter:on
            count++;
         }
      }
   }

   public DataClassificationAttributeType() {
      this(NamespaceToken.OSEE, count);
   }

   public class DataClassificationEnum extends EnumToken {
      public DataClassificationEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}
