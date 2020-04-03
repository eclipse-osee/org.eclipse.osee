/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
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
   private final ParamConverter<Id> idConverter = new IdParamConverter<>(null);

   public OrcsParamConverterProvider(OrcsTokenService tokenService) {
      if (tokenService == null) {
         artifactTypeConverter = null;
         attributeTypeConverter = null;
         relationTypeConverter = null;
      } else {
         artifactTypeConverter = new IdParamConverter<>(tokenService::getArtifactType);
         attributeTypeConverter = new IdParamConverter<>(tokenService::getAttributeType);
         relationTypeConverter = new IdParamConverter<>(tokenService::getRelationType);
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
      if (Id.class.isAssignableFrom(rawType)) {
         return (ParamConverter<T>) idConverter;
      }

      return null;
   }
}