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
import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.osee;
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
public interface CoreTupleTypes {

   public static final Tuple2Type<TupleFamilyId, TupleTypeId> TupleMetaType =
      osee.add(DefaultFamily, 1L, TupleFamilyId::valueOf, TupleTypeId::valueOf);
   public static final Tuple2Type<ArtifactId, String> ViewApplicability =
      osee.add(ProductLineFamily, 2L, ArtifactId::valueOf, KeyedString);
   public static final Tuple2Type<ArtifactId, String> ApplicabilityDefinition =
      osee.add(ProductLineFamily, 11L, ArtifactId::valueOf, KeyedString);
   public static final Tuple2Type<ArtifactId, ApplicabilityId> ArtifactReferenceApplicabilityType =
      osee.add(ProductLineFamily, 13L, ArtifactId::valueOf, ApplicabilityId::valueOf);
   public static final Tuple2Type<ArtifactId, ArtifactId> VersionConfig =
      osee.add(ProductLineFamily, 12L, ArtifactId::valueOf, ArtifactId::valueOf);

   public static final Tuple2Type<ArtifactTypeJoin, ArtifactTypeToken> ArtifactTypeJoin =
      osee.add(JoinFamily, 14L, TupleTypeImpl.ArtifactTypeJoin, ArtifactType);
   public static final Tuple2Type<AttributeTypeJoin, AttributeTypeToken> AttributeTypeJoin =
      osee.add(JoinFamily, 15L, TupleTypeImpl.AttributeTypeJoin, AttributeType);
   public static final Tuple2Type<RelationTypeJoin, RelationTypeToken> RelationTypeJoin =
      osee.add(JoinFamily, 16L, TupleTypeImpl.RelationTypeJoin, RelationType);

   //Modeling Feature dependence
   public static final Tuple2Type<ApplicabilityId, ApplicabilityId> ApplicabilityConstraint =
      osee.add(ProductLineFamily, 17L, ApplicabilityId::valueOf, ApplicabilityId::valueOf);

   // Data Maintenance
   public static final Tuple3Type<String, Long, Long> FixedMaintenanceData =
      osee.add(DefaultFamily, 5L, TupleTypeImpl.KeyedString, Function.identity(), Function.identity());
   public static final Tuple3Type<String, Long, Long> VariableMaintenanceData =
      osee.add(DefaultFamily, 6L, TupleTypeImpl.KeyedString, Function.identity(), Function.identity());
   public static final Tuple3Type<String, Long, Long> CrossRefMaintenanceData =
      osee.add(DefaultFamily, 7L, TupleTypeImpl.KeyedString, Function.identity(), Function.identity());
   public static final Tuple3Type<String, Long, Long> ReversalMaintenanceData =
      osee.add(DefaultFamily, 8L, TupleTypeImpl.KeyedString, Function.identity(), Function.identity());
   public static final Tuple3Type<String, Long, Long> LruDataTypes =
      osee.add(DefaultFamily, 9L, TupleTypeImpl.KeyedString, Function.identity(), Function.identity());
   public static final Tuple3Type<String, Long, Long> LruFormatData =
      osee.add(DefaultFamily, 10L, TupleTypeImpl.KeyedString, Function.identity(), Function.identity());

}