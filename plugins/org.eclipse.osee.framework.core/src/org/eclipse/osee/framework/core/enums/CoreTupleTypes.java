/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreTupleFamilyTypes.DefaultFamily;
import static org.eclipse.osee.framework.core.enums.CoreTupleFamilyTypes.ProductLineFamily;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.TupleFamilyId;
import org.eclipse.osee.framework.core.data.TupleTypeId;

/**
 * @author Ryan D. Brooks
 */
public final class CoreTupleTypes {

   public static final Tuple2Type<TupleFamilyId, TupleTypeId> TupleMetaType = Tuple2Type.valueOf(DefaultFamily, 1L);
   public static final Tuple2Type<ArtifactId, String> ViewApplicability = Tuple2Type.valueOf(ProductLineFamily, 2L);
   public static final Tuple2Type<Long, AttributeId> OseeTypeDef = Tuple2Type.valueOf(DefaultFamily, 4L);

   private CoreTupleTypes() {
      // Constants
   }
}