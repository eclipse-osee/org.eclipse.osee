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

/**
 * @author Murshed Alam
 */
public class DataClassificationAttributeType extends AttributeTypeEnum<DataClassificationEnum> {

   public final DataClassificationEnum CUI = new DataClassificationEnum(0, "CUI");
   public final DataClassificationEnum Unspecified = new DataClassificationEnum(1, "Unspecified");

   public DataClassificationAttributeType(NamespaceToken namespace, int enumCount) {
      super(4024614255972662076L, namespace, "Data Classification", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public DataClassificationAttributeType() {
      this(NamespaceToken.OSEE, 2);
   }

   public class DataClassificationEnum extends EnumToken {
      public DataClassificationEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}
