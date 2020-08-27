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
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.SafetySeverityAttributeType.SafetySeverityEnum;

/**
 * @author Stephen J. Molaro
 */
public class SafetySeverityAttributeType extends AttributeTypeEnum<SafetySeverityEnum> {

   public final SafetySeverityEnum Catastrophic = new SafetySeverityEnum(0, "(1) Catastrophic");
   public final SafetySeverityEnum Critical = new SafetySeverityEnum(1, "(2) Critical");
   public final SafetySeverityEnum Marginal = new SafetySeverityEnum(2, "(3) Marginal");
   public final SafetySeverityEnum Negligible = new SafetySeverityEnum(3, "(4) Negligible");
   public final SafetySeverityEnum Unspecified = new SafetySeverityEnum(4, "Unspecified");
   public final SafetySeverityEnum None = new SafetySeverityEnum(5, "None");

   public SafetySeverityAttributeType(NamespaceToken namespace, int enumCount) {
      super(846763346271224762L, namespace, "Safety Severity", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public SafetySeverityAttributeType() {
      this(NamespaceToken.OSEE, 6);
   }

   public class SafetySeverityEnum extends EnumToken {
      public SafetySeverityEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}