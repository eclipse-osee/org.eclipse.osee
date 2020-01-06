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

import org.eclipse.osee.disposition.rest.enums.token.DispoItemStatusAttributeType.DispoItemStatusEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class DispoItemStatusAttributeType extends AttributeTypeEnum<DispoItemStatusEnum> {

   // @formatter:off
	public final DispoItemStatusEnum Pass = new DispoItemStatusEnum(0, "PASS");
	public final DispoItemStatusEnum Incomplete = new DispoItemStatusEnum(1, "INCOMPLETE");
	public final DispoItemStatusEnum Complete = new DispoItemStatusEnum(2, "COMPLETE");
	public final DispoItemStatusEnum CompleteAnalyzed = new DispoItemStatusEnum(3, "COMPLETE-ANALYZED");
	public final DispoItemStatusEnum Unspecified = new DispoItemStatusEnum(4, "Unspecified");
	// @formatter:on

   public DispoItemStatusAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(3458764513820541336L, namespace, "Dispo Item Status", mediaType, "", taggerType);
   }

   public class DispoItemStatusEnum extends EnumToken {
      public DispoItemStatusEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
