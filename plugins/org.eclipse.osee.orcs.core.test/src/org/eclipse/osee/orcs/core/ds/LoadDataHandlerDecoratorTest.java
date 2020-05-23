/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.ds;

import static org.mockito.Mockito.verify;
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
    
   // @formatter:on

   private LoadDataHandlerDecorator decorated;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      decorated = new LoadDataHandlerDecorator(handler);

   }

   @Test
   public void testOnData() {
      decorated.onLoadStart();
      verify(handler).onLoadStart();

      decorated.onLoadDescription(description);
      verify(handler).onLoadDescription(description);

      decorated.onData(attrData, match);
      verify(handler).onData(attrData, match);

      decorated.onData(artData);
      verify(handler).onData(artData);

      decorated.onData(attrData);
      verify(handler).onData(attrData);

      decorated.onData(relData);
      verify(handler).onData(relData);

      decorated.onLoadEnd();
      verify(handler).onLoadEnd();
   }

}
