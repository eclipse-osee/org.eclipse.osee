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
import org.eclipse.osee.framework.core.enums.token.DataRightsClassificationAttributeType.DataRightsClassificationEnum;

/**
 * @author Stephen J. Molaro
 */
public class DataRightsClassificationAttributeType extends AttributeTypeEnum<DataRightsClassificationEnum> {

   public final DataRightsClassificationEnum RestrictedRights =
      new DataRightsClassificationEnum(0, "Restricted Rights");
   public final DataRightsClassificationEnum GovernmentPurposeRights =
      new DataRightsClassificationEnum(1, "Government Purpose Rights");
   public final DataRightsClassificationEnum Unspecified = new DataRightsClassificationEnum(2, "Unspecified");
   public final DataRightsClassificationEnum Proprietary = new DataRightsClassificationEnum(3, "Proprietary");
   public final DataRightsClassificationEnum LimitedRights = new DataRightsClassificationEnum(4, "Limited Rights");
   public final DataRightsClassificationEnum UnlimitedRights = new DataRightsClassificationEnum(5, "Unlimited Rights");
   public final DataRightsClassificationEnum ExportControlledItar =
      new DataRightsClassificationEnum(6, "Export Controlled ITAR");

   public DataRightsClassificationAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847317L, namespace, "Data Rights Classification", mediaType,
         "Restricted Rights:  Rights are retained by the company\n\nRestricted Rights Mixed:  contains some Restricted Rights that need separation of content with other rights\n\nOther:  does not contain content with Restricted Rights\n\nUnspecified: not yet specified",
         taggerType, 7);
   }

   public class DataRightsClassificationEnum extends EnumToken {
      public DataRightsClassificationEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}