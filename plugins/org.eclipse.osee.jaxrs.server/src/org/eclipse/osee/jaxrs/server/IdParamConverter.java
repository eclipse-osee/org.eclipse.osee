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

import java.util.function.Function;
import javax.ws.rs.ext.ParamConverter;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;

/**
 * @author Ryan D. Brooks
 */
public class IdParamConverter<T> implements ParamConverter<T> {
   private final Function<Long, T> function;

   public IdParamConverter(Function<Long, T> function) {
      this.function = function;
   }

   @Override
   public String toString(T value) {
      /*
       * return null so that the default parameter conversion will be used. Namely that
       * org.apache.cxf.jaxrs.utils.InjectionUtils.handleParameter(String, boolean, Class<T>, Annotation[],
       * ParameterType, Message) will call valueOf(String value) on the formal parameter type
       */
      return null;
   }

   @Override
   public T fromString(String value) {
      T token = function.apply(Long.valueOf(value));
      if (token == null) {
         throw new ItemDoesNotExist("Missing Orcs Type with id " + value);
      }
      return token;
   }
}