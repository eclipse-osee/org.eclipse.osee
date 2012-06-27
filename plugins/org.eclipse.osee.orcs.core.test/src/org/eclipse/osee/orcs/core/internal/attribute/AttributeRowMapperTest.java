/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.attribute;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.internal.artifact.AttributeManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link AttributeRowMapper}
 * 
 * @author Roberto E. Escobar
 */
public class AttributeRowMapperTest {

   // @formatter:off
   @Mock private Log logger;
   @Mock private AttributeFactory factory;
   @Mock private Map<Integer, ? extends AttributeManager> attributeContainers;
   @Mock private AttributeData data;
   @Mock private AttributeManager manager;
   // @formatter:on

   private AttributeRowMapper mapper;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);

      mapper = new AttributeRowMapper(logger, factory, attributeContainers);
   }

   @Test
   public void testOnDataValid() throws OseeCoreException {
      when(data.getArtifactId()).thenReturn(45);
      when(attributeContainers.get(45)).thenAnswer(new Answer<AttributeManager>() {

         @Override
         public AttributeManager answer(InvocationOnMock invocation) throws Throwable {
            return manager;
         }
      });

      mapper.onData(data);

      verify(attributeContainers).get(45);
      verify(factory).createAttribute(manager, data);
      verify(logger, times(0)).warn("");
   }

   @Test
   public void testOnDataNotFound() throws OseeCoreException {
      when(data.getArtifactId()).thenReturn(45);
      when(attributeContainers.get(45)).thenReturn(null);

      mapper.onData(data);

      verify(attributeContainers).get(45);
      verify(factory, times(0)).createAttribute(manager, data);
      verify(logger).warn("Orphaned attribute detected - [%s]", data);
   }
}
