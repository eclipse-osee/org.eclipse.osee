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

package org.eclipse.osee.config.admin.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test Case for {@link ConfigManagerConfigurationBuilder}
 * 
 * @author Roberto E. Escobar
 */
public class ConfigManagerConfigurationBuilderTest {

   private static final String CONFIG_URI = "adsflkajdfajlsdaj";
   private static final long POLL_TIME = Long.MAX_VALUE;
   private static final TimeUnit POLL_TIME_UNIT = TimeUnit.MILLISECONDS;

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   private ConfigManagerConfigurationBuilder builder;

   @Before
   public void testSetup() {
      initMocks(this);

      builder = ConfigManagerConfigurationBuilder.newBuilder();
   }

   @Test
   public void testConfigUri() {
      builder.configUri(CONFIG_URI);

      ConfigManagerConfiguration actual = builder.build();

      assertEquals(CONFIG_URI, actual.getConfigUri());
   }

   @Test
   public void testAddSchemes() {
      builder.pollTime(POLL_TIME, POLL_TIME_UNIT);

      ConfigManagerConfiguration actual = builder.build();

      assertEquals(POLL_TIME, actual.getPollTime());
      assertEquals(POLL_TIME_UNIT, actual.getTimeUnit());
   }

   @Test
   public void testDefaultProperties() {
      String original = System.getProperty(ConfigManagerConstants.CONFIGURATION_URI, "");
      try {
         System.setProperty(ConfigManagerConstants.CONFIGURATION_URI, "helloConfig");

         Map<String, Object> properties = new HashMap<>();
         builder.properties(properties);

         ConfigManagerConfiguration actual = builder.build();

         assertEquals("helloConfig", actual.getConfigUri());
         assertEquals(ConfigManagerConstants.DEFAULT_POLL_TIME, actual.getPollTime());
         assertEquals(ConfigManagerConstants.DEFAULT_POLL_TIME_UNIT, actual.getTimeUnit());
      } finally {
         System.setProperty(ConfigManagerConstants.CONFIGURATION_URI, original);
      }
   }

   @Test
   public void testConfigProperties() {
      Map<String, Object> properties = new HashMap<>();
      properties.put(ConfigManagerConstants.CONFIGURATION_URI, CONFIG_URI);
      properties.put(ConfigManagerConstants.CONFIGURATION_POLL_TIME, POLL_TIME);
      properties.put(ConfigManagerConstants.CONFIGURATION_POLL_TIME_UNIT, POLL_TIME_UNIT);

      builder.properties(properties);

      ConfigManagerConfiguration actual = builder.build();

      assertEquals(CONFIG_URI, actual.getConfigUri());
      assertEquals(POLL_TIME, actual.getPollTime());
      assertEquals(POLL_TIME_UNIT, actual.getTimeUnit());
   }

   @Test
   public void testNoChangeAfterBuild() {
      builder.configUri(CONFIG_URI);
      builder.pollTime(POLL_TIME, POLL_TIME_UNIT);

      ConfigManagerConfiguration actual = builder.build();

      assertEquals(CONFIG_URI, actual.getConfigUri());
      assertEquals(POLL_TIME, actual.getPollTime());
      assertEquals(POLL_TIME_UNIT, actual.getTimeUnit());

      builder.configUri("adsfasdfafadffsfsa");
      builder.pollTime(35L, TimeUnit.HOURS);

      builder.build();

      assertEquals(CONFIG_URI, actual.getConfigUri());
      assertEquals(POLL_TIME, actual.getPollTime());
      assertEquals(POLL_TIME_UNIT, actual.getTimeUnit());
   }

}
