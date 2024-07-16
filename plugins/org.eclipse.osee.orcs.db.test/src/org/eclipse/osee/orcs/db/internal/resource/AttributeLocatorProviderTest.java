/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.internal.resource;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;
import org.junit.Assert;

/**
 * Test Cases for {@link AttributeLocatorProvider}
 *
 * @author Roberto E. Escobar
 */
public class AttributeLocatorProviderTest {

   @org.junit.Test
   public void testCreateAttributeLocatorProvider() {
      Assert.assertNotNull(new AttributeLocatorProvider());
   }

   @org.junit.Test
   public void testIsValid() {
      AttributeLocatorProvider provider = new AttributeLocatorProvider();
      String[] data = new String[] {"a", "", null, "attr://", "attr"};
      boolean[] expected = new boolean[] {false, false, false, false, true};

      for (int index = 0; index < 5; index++) {
         boolean result = provider.isValid(data[index]);
         Assert.assertEquals(expected[index], result);
      }
   }

   private List<TestData> getTestGenerateLocatorData() {
      List<TestData> cases = new ArrayList<>();
      cases.add(new TestData("1", "", null, true, null));
      cases.add(new TestData("2", null, null, true, null));
      cases.add(new TestData("3", null, "", true, null));
      cases.add(new TestData("4", "", "", true, null));
      cases.add(new TestData("5", "123", "", true, null));
      cases.add(new TestData("6", "123", "hello.txt", false, "attr://123/hello.txt"));
      cases.add(new TestData("7", "1234", "hello.txt", false, "attr://123/4/hello.txt"));
      cases.add(new TestData("8", "1", "hello", false, "attr://1/hello"));
      return cases;
   }

   @org.junit.Test
   public void testGenerateLocator() {
      AttributeLocatorProvider provider = new AttributeLocatorProvider();
      List<TestData> cases = getTestGenerateLocatorData();
      for (TestData data : cases) {
         IResourceLocator locator = null;
         try {
            locator = provider.generateResourceLocator(data.getSeed(), data.getName());
         } catch (OseeCoreException ex) {
            Assert.assertTrue(ex instanceof MalformedLocatorException);
            Assert.assertEquals(data.getId(), data.getShouldException(), true);
         }
         Assert.assertEquals(data.getId(), data.getExpected(),
            locator != null ? locator.getLocation().toASCIIString() : null);
      }
   }

   private List<TestData> getTestGetResourceLocatorData() {
      List<TestData> cases = new ArrayList<>();
      cases.add(new TestData("1", "", true, null));
      cases.add(new TestData("2", null, true, null));
      cases.add(new TestData("3", "$%#", true, null));
      cases.add(new TestData("4", "x://", true, null));
      cases.add(new TestData("5", "x:1234/4", true, null));
      cases.add(new TestData("6", "attr:123", true, null));
      cases.add(new TestData("7", "attr://123", false, "attr://123"));
      cases.add(new TestData("8", "attr://123/hello.txt", false, "attr://123/hello.txt"));
      return cases;
   }

   @org.junit.Test
   public void testAcquireResourceAttributeLocator() {
      AttributeLocatorProvider provider = new AttributeLocatorProvider();
      List<TestData> cases = getTestGetResourceLocatorData();
      for (TestData data : cases) {
         IResourceLocator locator = null;
         try {
            locator = provider.getResourceLocator(data.getPath());
         } catch (OseeCoreException ex) {
            Assert.assertTrue(ex instanceof MalformedLocatorException);
            Assert.assertEquals(data.getId(), data.getShouldException(), true);
         }
         Assert.assertEquals(data.getId(), data.getExpected(),
            locator != null ? locator.getLocation().toASCIIString() : null);
      }
   }

   private final class TestData {
      private final String id;
      private String seed;
      private String name;
      private String path;
      private final boolean shouldException;
      private final String expected;

      public TestData(String id, String path, boolean shouldException, String expected) {
         super();
         this.id = id;
         this.path = path;
         this.shouldException = shouldException;
         this.expected = expected;
      }

      public TestData(String id, String seed, String name, boolean shouldException, String expected) {
         super();
         this.id = id;
         this.seed = seed;
         this.name = name;
         this.shouldException = shouldException;
         this.expected = expected;
      }

      public String getId() {
         return id;
      }

      public String getSeed() {
         return seed;
      }

      public String getName() {
         return name;
      }

      public String getPath() {
         return path;
      }

      public String getExpected() {
         return expected;
      }

      public boolean getShouldException() {
         return shouldException;
      }
   }
}