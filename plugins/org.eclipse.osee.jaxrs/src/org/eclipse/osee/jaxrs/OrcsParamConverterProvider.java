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

package org.eclipse.osee.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ActivityTypeId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeJoin;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeJoin;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeJoin;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Ryan D. Brooks
 */
@Provider
public final class OrcsParamConverterProvider implements javax.ws.rs.ext.ParamConverterProvider {
   private final ParamConverter<ArtifactTypeToken> artifactTypeConverter;
   private final ParamConverter<AttributeTypeGeneric<?>> attributeTypeConverter;
   private final ParamConverter<RelationTypeToken> relationTypeConverter;
   private final ParamConverter<ArtifactId> artifactIdConverter;
   private final ParamConverter<UserId> userIdConverter;
   private final ParamConverter<BranchId> branchIdConverter;
   private final ParamConverter<TransactionId> transactionIdConverter;
   private final ParamConverter<AttributeId> attributeIdConverter;
   private final ParamConverter<ActivityTypeId> activityTypeIdConverter;

   private final ParamConverter<BranchState> branchStateConverter;
   private final ParamConverter<BranchType> branchTypeConverter;

   private final ParamConverter<ArtifactTypeJoin> artifactTypeJoinConverter;
   private final ParamConverter<AttributeTypeJoin> attributeTypeJoinConverter;
   private final ParamConverter<RelationTypeJoin> relationTypeJoinConverter;

   private final ParamConverter<Id> idConverter = new IdParamConverter<>(null);

   public OrcsParamConverterProvider(OrcsTokenService tokenService) {
      if (tokenService == null) {
         artifactTypeConverter = null;
         attributeTypeConverter = null;
         relationTypeConverter = null;

         artifactTypeJoinConverter = null;
         attributeTypeJoinConverter = null;
         relationTypeJoinConverter = null;
      } else {
         artifactTypeConverter = new IdParamConverter<>(tokenService::getArtifactType);
         attributeTypeConverter = new IdParamConverter<>(tokenService::getAttributeType);
         relationTypeConverter = new IdParamConverter<>(tokenService::getRelationType);

         artifactTypeJoinConverter = new IdParamConverter<>(tokenService::getArtifactTypeJoin);
         attributeTypeJoinConverter = new IdParamConverter<>(tokenService::getAttributeTypeJoin);
         relationTypeJoinConverter = new IdParamConverter<>(tokenService::getRelationTypeJoin);
      }
      userIdConverter = new IdParamConverter<>(UserId::valueOf);
      branchIdConverter = new IdParamConverter<>(BranchId::valueOf);
      artifactIdConverter = new IdParamConverter<>(ArtifactId::valueOf);
      transactionIdConverter = new IdParamConverter<>(TransactionId::valueOf);
      activityTypeIdConverter = new IdParamConverter<>(ActivityTypeId::valueOf);
      branchStateConverter = new IdParamConverter<>(BranchState::valueOf);
      branchTypeConverter = new IdParamConverter<>(BranchType::valueOf);
      attributeIdConverter = new IdParamConverter<>(AttributeId::valueOf);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
      if (ArtifactTypeToken.class.equals(rawType)) {
         return (ParamConverter<T>) artifactTypeConverter;
      }
      if (AttributeTypeToken.class.isAssignableFrom(rawType)) {
         return (ParamConverter<T>) attributeTypeConverter;
      }
      if (RelationTypeToken.class.equals(rawType)) {
         return (ParamConverter<T>) relationTypeConverter;
      }

      if (ArtifactTypeJoin.class.isAssignableFrom(rawType)) {
         return (ParamConverter<T>) artifactTypeJoinConverter;
      }
      if (AttributeTypeJoin.class.isAssignableFrom(rawType)) {
         return (ParamConverter<T>) attributeTypeJoinConverter;
      }
      if (RelationTypeJoin.class.isAssignableFrom(rawType)) {
         return (ParamConverter<T>) relationTypeJoinConverter;
      }
      if (ActivityTypeId.class.isAssignableFrom(rawType)) {
         return (ParamConverter<T>) activityTypeIdConverter;
      }
      if (UserId.class.isAssignableFrom(rawType)) {
         return (ParamConverter<T>) userIdConverter;
      }
      if (BranchId.class.isAssignableFrom(rawType)) {
         return (ParamConverter<T>) branchIdConverter;
      }
      if (ArtifactId.class.isAssignableFrom(rawType)) {
         return (ParamConverter<T>) artifactIdConverter;
      }
      if (TransactionId.class.isAssignableFrom(rawType)) {
         return (ParamConverter<T>) transactionIdConverter;
      }
      if (AttributeId.class.isAssignableFrom(rawType)) {
         return (ParamConverter<T>) attributeIdConverter;
      }
      if (BranchState.class.isAssignableFrom(rawType)) {
         return (ParamConverter<T>) branchStateConverter;
      }
      if (BranchType.class.isAssignableFrom(rawType)) {
         return (ParamConverter<T>) branchTypeConverter;
      }
      if (Id.class.isAssignableFrom(rawType)) {
         return (ParamConverter<T>) idConverter;
      }

      return null;
   }
}