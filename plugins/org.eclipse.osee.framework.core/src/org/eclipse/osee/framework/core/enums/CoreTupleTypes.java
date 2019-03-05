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
import java.util.function.Function;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.TupleFamilyId;
import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.data.TupleTypeImpl;

/**
 * @author Ryan D. Brooks
 */
public final class CoreTupleTypes {

   public static final Tuple2Type<TupleFamilyId, TupleTypeId> TupleMetaType = Tuple2Type.valueOf(DefaultFamily, 1L);
   public static final Tuple2Type<ArtifactId, String> ViewApplicability = Tuple2Type.valueOf(ProductLineFamily, 2L);
   public static final Tuple2Type<Long, AttributeId> OseeTypeDef = Tuple2Type.valueOf(DefaultFamily, 4L);
   public static final Tuple2Type<ArtifactId, ApplicabilityId> ArtifactReferenceApplicabilityType =
      Tuple2Type.valueOf(CoreTupleFamilyTypes.ProductLineFamily, 13L);
   public static final Tuple2Type<BranchId, ArtifactId> BranchView = Tuple2Type.valueOf(ProductLineFamily, 11L);
   public static final Tuple2Type<ArtifactId, ArtifactId> VersionConfig = Tuple2Type.valueOf(ProductLineFamily, 12L);

   // Data Maintenance
   public static final Tuple3Type<String, Long, Long> FixedMaintenanceData =
      Tuple3Type.valueOf(DefaultFamily, 5L, TupleTypeImpl.KeyedString, Function.identity(), Function.identity());
   public static final Tuple3Type<String, Long, Long> VariableMaintenanceData =
      Tuple3Type.valueOf(DefaultFamily, 6L, TupleTypeImpl.KeyedString, Function.identity(), Function.identity());
   public static final Tuple3Type<String, Long, Long> CrossRefMaintenanceData =
      Tuple3Type.valueOf(DefaultFamily, 7L, TupleTypeImpl.KeyedString, Function.identity(), Function.identity());
   public static final Tuple3Type<String, Long, Long> ReversalMaintenanceData =
      Tuple3Type.valueOf(DefaultFamily, 8L, TupleTypeImpl.KeyedString, Function.identity(), Function.identity());
   public static final Tuple3Type<String, Long, Long> LruDataTypes =
      Tuple3Type.valueOf(DefaultFamily, 9L, TupleTypeImpl.KeyedString, Function.identity(), Function.identity());
   public static final Tuple3Type<String, Long, Long> LruFormatData =
      Tuple3Type.valueOf(DefaultFamily, 10L, TupleTypeImpl.KeyedString, Function.identity(), Function.identity());

   private CoreTupleTypes() {
      // Constants
   }
}