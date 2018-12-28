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
package org.eclipse.osee.jdbc.internal.osgi;

import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVICE__CONFIGS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVICE__ID;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVICE__OSGI_BINDING;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__CONNECTION_URI;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__CONNECTION_USERNAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.jdbc.JdbcException;
import org.eclipse.osee.jdbc.internal.osgi.JdbcComponentFactory.JdbcServiceComponent;
import org.eclipse.osee.logger.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

/**
 * Test Case for {@link JdbcComponentFactory}
 *
 * @author Roberto E. Escobar
 */
public class JdbcComponentFactoryTest {

   private static final String CONFIG_1 = //
      " {" + //
         "     'service.id':'1001'," + //
         "     'jdbc.client.db.uri': 'uri1'," + //
         "     'jdbc.client.db.username': 'user1'," + //
         "     'osgi.binding': [                 " + //
         "         'binding1'" + //
         "     ]" + //
         " }";

   private static final String CONFIG_2 = //
      " {" + //
         "     'service.id':'1002'," + //
         "     'jdbc.client.db.uri': 'uri2'," + //
         "     'jdbc.client.db.username': 'user2'," + //
         "     'osgi.binding': [" + //
         "         'binding2'" + //
         "     ]" + //
         " }";

   private static final String CONFIG_3 = //
      " {" + //
         "     'service.id':'1003'," + //
         "     'jdbc.client.db.uri': 'uri3'," + //
         "     'jdbc.client.db.username': 'user3'," + //
         "     'osgi.binding': [" + //
         "         'binding3'" + //
         "     ]" + //
         " }";

   private static final String CONFIG_ID_COLLISION = //
      " {" + //
         "     'service.id':'1001'," + //
         "     'jdbc.client.db.uri': 'uri4'," + //
         "     'jdbc.client.db.username': 'user4'," + //
         "     'osgi.binding': [                 " + //
         "         'binding4'" + //
         "     ]" + //
         " }";

   private static final String CONFIG_BINDING_COLLISION = //
      " {" + //
         "     'service.id':'1005'," + //
         "     'jdbc.client.db.uri': 'uri5'," + //
         "     'jdbc.client.db.username': 'user5'," + //
         "     'osgi.binding': [" + //
         "         'binding1'" + //
         "     ]" + //
         " }";

   private static final String CONFIG_ID_NULL = //
      " {" + //
         "     'jdbc.client.db.uri': 'uri6'," + //
         "     'jdbc.client.db.username': 'user6'," + //
         "     'osgi.binding': [                 " + //
         "         'binding6'" + //
         "     ]" + //
         " }";

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   //@formatter:off
   @Mock private Log logger;
   @Mock private ComponentFactory componentFactory;

   @Mock private ComponentInstance svcInstance;
   //@formatter:on

   private JdbcComponentFactory factory;

   @SuppressWarnings("unchecked")
   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      factory = new JdbcComponentFactory();
      factory.setComponentFactory(componentFactory);
      factory.setLogger(logger);

