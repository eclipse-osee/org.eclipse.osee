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
import org.eclipse.osee.framework.core.data.ArtifactTypeJoin;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeJoin;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeJoin;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Ryan D. Brooks
 */
@Provider
public final class OrcsParamConverterProvider implements javax.ws.rs.ext.ParamConverterProvider {
   private final ParamConverter<ArtifactTypeToken> artifactTypeConverter;
   private final ParamConverter<AttributeTypeToken> attributeTypeConverter;
   private final ParamConverter<RelationTypeToken> relationTypeConverter;

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
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
      if (ArtifactTypeToken.class.equals(rawType)) {
         return (ParamConverter<T>) artifactTypeConverter;
      }
      if (AttributeTypeToken.class.equals(rawType)) {
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

      if (Id.class.isAssignableFrom(rawType)) {
         return (ParamConverter<T>) idConverter;
      }

      return null;
   }
}