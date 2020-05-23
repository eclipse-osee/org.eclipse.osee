/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.SegmentAttributeType.FaceSegmentEnum;

/**
 * @author David W. Miller
 */
public final class SegmentAttributeType extends AttributeTypeEnum<FaceSegmentEnum> {

   public final FaceSegmentEnum Uspecified = new FaceSegmentEnum(0, "Unspecified");
   public final FaceSegmentEnum PSSS_PSDS = new FaceSegmentEnum(1, "PSSS-PSDS");
   public final FaceSegmentEnum PSSS_PSCS = new FaceSegmentEnum(2, "PSSS-PSCS");
   public final FaceSegmentEnum PSSS_PSGS = new FaceSegmentEnum(3, "PSSS-PSGS");
   public final FaceSegmentEnum PCS = new FaceSegmentEnum(4, "PCS");
   public final FaceSegmentEnum IOSS = new FaceSegmentEnum(5, "IOSS");
   public final FaceSegmentEnum TSS = new FaceSegmentEnum(6, "TSS");

   public SegmentAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(4630657574707057068L, namespace, "Segment", mediaType,
         "Enumeration to describe The Segment for the FACE UoC", taggerType, 7);
   }

   public class FaceSegmentEnum extends EnumToken {
      public FaceSegmentEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}