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
package org.eclipse.osee.config.admin.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.Dictionary;
import java.util.Iterator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

/**
 * Test Case for {@link ConfigParser}
 * 
 * @author Roberto E. Escobar
 */
public class ConfigParserTest {

   private static final String VALID_CONFIG = //
      "{" + //
         "\"config\": " + //
         "  [" + //
         "     {" + //
         "        \"service.pid\": \"service-1\"," + //
         "        \"key1\": \"val1\"," + //
         "        \"key2\": \"val2\"" + //
         "     }," + //
         "     {" + //
         "        \"service.pid\": \"service-2\"," + //
         "        \"a\": \"34242\"," + //
         "        \"b\": \"hello\"" + //
         "     }," + //
         "     {" + //
         "        \"service.pid\": \"service-3\"" + //
         "     }" + //
         "  ]" + //
         "}";

   private static final String ERROR_CONFIG_1 = "{" + //
      "\"config\": [" + //
      "{";

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   //@formatter:off
   @Mock private ConfigWriter writer;
   @Captor private ArgumentCaptor<String> idCaptor;
   @Captor private ArgumentCaptor<Dictionary<String, Object>> valuesCaptor;
   //@formatter:on

   private final ConfigParser parser = new ConfigParser();

   @Before
   public void testSetup() {
      initMocks(this);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testEmpty() {
      parser.process(writer, "");

      verify(writer, times(0)).write(anyString(), any(Dictionary.class));
   }

   @Test
   public void testException() {
      thrown.expect(OseeCoreException.class);
      parser.process(writer, ERROR_CONFIG_1);
   }

   @Test
   public void testProcess() {
      parser.process(writer, VALID_CONFIG);

      verify(writer, times(3)).write(idCaptor.capture(), valuesCaptor.capture());

      Iterator<String> idIt = idCaptor.getAllValues().iterator();
      Iterator<Dictionary<String, Object>> valuesIt = valuesCaptor.getAllValues().iterator();

      assertEquals("service-1", idIt.next());
      assertValues(valuesIt.next(), "service.pid", "service-1", "key1", "val1", "key2", "val2");

      assertEquals("service-2", idIt.next());
      assertValues(valuesIt.next(), "service.pid", "service-2", "a", "34242", "b", "hello");

      assertEquals("service-3", idIt.next());
      assertValues(valuesIt.next(), "service.pid", "service-3");
   }

   private static void assertValues(Dictionary<String, Object> actual, String... expected) {
      int expectedSize = expected.length == 0 ? 0 : expected.length / 2;
      assertEquals(expectedSize, actual.size());

      for (int index = 0; index < expected.length; index++) {
         String key = expected[index];
         String value = expected[++index];

         assertEquals(value, actual.get(key));
      }
   }
}
