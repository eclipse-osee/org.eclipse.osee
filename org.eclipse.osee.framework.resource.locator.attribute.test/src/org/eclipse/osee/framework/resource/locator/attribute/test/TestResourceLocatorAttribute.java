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
package org.eclipse.osee.framework.resource.locator.attribute.test;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.osee.framework.resource.locator.attribute.AttributeLocatorProvider;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;

/**
 * @author Roberto E. Escobar
 */
public class TestResourceLocatorAttribute extends TestCase {

   public void testCreateAttributeLocatorProvider() {
      assertNotNull(new AttributeLocatorProvider());
   }

   public void testIsValid() {
      AttributeLocatorProvider provider = new AttributeLocatorProvider();
      String[] data = new String[] {"a", "", null, "attr://", "attr"};
      boolean[] expected = new boolean[] {false, false, false, false, true};

      for (int index = 0; index < 0; index++) {
         boolean result = provider.isValid(data[index]);
         assertEquals(expected[index], result);
      }
   }

   private List<TestData> getTestGenerateLocatorData() {
      List<TestData> cases = new ArrayList<TestData>();
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

   public void testGenerateLocator() {
      AttributeLocatorProvider provider = new AttributeLocatorProvider();
      List<TestData> cases = getTestGenerateLocatorData();
      for (TestData data : cases) {
         IResourceLocator locator = null;
         try {
            locator = provider.generateResourceLocator(data.getSeed(), data.getName());
         } catch (MalformedLocatorException ex) {
            assertEquals(data.getId(), data.getShouldException(), true);
         }
         assertEquals(data.getId(), data.getExpected(), locator != null ? locator.getLocation().toASCIIString() : null);
      }
   }

   private List<TestData> getTestGetResourceLocatorData() {
      List<TestData> cases = new ArrayList<TestData>();
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

   public void testAcquireResourceAttributeLocator() {
      AttributeLocatorProvider provider = new AttributeLocatorProvider();
      List<TestData> cases = getTestGetResourceLocatorData();
      for (TestData data : cases) {
         IResourceLocator locator = null;
         try {
            locator = provider.getResourceLocator(data.getPath());
         } catch (MalformedLocatorException ex) {
            assertEquals(data.getId(), data.getShouldException(), true);
         }
         assertEquals(data.getId(), data.getExpected(), locator != null ? locator.getLocation().toASCIIString() : null);
      }
   }

   private final class TestData {
      private String id;
      private String seed;
      private String name;
      private String path;
      private boolean shouldException;
      private String expected;

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