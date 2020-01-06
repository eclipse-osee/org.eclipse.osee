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
import org.eclipse.osee.framework.core.enums.token.SafetySeverityAttributeType.SafetySeverityEnum;

/**
 * @author Stephen J. Molaro
 */
public class SafetySeverityAttributeType extends AttributeTypeEnum<SafetySeverityEnum> {

   // @formatter:off
	public final SafetySeverityEnum _1Catastrophic = new SafetySeverityEnum(0, "(1) Catastrophic");
	public final SafetySeverityEnum _2Critical = new SafetySeverityEnum(1, "(2) Critical");
	public final SafetySeverityEnum _3Marginal = new SafetySeverityEnum(2, "(3) Marginal");
	public final SafetySeverityEnum _4Negligible = new SafetySeverityEnum(3, "(4) Negligible");
	// @formatter:on

   public SafetySeverityAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(846763346271224762L, namespace, "Safety Severity", mediaType, "", taggerType);
   }

   public class SafetySeverityEnum extends EnumToken {
      public SafetySeverityEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
