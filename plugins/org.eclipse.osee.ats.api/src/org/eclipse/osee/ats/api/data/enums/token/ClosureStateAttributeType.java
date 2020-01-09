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

import org.eclipse.osee.ats.api.data.enums.token.ClosureStateAttributeType.ClosureStateEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class ClosureStateAttributeType extends AttributeTypeEnum<ClosureStateEnum> {

   // @formatter:off
	public final ClosureStateEnum Open = new ClosureStateEnum(0, "Open");
	public final ClosureStateEnum PrepareToClose = new ClosureStateEnum(1, "Prepare to Close");
	public final ClosureStateEnum CloseOut = new ClosureStateEnum(2, "Close Out");
	public final ClosureStateEnum Closed = new ClosureStateEnum(3, "Closed");
	// @formatter:on

   public ClosureStateAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847452L, namespace, "ats.closure.Closure State", mediaType, "", taggerType);
   }

   public class ClosureStateEnum extends EnumToken {
      public ClosureStateEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
