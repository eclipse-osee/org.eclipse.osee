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
import org.eclipse.osee.framework.core.enums.token.ProductApplicabilityAttributeType.ProductApplicabilityEnum;

/**
 * @author Audrey Denk
 */
public class ProductApplicabilityAttributeType extends AttributeTypeEnum<ProductApplicabilityEnum> {

   public final ProductApplicabilityEnum String = new ProductApplicabilityEnum(0, "OFP");

   public ProductApplicabilityAttributeType(NamespaceToken namespace, int enumCount) {
      super(4522673803793808650L, namespace, "Product Applicability", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public ProductApplicabilityAttributeType() {
      this(NamespaceToken.OSEE, 1);
   }

   public class ProductApplicabilityEnum extends EnumToken {
      public ProductApplicabilityEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}