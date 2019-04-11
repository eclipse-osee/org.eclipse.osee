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

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.TransactionTokenDeserializer;
import org.eclipse.osee.framework.core.data.TransactionTokenSerializer;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.data.UserTokenDeserializer;
import org.eclipse.osee.framework.core.data.UserTokenSerializer;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdDeserializer;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;
import org.eclipse.osee.framework.jdk.core.type.NamedIdDeserializer;

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
      providers.add(JacksonFeature.newJacksonJsonProvider());
      providers.add(JsonParseExceptionMapper.class);
      providers.add(JsonMappingExceptionMapper.class);
      return providers;
   }

   private static JacksonJsonProvider newJacksonJsonProvider() {
      ObjectMapper objectMapper = new ObjectMapper();

      objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
      objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
      objectMapper.configure(SerializationFeature.WRAP_EXCEPTIONS, true);
      objectMapper.configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
      objectMapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
      objectMapper.configure(MapperFeature.AUTO_DETECT_GETTERS, true);
      objectMapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, true);
      objectMapper.configure(MapperFeature.USE_ANNOTATIONS, true);
      objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
      objectMapper.configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true);

      objectMapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
      objectMapper.configure(MapperFeature.AUTO_DETECT_SETTERS, true);
      objectMapper.configure(MapperFeature.AUTO_DETECT_CREATORS, true);
      objectMapper.configure(MapperFeature.USE_ANNOTATIONS, true);
      objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
      objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      SimpleModule module = new SimpleModule("OSEE", new Version(1, 0, 0, "", "", ""));

      module.addDeserializer(ApplicabilityToken.class, new NamedIdDeserializer<>(ApplicabilityToken::create));
      module.addDeserializer(ArtifactToken.class, new NamedIdDeserializer<ArtifactToken>(ArtifactToken::valueOf));
      module.addDeserializer(ArtifactId.class, new IdDeserializer<ArtifactId>(ArtifactId::valueOf));
      module.addDeserializer(TransactionToken.class, new TransactionTokenDeserializer());
      module.addSerializer(TransactionToken.class, new TransactionTokenSerializer());
      module.addDeserializer(UserToken.class, new UserTokenDeserializer());
      module.addSerializer(UserToken.class, new UserTokenSerializer());
      JsonSerializer<Id> idSerializer = new IdSerializer();
      module.addSerializer(TransactionId.class, idSerializer);
      module.addSerializer(BranchType.class, idSerializer);
      module.addSerializer(BranchState.class, idSerializer);
      module.addDeserializer(TransactionId.class, new IdDeserializer<TransactionId>(TransactionId::valueOf));

      objectMapper.registerModule(module);
      return new JacksonJaxbJsonProvider(objectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS);
   }
}