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
package org.eclipse.osee.orcs.core.internal.proxy.impl;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Date;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for @{link AttributeReadOnlyImpl}
 *
 * @author Roberto E. Escobar
 */
public class AttributeReadOnlyImplTest {

   //@formatter:off
   @Mock private ExternalArtifactManager proxyManager;
   @Mock private OrcsSession session;
   @Mock private Attribute<Date> proxiedObject;

   @Mock private AttributeId attributeId;

   @Mock private Attribute<Object> attribute1;
   @Mock private AttributeReadable<Object> attributeReadable1;
   //@formatter:on

   private AttributeReadOnlyImpl<Date> readOnly;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      readOnly = new AttributeReadOnlyImpl<>(proxyManager, session, proxiedObject);
   }

   @Test
   public void testGetAttributeType()  {
      AttributeTypeToken expected = Name;
      when(proxiedObject.getAttributeType()).thenReturn(expected);

      assertEquals(expected, readOnly.getAttributeType());
      verify(proxiedObject).getAttributeType();
   }

   @Test
   public void testGetDisplayableString()  {
      String expected = "Hello";
      when(proxiedObject.getDisplayableString()).thenReturn(expected);

      String actual = readOnly.getDisplayableString();

      assertEquals(expected, actual);
      verify(proxiedObject).getDisplayableString();
   }

   @Test
   public void testGetGammaId() {
      long expected = 1231232112L;
      when(proxiedObject.getGammaId()).thenReturn(expected);

      long actual = readOnly.getGammaId();

      assertEquals(expected, actual);
      verify(proxiedObject).getGammaId();
   }

   @Test
   public void testGetModificationType() {
      ModificationType expected = ModificationType.ARTIFACT_DELETED;
      when(proxiedObject.getModificationType()).thenReturn(expected);

      ModificationType actual = readOnly.getModificationType();

      assertEquals(expected, actual);
      verify(proxiedObject).getModificationType();
   }

   @Test
   public void testGetValue()  {
      Date expected = new Date();
      when(proxiedObject.getValue()).thenReturn(expected);

      Date actual = readOnly.getValue();

      assertEquals(expected, actual);
      verify(proxiedObject).getValue();
   }

   @Test
   public void testIsDeleted() {
      boolean expected = true;
      when(proxiedObject.isDeleted()).thenReturn(expected);

      boolean actual = readOnly.isDeleted();

      assertEquals(expected, actual);
      verify(proxiedObject).isDeleted();
   }

   @Test
   public void testIsOfType()  {
      boolean expected = true;
      when(proxiedObject.isOfType(Name)).thenReturn(expected);

      boolean actual = readOnly.isOfType(Name);

      assertEquals(expected, actual);
      verify(proxiedObject).isOfType(Name);
   }
}