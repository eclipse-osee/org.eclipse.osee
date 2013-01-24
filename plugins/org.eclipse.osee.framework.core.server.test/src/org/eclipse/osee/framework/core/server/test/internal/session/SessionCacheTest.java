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
package org.eclipse.osee.framework.core.server.test.internal.session;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.server.internal.session.Cache;
import org.eclipse.osee.framework.core.server.internal.session.CacheFactory;
import org.eclipse.osee.framework.core.server.internal.session.ReadDataAccessor;
import org.eclipse.osee.framework.core.server.internal.session.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link SessionCacheTest}
 * 
 * @author Roberto E. Escobar
 */
public class SessionCacheTest {

   private static Cache<String, Session> cache;

   @Mock
   private ReadDataAccessor<String, Session> lda;
   @Captor
   private ArgumentCaptor<Iterable<? extends String>> idCaptor;

   @Before
   public void initMocks() {
      MockitoAnnotations.initMocks(this);

      CacheFactory factory = new CacheFactory();
      cache = factory.create(lda);
   }

   @Test
   public void testGet() throws OseeCoreException {
      Session s = mock(Session.class);
      when(s.getGuid()).thenReturn("id1");
      when(lda.load("id1")).thenReturn(s);

      Session toCheck = cache.get("id1");

      assertEquals(s, toCheck);
      verify(lda).load("id1");
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testGetAll() throws OseeCoreException {
      List<String> allIds = new LinkedList<String>();
      allIds.add("id1");
      doReturn(allIds).when(lda).getAllKeys();
      Map<String, Session> map = new HashMap<String, Session>();
      Session session = mock(Session.class);
      map.put("id1", session);
      when(lda.load(anyList())).thenReturn(map);
      Iterable<Session> all = cache.getAll();
      verify(lda).getAllKeys();
      verify(lda).load(idCaptor.capture());
      assertEquals("id1", idCaptor.getValue().iterator().next());
      assertEquals(session, all.iterator().next());
   }

   @Test
   public void testReloadCache() throws OseeCoreException {
      Session s1 = mock(Session.class);
      Session s2 = mock(Session.class);
      when(s1.getGuid()).thenReturn("id1");
      when(s1.getGuid()).thenReturn("id1");

      when(lda.load("id1")).thenReturn(s1, s2);

      Session toCheck = cache.get("id1");
      assertEquals(s1, toCheck);

      cache.invalidateAll();
      toCheck = cache.get("id1");
      assertEquals(s2, toCheck);
   }
}
