/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import static org.eclipse.osee.framework.core.data.TupleTypeImpl.ArtifactType;
import static org.eclipse.osee.framework.core.data.TupleTypeImpl.AttributeType;
import static org.eclipse.osee.framework.core.data.TupleTypeImpl.KeyedString;
import static org.eclipse.osee.framework.core.data.TupleTypeImpl.RelationType;
import static org.eclipse.osee.framework.core.enums.CoreTupleFamilyTypes.DefaultFamily;
import static org.eclipse.osee.framework.core.enums.CoreTupleFamilyTypes.JoinFamily;
import static org.eclipse.osee.framework.core.enums.CoreTupleFamilyTypes.ProductLineFamily;
import java.util.function.Function;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeJoin;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeJoin;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeJoin;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.TupleFamilyId;
import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.data.TupleTypeImpl;

/**
 * @author Ryan D. Brooks
 */
public final class CoreTupleTypes {

   public static final Tuple2Type<TupleFamilyId, TupleTypeId> TupleMetaType =
      Tuple2Type.valueOf(DefaultFamily, 1L, TupleFamilyId::valueOf, TupleTypeId::valueOf);
   public static final Tuple2Type<ArtifactId, String> ViewApplicability =
      Tuple2Type.valueOf(ProductLineFamily, 2L, ArtifactId::valueOf, KeyedString);
   public static final Tuple2Type<ArtifactId, String> ApplicabilityDefinition =
      Tuple2Type.valueOf(ProductLineFamily, 11L, ArtifactId::valueOf, KeyedString);
   public static final Tuple2Type<ArtifactId, ApplicabilityId> ArtifactReferenceApplicabilityType =
      Tuple2Type.valueOf(CoreTupleFamilyTypes.ProductLineFamily, 13L, ArtifactId::valueOf, ApplicabilityId::valueOf);
   public static final Tuple2Type<ArtifactId, ArtifactId> VersionConfig =
      Tuple2Type.valueOf(ProductLineFamily, 12L, ArtifactId::valueOf, ArtifactId::valueOf);

   public static final Tuple2Type<ArtifactTypeJoin, ArtifactTypeToken> ArtifactTypeJoin =
      Tuple2Type.valueOf(JoinFamily, 14L, TupleTypeImpl.ArtifactTypeJoin, ArtifactType);
   public static final Tuple2Type<AttributeTypeJoin, AttributeTypeToken> AttributeTypeJoin =
      Tuple2Type.valueOf(JoinFamily, 15L, TupleTypeImpl.AttributeTypeJoin, AttributeType);
   public static final Tuple2Type<RelationTypeJoin, RelationTypeToken> RelationTypeJoin =
      Tuple2Type.valueOf(JoinFamily, 16L, TupleTypeImpl.RelationTypeJoin, RelationType);

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