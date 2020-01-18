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
package org.eclipse.osee.jaxrs.client.internal.ext;

import javax.ws.rs.ext.ParamConverter;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Ryan D. Brooks
 */
public class IdParamConverter implements ParamConverter<Id> {
   @Override
   public String toString(Id value) {
      return value.getIdString();
   }

   @Override
   public Id fromString(String value) {
      /*
       * return null so that the default parameter conversion will be used. Namely that
       * org.apache.cxf.jaxrs.utils.InjectionUtils.handleParameter(String, boolean, Class<T>, Annotation[],
       * ParameterType, Message) will call valueOf(String value) on the formal parameter type
       */
      return null;
   }
}