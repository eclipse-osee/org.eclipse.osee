/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import org.eclipse.osee.framework.core.enums.token.SoftwareCriticalityIndexAttributeType.SoftwareCriticalityIndexEnum;

/**
 * @author David W. Miller
 */
public class SoftwareCriticalityIndexAttributeType extends AttributeTypeEnum<SoftwareCriticalityIndexEnum> {

   public final SoftwareCriticalityIndexEnum Unspecified = new SoftwareCriticalityIndexEnum(0, "Unspecified");
   public final SoftwareCriticalityIndexEnum SwCI1 = new SoftwareCriticalityIndexEnum(1, "SwCI 1");
   public final SoftwareCriticalityIndexEnum SwCI2 = new SoftwareCriticalityIndexEnum(2, "SwCI 2");
   public final SoftwareCriticalityIndexEnum SwCI3 = new SoftwareCriticalityIndexEnum(3, "SwCI 3");
   public final SoftwareCriticalityIndexEnum SwCI4 = new SoftwareCriticalityIndexEnum(4, "SwCI 4");
   public final SoftwareCriticalityIndexEnum SwCI5 = new SoftwareCriticalityIndexEnum(5, "SwCI 5");

   public SoftwareCriticalityIndexAttributeType(NamespaceToken namespace, int enumCount) {
      super(2078511098117892252L, namespace, "Software Criticality Index", MediaType.TEXT_PLAIN,
         "Software Criticality Index Classification", TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public SoftwareCriticalityIndexAttributeType() {
      this(NamespaceToken.OSEE, 6);
   }

   public class SoftwareCriticalityIndexEnum extends EnumToken {
      public SoftwareCriticalityIndexEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}