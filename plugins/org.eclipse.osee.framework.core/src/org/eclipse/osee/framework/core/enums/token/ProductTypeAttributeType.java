/*********************************************************************
 * Copyright (c) 2020 Boeing
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
import org.eclipse.osee.framework.core.enums.token.ProductTypeAttributeType.ProductTypeEnum;

/**
 * @author Audrey Denk
 */
public class ProductTypeAttributeType extends AttributeTypeEnum<ProductTypeEnum> {

   public final ProductTypeEnum Unspecified = new ProductTypeEnum(0, "Unspecified");
   public final ProductTypeEnum Code = new ProductTypeEnum(1, "Code");
   public final ProductTypeEnum Documentation = new ProductTypeEnum(2, "Documentation");
   public final ProductTypeEnum Requirements = new ProductTypeEnum(3, "Requirements");
   public final ProductTypeEnum Test = new ProductTypeEnum(4, "Test");
   public final ProductTypeEnum ContinuousIntegration = new ProductTypeEnum(5, "Continuous Integration");

   public ProductTypeAttributeType(NamespaceToken namespace, int enumCount) {
      super(4522673803793808650L, namespace, "Product Type", MediaType.TEXT_PLAIN, "", TaggerTypeToken.PlainTextTagger,
         enumCount);
   }

   public ProductTypeAttributeType() {
      this(NamespaceToken.OSEE, 6);
   }

   public class ProductTypeEnum extends EnumToken {
      public ProductTypeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}