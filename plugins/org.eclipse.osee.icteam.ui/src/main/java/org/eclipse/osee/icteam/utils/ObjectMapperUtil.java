/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.utils;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ajay Chandrahasan
 */

@Component("objectMapperUtil")
public class ObjectMapperUtil<T> {

   private static final Logger LOG = Logger.getLogger(ObjectMapperUtil.class);

   public T parseToObject(final String jsonString, final Class<T> objectType) {
      T object = null;

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      try {
         object = mapper.readValue(jsonString, objectType);
      } catch (IOException e) {
         LOG.error("Error while parsing string to object of type " + objectType, e);
      }

      return object;

   }

   public String parseToString(final T object) {
      String jsonString = null;

      ObjectMapper mapper = new ObjectMapper();
      try {
         jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);

      } catch (JsonProcessingException e) {
         LOG.error("Error while parsing object to string", e);
      }
      return jsonString;

   }

}
