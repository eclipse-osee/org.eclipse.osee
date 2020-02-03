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

import org.eclipse.osee.ats.api.data.enums.token.WorkPackageTypeAttributeType.WorkPackageTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class WorkPackageTypeAttributeType extends AttributeTypeEnum<WorkPackageTypeEnum> {

   public final WorkPackageTypeEnum Discrete = new WorkPackageTypeEnum(0, "Discrete");
   public final WorkPackageTypeEnum DiscreteComplete = new WorkPackageTypeEnum(1, "Discrete - % Complete");
   public final WorkPackageTypeEnum Discrete50_50 = new WorkPackageTypeEnum(2, "Discrete - 50-50");
   public final WorkPackageTypeEnum Discrete0_100 = new WorkPackageTypeEnum(3, "Discrete - 0-100");
   public final WorkPackageTypeEnum LOE = new WorkPackageTypeEnum(4, "LOE");
   public final WorkPackageTypeEnum LOE_Planning = new WorkPackageTypeEnum(5, "LOE_Planning");
   public final WorkPackageTypeEnum Planning = new WorkPackageTypeEnum(6, "Planning");

   public WorkPackageTypeAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(72057594037928065L, namespace, "ats.Work Package Type", mediaType, "", taggerType, 7);
   }

   public class WorkPackageTypeEnum extends EnumToken {
      public WorkPackageTypeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}