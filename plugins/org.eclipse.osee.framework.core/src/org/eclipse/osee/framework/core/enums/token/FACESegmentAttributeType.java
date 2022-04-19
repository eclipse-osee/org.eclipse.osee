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
import org.eclipse.osee.framework.core.enums.token.FACESegmentAttributeType.FACESegmentEnum;

/**
 * @author David W. Miller
 */
public class FACESegmentAttributeType extends AttributeTypeEnum<FACESegmentEnum> {

   public final FACESegmentEnum Unspecified = new FACESegmentEnum(0, "Unspecified");
   public final FACESegmentEnum PSSSPSDS = new FACESegmentEnum(1, "PSSS - PSDS");
   public final FACESegmentEnum PSSSPSCS = new FACESegmentEnum(2, "PSSS - PSCS");
   public final FACESegmentEnum PSSSPSGS = new FACESegmentEnum(3, "PSSS - PSGS");
   public final FACESegmentEnum PCS = new FACESegmentEnum(4, "PCS");
   public final FACESegmentEnum IOSS = new FACESegmentEnum(5, "IOSS");
   public final FACESegmentEnum TSS = new FACESegmentEnum(6, "TSS");

   public FACESegmentAttributeType(NamespaceToken namespace, int enumCount) {
      super(1532273833894818550L, namespace, "FACE Segment", MediaType.TEXT_PLAIN, "", TaggerTypeToken.PlainTextTagger,
         enumCount);
   }

   public FACESegmentAttributeType() {
      this(NamespaceToken.OSEE, 7);
   }

   public class FACESegmentEnum extends EnumToken {
      public FACESegmentEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}