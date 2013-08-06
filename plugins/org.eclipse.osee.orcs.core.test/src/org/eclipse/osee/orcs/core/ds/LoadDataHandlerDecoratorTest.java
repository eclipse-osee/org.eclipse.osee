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
package org.eclipse.osee.orcs.core.ds;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link LoadDataHandlerForwarder}
 * 
 * @author Roberto E. Escobar
 */
public class LoadDataHandlerDecoratorTest {

   // @formatter:off
   @Mock private LoadDataHandler handler;
   
   @Mock private ArtifactData artData;
   @Mock private AttributeData attrData;
   @Mock private RelationData relData;
   @Mock private LoadDescription description;
   @Mock private MatchLocation match;   
   
   
   @Mock private OrcsDataHandler<ArtifactData> artDataHandler;
   @Mock private OrcsDataHandler<AttributeData> attrDataHandler;
   @Mock private OrcsDataHandler<RelationData> relDataHandler;   
   // @formatter:on

   private LoadDataHandlerDecorator decorated;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      decorated = new LoadDataHandlerDecorator(handler);

      when(handler.getArtifactDataHandler()).thenReturn(artDataHandler);
      when(handler.getAttributeDataHandler()).thenReturn(attrDataHandler);
      when(handler.getRelationDataHandler()).thenReturn(relDataHandler);
   }

   @Test
   public void testOnData() throws OseeCoreException {
      decorated.onLoadStart();
      verify(handler).onLoadStart();

      decorated.onLoadDescription(description);
      verify(handler).onLoadDescription(description);

      decorated.onData(attrData, match);
      verify(handler).onData(attrData, match);

      decorated.onData(artData);
      verify(artDataHandler).onData(artData);

      decorated.onData(attrData);
      verify(attrDataHandler).onData(attrData);

      decorated.onData(relData);
      verify(relDataHandler).onData(relData);

      decorated.onLoadEnd();
      verify(handler).onLoadEnd();
   }

}
