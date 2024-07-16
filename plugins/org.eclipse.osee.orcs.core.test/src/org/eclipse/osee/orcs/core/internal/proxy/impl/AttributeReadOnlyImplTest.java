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

package org.eclipse.osee.orcs.core.internal.proxy.impl;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Date;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
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
   public void testGetAttributeType() {
      AttributeTypeToken expected = Name;
      when(proxiedObject.getAttributeType()).thenReturn(expected);

      assertEquals(expected, readOnly.getAttributeType());
      verify(proxiedObject).getAttributeType();
   }

   @Test
   public void testGetDisplayableString() {
      String expected = "Hello";
      when(proxiedObject.getDisplayableString()).thenReturn(expected);

      String actual = readOnly.getDisplayableString();

      assertEquals(expected, actual);
      verify(proxiedObject).getDisplayableString();
   }

   @Test
   public void testGetGammaId() {
      GammaId expected = GammaId.valueOf(1231232112L);
      when(proxiedObject.getGammaId()).thenReturn(expected);

      GammaId actual = readOnly.getGammaId();

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
   public void testGetValue() {
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
}