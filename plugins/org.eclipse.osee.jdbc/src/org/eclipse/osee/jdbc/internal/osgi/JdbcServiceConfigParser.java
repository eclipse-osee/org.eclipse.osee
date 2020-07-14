/*********************************************************************
* Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jdbc.internal.osgi;

import static org.eclipse.osee.jdbc.JdbcException.newJdbcException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcException;

/**
 * @author Roberto E. Escobar
 */
public final class JdbcServiceConfigParser {

   public Map<String, JdbcServiceConfig> parse(String source) {
      Map<String, JdbcServiceConfig> toReturn = new LinkedHashMap<>();
      if (Strings.isValid(source)) {
         try {

            ObjectMapper OM = new ObjectMapper();
            OM.configure(Feature.ALLOW_SINGLE_QUOTES, true);
            JsonNode jNode = OM.readTree(source);

            Iterator<JsonNode> elements = jNode.elements();

            while (elements.hasNext()) {
               JdbcServiceConfig newConfig = asConfig(elements.next());
               if (!newConfig.isEmpty()) {
                  checkId(newConfig);

                  JdbcServiceConfig oldConfig = toReturn.put(newConfig.getId(), newConfig);

                  checkUnique(oldConfig, newConfig);
               }
            }
         } catch (IOException ex) {
            throw JdbcException.newJdbcException(ex, "Error parsing jdbc config [%s]", source);
         }
      }
      return toReturn;
   }

   private JdbcServiceConfig asConfig(JsonNode object) throws IOException {

      JdbcServiceConfig toReturn = new JdbcServiceConfig();
      ObjectMapper OM = new ObjectMapper();
      JsonNode newNode = OM.readTree(object.toString());
      Iterator<String> fields = newNode.fieldNames();
      Iterator<JsonNode> elements = newNode.elements();
      while (fields.hasNext()) {
         toReturn.put(fields.next(), elements.next().asText());
      }
      return toReturn;

   }

   private void checkId(JdbcServiceConfig config) {
      String id = config.getId();
      if (!Strings.isValid(id)) {
         throw newError("id cannot be null or empty - config[%s]", config);
      }
   }

   private void checkUnique(JdbcServiceConfig oldConfig, JdbcServiceConfig newConfig) {
      if (oldConfig != null) {
         throw newError("duplicate service id detected - id[%s]", newConfig.getId());
      }
   }

   private RuntimeException newError(String msg, Object... args) {
      return newJdbcException("Jdbc Service configuration error - " + msg, args);
   }
}
