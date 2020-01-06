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

import org.eclipse.osee.ats.api.data.enums.token.PriorityAttributeType.PriorityEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class PriorityAttributeType extends AttributeTypeEnum<PriorityEnum> {

   // @formatter:off
	public final PriorityEnum Priority1 = new PriorityEnum(0, "1");
	public final PriorityEnum Priority2 = new PriorityEnum(1, "2");
	public final PriorityEnum Priority3 = new PriorityEnum(2, "3");
	public final PriorityEnum Priority4 = new PriorityEnum(3, "4");
	public final PriorityEnum Priority5 = new PriorityEnum(4, "5");
	// @formatter:on

   public PriorityAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847179L, namespace, "Priority", mediaType, "", taggerType);
   }

   public class PriorityEnum extends EnumToken {
      public PriorityEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
