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
package org.eclipse.osee.disposition.rest.enums.token;

import org.eclipse.osee.disposition.rest.enums.token.DispoImportStateAttributeType.DispoImportStateEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class DispoImportStateAttributeType extends AttributeTypeEnum<DispoImportStateEnum> {

   public final DispoImportStateEnum All = new DispoImportStateEnum(0, "All");
   public final DispoImportStateEnum None = new DispoImportStateEnum(1, "None");
   public final DispoImportStateEnum NoChange = new DispoImportStateEnum(2, "No Change");
   public final DispoImportStateEnum OK = new DispoImportStateEnum(3, "OK");
   public final DispoImportStateEnum Warnings = new DispoImportStateEnum(4, "Warnings");
   public final DispoImportStateEnum Failed = new DispoImportStateEnum(5, "Failed");
   public final DispoImportStateEnum Unspecified = new DispoImportStateEnum(6, "Unspecified");

   public DispoImportStateAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(3458764513820541334L, namespace, "dispo.Import State", mediaType, "", taggerType, 7);
   }

   public class DispoImportStateEnum extends EnumToken {
      public DispoImportStateEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}