      when(componentFactory.newInstance(Matchers.any(Dictionary.class))).thenReturn(svcInstance);
   }

   @Test
   public void testStartStop() {
      factory.start(asConfig(CONFIG_1, CONFIG_2));

      Map<String, JdbcServiceComponent> services = factory.getServices();
      assertEquals(2, services.size());

      checkConfig(services.get("1001"), "1001", //
         map(JDBC_SERVICE__ID, "1001", //
            JDBC__CONNECTION_URI, "uri1", //
            JDBC__CONNECTION_USERNAME, "user1", JDBC_SERVICE__OSGI_BINDING, set("binding1")));

      checkConfig(services.get("1002"), "1002", //
         map(JDBC_SERVICE__ID, "1002", //
            JDBC__CONNECTION_URI, "uri2", //
            JDBC__CONNECTION_USERNAME, "user2", JDBC_SERVICE__OSGI_BINDING, set("binding2")));

      factory.stop();
      services = factory.getServices();
      assertEquals(0, services.size());
   }

   @Test
   public void testUpdate() {
      factory.start(asConfig(CONFIG_1, CONFIG_2));

      Map<String, JdbcServiceComponent> services = factory.getServices();
      assertEquals(2, services.size());

      checkConfig(services.get("1001"), "1001", //
         map(JDBC_SERVICE__ID, "1001", //
            JDBC__CONNECTION_URI, "uri1", //
            JDBC__CONNECTION_USERNAME, "user1", JDBC_SERVICE__OSGI_BINDING, set("binding1")));

      checkConfig(services.get("1002"), "1002", //
         map(JDBC_SERVICE__ID, "1002", //
            JDBC__CONNECTION_URI, "uri2", //
            JDBC__CONNECTION_USERNAME, "user2", JDBC_SERVICE__OSGI_BINDING, set("binding2")));

      factory.update(asConfig(CONFIG_1, CONFIG_2, CONFIG_3));
      assertEquals(3, services.size());

      checkConfig(services.get("1001"), "1001", //
         map(JDBC_SERVICE__ID, "1001", //
            JDBC__CONNECTION_URI, "uri1", //
            JDBC__CONNECTION_USERNAME, "user1", JDBC_SERVICE__OSGI_BINDING, set("binding1")));

      checkConfig(services.get("1002"), "1002", //
         map(JDBC_SERVICE__ID, "1002", //
            JDBC__CONNECTION_URI, "uri2", //
            JDBC__CONNECTION_USERNAME, "user2", JDBC_SERVICE__OSGI_BINDING, set("binding2")));

      checkConfig(services.get("1003"), "1003", //
         map(JDBC_SERVICE__ID, "1003", //
            JDBC__CONNECTION_URI, "uri3", //
            JDBC__CONNECTION_USERNAME, "user3", JDBC_SERVICE__OSGI_BINDING, set("binding3")));

      factory.update(asConfig(CONFIG_1, CONFIG_3));
      assertEquals(2, services.size());

      checkConfig(services.get("1001"), "1001", //
         map(JDBC_SERVICE__ID, "1001", //
            JDBC__CONNECTION_URI, "uri1", //
            JDBC__CONNECTION_USERNAME, "user1", JDBC_SERVICE__OSGI_BINDING, set("binding1")));

      checkConfig(services.get("1003"), "1003", //
         map(JDBC_SERVICE__ID, "1003", //
            JDBC__CONNECTION_URI, "uri3", //
            JDBC__CONNECTION_USERNAME, "user3", JDBC_SERVICE__OSGI_BINDING, set("binding3")));

      factory.stop();
      services = factory.getServices();
      assertEquals(0, services.size());
   }

   private static void checkConfig(JdbcServiceComponent comp, String id, Map<String, Object> expected) {
      assertEquals(id, comp.getId());
      Map<String, Object> data = comp.getConfig();
      assertEquals(false, Compare.isDifferent(expected, data));
   }

   @Test
   public void testStartConfigErrorNullId() {
      thrown.expect(JdbcException.class);
      thrown.expectMessage("Jdbc Service configuration error - id cannot be null or empty");
      factory.start(asConfig(CONFIG_1, CONFIG_ID_NULL));
   }

   @Test
   public void testStartConfigErrorIdCollision() {
      thrown.expect(JdbcException.class);
      thrown.expectMessage("Jdbc Service configuration error - duplicate service id detected - id[1001]");
      factory.start(asConfig(CONFIG_1, CONFIG_ID_COLLISION));
   }

   @Test
   public void testStartConfigErrorBindingCollision() {
      thrown.expect(JdbcException.class);
      thrown.expectMessage(
         "Jdbc Service configuration error - binding [binding1] should not be referenced multiple times betweeen [jdbc.service] configurations. Ensure [osgi.binding] contains unique bindings.");
      factory.start(asConfig(CONFIG_1, CONFIG_BINDING_COLLISION));
   }

   @Test
   public void testStartUpdateConfigErrorBindingCollision() {
      factory.start(asConfig(CONFIG_1));

      Map<String, JdbcServiceComponent> services = factory.getServices();
      assertEquals(1, services.size());

      thrown.expect(JdbcException.class);
      thrown.expectMessage(
         "Jdbc Service configuration error - binding [binding1] should not be referenced multiple times betweeen [jdbc.service] configurations. Ensure [osgi.binding] contains unique bindings.");
      factory.update(asConfig(CONFIG_1, CONFIG_BINDING_COLLISION));
   }

   private Set<String> set(String... vals) {
      return new TreeSet<>(Arrays.asList(vals));
   }

   private Map<String, Object> asConfig(String... configs) {
      StringBuilder builder = new StringBuilder("[");
      int size = configs.length;
      for (int index = 0; index < size; index++) {
         builder.append(configs[index]);
         if (index + 1 < size) {
            builder.append(",");
         }
      }
      builder.append("]");
      return map(JDBC_SERVICE__CONFIGS, builder.toString());
   }

   private static Map<String, Object> map(Object... keyVals) {
      Map<String, Object> data = new HashMap<>();
      String key = null;
      boolean isKey = true;
      for (Object keyVal : keyVals) {
         if (isKey) {
            key = String.valueOf(keyVal);
            isKey = false;
         } else {
            data.put(key, keyVal);
            isKey = true;
         }
      }
      return data;
   }
}
