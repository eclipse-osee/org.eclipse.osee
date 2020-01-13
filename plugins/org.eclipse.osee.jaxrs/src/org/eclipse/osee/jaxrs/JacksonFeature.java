/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs;

import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.core.util.JsonUtil;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class JacksonFeature implements Feature {

   private final static String JSON_FEATURE = JacksonFeature.class.getName();

   public static final String JAXRS_CONFIG_PREFIX = "jaxrs";
   public static final String JAXRS_CONFIG_PREFIX_AND_DOT = JAXRS_CONFIG_PREFIX + ".";
   public static final String JSON_FEATURE_KEY = JAXRS_CONFIG_PREFIX_AND_DOT + "jsonFeature";

   @Override
   public boolean configure(FeatureContext context) {
      boolean configured = false;
      Configuration config = context.getConfiguration();

      String propertyKey = getPropertyNameForRuntime(JSON_FEATURE_KEY, config.getRuntimeType());

      String jsonFeature = getJsonFeature(config, propertyKey, JSON_FEATURE);
      if (JSON_FEATURE.equalsIgnoreCase(jsonFeature)) {
         context.property(propertyKey, JSON_FEATURE);

         if (!config.isRegistered(JacksonJaxbJsonProvider.class)) {
            for (Object object : getProviders()) {
               context.register(object);
            }
         }
         configured = true;
      }
      return configured;
   }

   private String getJsonFeature(Configuration config, String propertyKey, String defaultValue) {
      String toReturn = defaultValue;
      Map<String, Object> props = config.getProperties();
      Object value = props != null ? props.get(propertyKey) : null;
      if (value != null) {
         toReturn = String.valueOf(value);
      }
      return toReturn;
   }

   public static String getPropertyNameForRuntime(String key, RuntimeType runtimeType) {
      if (runtimeType != null && key.startsWith(JAXRS_CONFIG_PREFIX)) {
         RuntimeType[] types = RuntimeType.values();
         for (RuntimeType type : types) {
            if (key.startsWith(JAXRS_CONFIG_PREFIX_AND_DOT + type.name().toLowerCase())) {
               return key;
            }
         }
         return key.replace(JAXRS_CONFIG_PREFIX, JAXRS_CONFIG_PREFIX_AND_DOT + runtimeType.name().toLowerCase());
      }
      return key;
   }

   public static List<? extends Object> getProviders() {
      List<Object> providers = new ArrayList<>();
      providers.add(new JacksonJaxbJsonProvider(JsonUtil.getMapper(), JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS));
      providers.add(JsonParseExceptionMapper.class);
      providers.add(JsonMappingExceptionMapper.class);
      return providers;
   }
}