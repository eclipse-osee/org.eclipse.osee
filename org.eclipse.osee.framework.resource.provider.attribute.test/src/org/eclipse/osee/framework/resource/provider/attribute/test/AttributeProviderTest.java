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

package org.eclipse.osee.framework.resource.provider.attribute.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.resource.management.IResourceProvider;
import org.eclipse.osee.framework.resource.management.util.ResourceLocator;
import org.eclipse.osee.framework.resource.provider.attribute.AttributeProvider;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class AttributeProviderTest {

   @Test
   public void testGetSupportedProtocols() {
      IResourceProvider provider = new AttributeProvider();
      Collection<String> protocols = provider.getSupportedProtocols();
      Assert.assertEquals(1, protocols.size());
      Assert.assertEquals("attr", protocols.iterator().next());
   }

   @Test
   public void testIsValid() throws OseeCoreException, URISyntaxException {
      IResourceProvider provider = new AttributeProvider();
      Assert.assertFalse(provider.isValid(null));
      Assert.assertFalse(provider.isValid(new ResourceLocator(new URI("http://hello"))));
      Assert.assertTrue(provider.isValid(new ResourceLocator(new URI("attr://hello"))));
   }

   @Ignore
   @Test
   public void testOps() throws OseeCoreException, URISyntaxException {
      //      IResourceProvider provider = new AttributeProvider();
      Assert.fail("Implement Remaining Tests");
      //      provider.acquire(locator, options)
      //      provider.delete(locator)
      //      provider.exists(locator)
      //      provider.save(locator, resource, options)
   }
}
