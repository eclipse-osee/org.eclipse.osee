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
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Dictionary;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author John Misinco
 */
public class ConfigManagerImplTest {

   private static final String CONFIG_1 = //
      "{'config': [" + //
         " {" + //
         "     'service.pid':'1001'," + //
         "     'a': '1'," + //
         "     'b': '2'," + //
         "     'c': '3'" + //
         " }" + //
         "]}";

   private static final String CONFIG_1_WITH_CHANGE = //
      "{'config': [" + //
         " {" + //
         "     'service.pid':'1001'," + //
         "     'a': '1'," + //
         "     'c': '3'" + //
         " }" + //
         "]}";

   private static final String CONFIG_1_AND_2 = //
      "{'config': [" + //
         " {" + //
         "     'service.pid':'1001'," + //
         "     'a': '1'," + //
         "     'c': '3'" + //
         " }," + //
         " {" + //
         "     'service.pid':'1002'," + //
         "     'd': '4'," + //
         "     'e': '5'" + //
         " }" + //
         "]}";

   private static final String CONFIG_1_AND_2_WITH_CHANGE = //
      "{'config': [" + //
         " {" + //
         "     'service.pid':'1001'," + //
         "     'a': '1'," + //
         "     'c': '3'" + //
         " }," + //
         " {" + //
         "     'service.pid':'1002'," + //
         "     'e': '5'" + //
         " }" + //
         "]}";

   private static final String CONFIG_2_WITH_CHANGE = //
      "{'config': [" + //
         " {" + //
         "     'service.pid':'1002'," + //
         "     'e': '5'" + //
         " }" + //
         "]}";

   private static final String NO_CONFIG = //
      "{'config': []}";

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @Rule
   public TestName testName = new TestName();

   //@formatter:off
   @Mock private Log logger;
   @Mock private ConfigurationAdmin configAdmin;
   
   @Mock private Configuration configuration1;
   @Mock private Configuration configuration2;
   //@formatter:on

   @SuppressWarnings("rawtypes")
   private ArgumentCaptor<Dictionary> captor1;
   @SuppressWarnings("rawtypes")
   private ArgumentCaptor<Dictionary> captor2;

   private ConfigManagerImpl configManager;
   private File configFile;

   @Before
   public void setup() throws IOException {
      initMocks(this);

      configFile = folder.newFile(testName.getMethodName());

      configManager = new ConfigManagerImpl();
      configManager.setConfigAdmin(configAdmin);
      configManager.setLogger(logger);
   }

   private void resetMocks() throws IOException {
      reset(configuration1, configuration2);

      captor1 = ArgumentCaptor.forClass(Dictionary.class);
      captor2 = ArgumentCaptor.forClass(Dictionary.class);

      when(configAdmin.getConfiguration("1001", null)).thenReturn(configuration1);
      when(configAdmin.getConfiguration("1002", null)).thenReturn(configuration2);
   }

   private void notifyChanged(File configFile) {
      configManager.modificationDateChanged(Collections.singleton(configFile.toURI()));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testConfigChanges() throws IOException {
      // Write configuration 1
      resetMocks();
      writeConfig(configFile, CONFIG_1);
      notifyChanged(configFile);

      verify(configuration1, times(1)).update(captor1.capture());
      verify(configuration2, times(0)).update(captor2.capture());

      assertMap(captor1.getValue(), Constants.SERVICE_PID, "1001", "a", "1", "b", "2", "c", "3");

      // Make a change in configuration 1
      resetMocks();
      writeConfig(configFile, CONFIG_1_WITH_CHANGE);
      notifyChanged(configFile);

      verify(configuration1, times(1)).update(captor1.capture());
      verify(configuration2, times(0)).update(captor2.capture());

      assertMap(captor1.getValue(), Constants.SERVICE_PID, "1001", "a", "1", "c", "3");

      // Add configuration 2
      resetMocks();
      writeConfig(configFile, CONFIG_1_AND_2);
      notifyChanged(configFile);

      verify(configuration1, times(0)).update(captor1.capture());
      verify(configuration2, times(1)).update(captor2.capture());

      assertMap(captor2.getValue(), Constants.SERVICE_PID, "1002", "d", "4", "e", "5");

      // Change configuration 2
      resetMocks();
      writeConfig(configFile, CONFIG_1_AND_2_WITH_CHANGE);
      notifyChanged(configFile);

      verify(configuration1, times(0)).update(captor1.capture());
      verify(configuration2, times(1)).update(captor2.capture());

      assertMap(captor2.getValue(), Constants.SERVICE_PID, "1002", "e", "5");

      // Remove configuration 1
      resetMocks();
      writeConfig(configFile, CONFIG_2_WITH_CHANGE);
      notifyChanged(configFile);

      verify(configuration1, times(1)).delete();
      verify(configuration1, times(0)).update(captor1.capture());
      verify(configuration2, times(0)).update(captor2.capture());

      // Remove last configuration
      resetMocks();
      writeConfig(configFile, NO_CONFIG);
      notifyChanged(configFile);

      verify(configuration1, times(0)).delete();
      verify(configuration1, times(0)).update(captor1.capture());
      verify(configuration2, times(1)).delete();
      verify(configuration2, times(0)).update(captor2.capture());
   }

   @SuppressWarnings("rawtypes")
   private static void assertMap(Dictionary props, Object... values) {
      if (values.length == 0) {
         assertEquals(true, props.isEmpty());
      } else {
         int size = values.length / 2;
         assertEquals(size, props.size());

         Object key = null;
         for (int index = 0; index < values.length; index++) {
            if (key == null) {
               key = values[index];
            } else {
               assertEquals(values[index], props.get(key));
               key = null;
            }
         }
      }
   }

   private static void writeConfig(File file, String value) throws IOException {
      Writer writer = null;
      try {
         writer = new FileWriter(file);
         writer.write(value);
         writer.flush();
      } finally {
         Lib.close(writer);
      }
   }
}
