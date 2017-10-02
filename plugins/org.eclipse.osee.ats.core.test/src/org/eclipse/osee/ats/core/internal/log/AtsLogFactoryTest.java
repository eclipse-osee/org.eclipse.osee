/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.log;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AtsLogFactory}
 * 
 * @author Donald G. Dunne
 */
public class AtsLogFactoryTest {

   // @formatter:off
   @Mock IAtsWorkItem workItem;
   @Mock IAttributeResolver attrResolver;
   @Mock IAtsUser Joe;
   @Mock IAtsChangeSet changes;
   @Mock IAtsLog log;
   // @formatter:on

   @Before
   public void setup()  {
      MockitoAnnotations.initMocks(this);

      List<IAtsLogItem> items = new ArrayList<>();
      when(workItem.getLog()).thenReturn(log);
      when(log.getLogItems()).thenReturn(items);
   }

   @Test
   public void testGetLog() {
      AtsLogFactory factory = new AtsLogFactory();
      Assert.assertNotNull(factory.getLog());
   }

   @Test
   public void testGetLogLoaded() {
      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.Log, "")).thenReturn("");
      AtsLogFactory factory = new AtsLogFactory();
      factory.getLogLoaded(workItem, attrResolver);

      verify(attrResolver).getSoleAttributeValue(workItem, AtsAttributeTypes.Log, "");
   }

   @Test
   public void testNewLogItem() {
      AtsLogFactory factory = new AtsLogFactory();
      IAtsLogItem newLogItem = factory.newLogItem(LogType.Metrics, new Date(), Joe, "Analyze", "msg");
      Assert.assertNotNull(newLogItem);
      Assert.assertEquals("Analyze", newLogItem.getState());
   }

   @Test
   public void testWriteToStore() {
      when(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.Log, "")).thenReturn("");
      AtsLogFactory factory = new AtsLogFactory();
      factory.writeToStore(workItem, attrResolver, changes);

      verify(attrResolver).setSoleAttributeValue(workItem, AtsAttributeTypes.Log,
         "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><AtsLog/>", changes);
   }

   @Test
   public void testGetLogProvider() {
      AtsLogFactory factory = new AtsLogFactory();
      Assert.assertNotNull(factory.getLogProvider(workItem, attrResolver));
   }

}
