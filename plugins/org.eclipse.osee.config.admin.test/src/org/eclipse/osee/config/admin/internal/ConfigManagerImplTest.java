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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import org.eclipse.osee.logger.Log;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author John Misinco
 */
public class ConfigManagerImplTest {

   @Mock
   private ConfigurationAdmin configAdmin;
   @Mock
   private Configuration configuration;
   @Mock
   private Log logger;

   private ConfigManagerImpl configManager;

   @Before
   public void testSetup() {
      initMocks(this);
      configManager = new ConfigManagerImpl();
      configManager.setConfigAdmin(configAdmin);
      configManager.setLogger(logger);
   }

   @Test
   public void testWriteWithChanges() throws IOException {
      Dictionary<String, Object> currentProps = new Hashtable<String, Object>();
      currentProps.put("prop1", "value1");

      Dictionary<String, Object> newProps = new Hashtable<String, Object>();
      newProps.put("prop1", "value1");
      newProps.put("prop2", "value2");

      when(configAdmin.getConfiguration("service1", null)).thenReturn(configuration);
      when(configuration.getProperties()).thenReturn(currentProps);
      configManager.write("service1", newProps);

      verify(configuration).update(newProps);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testWriteWithOutChanges() throws IOException {
      Dictionary<String, Object> currentProps = new Hashtable<String, Object>();
      currentProps.put("prop1", "value1");
      currentProps.put("prop2", "value2");

      Dictionary<String, Object> newProps = new Hashtable<String, Object>();
      newProps.put("prop1", "value1");
      newProps.put("prop2", "value2");

      when(configAdmin.getConfiguration("service1", null)).thenReturn(configuration);
      when(configuration.getProperties()).thenReturn(currentProps);
      configManager.write("service1", newProps);

      verify(configuration, times(0)).update(any(Dictionary.class));
   }
}
