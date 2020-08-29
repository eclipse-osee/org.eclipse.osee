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
import org.eclipse.osee.framework.core.enums.DispoTypeTokenProvider;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.DispoImportStateAttributeType.DispoImportStateEnum;

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

   public DispoImportStateAttributeType(NamespaceToken namespace, int enumCount) {
      super(3458764513820541334L, namespace, "dispo.Import State", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public DispoImportStateAttributeType() {
      this(DispoTypeTokenProvider.DISPO, 7);
   }

   public class DispoImportStateEnum extends EnumToken {
      public DispoImportStateEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}