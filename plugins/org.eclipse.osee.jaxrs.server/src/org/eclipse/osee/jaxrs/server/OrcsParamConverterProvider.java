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
package org.eclipse.osee.jaxrs.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.orcs.OrcsTypes;

/**
 * @author Ryan D. Brooks
 */
@Provider
public final class OrcsParamConverterProvider implements javax.ws.rs.ext.ParamConverterProvider {
   private final ParamConverter<ArtifactTypeToken> artifactTypeConverter;
   private final ParamConverter<AttributeTypeToken> attributeTypeConverter;
   private final ParamConverter<RelationTypeToken> relationTypeConverter;

   public OrcsParamConverterProvider(OrcsTypes orcsTypes) {
      artifactTypeConverter = new IdParamConverter<>(orcsTypes.getArtifactTypes()::get);
      attributeTypeConverter = new IdParamConverter<>(orcsTypes.getAttributeTypes()::get);
      relationTypeConverter = new IdParamConverter<>(orcsTypes.getRelationTypes()::get);
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
      return null;
   }
}