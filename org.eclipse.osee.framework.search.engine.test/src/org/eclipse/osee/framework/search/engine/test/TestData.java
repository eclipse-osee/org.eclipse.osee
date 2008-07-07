/*
 * Created on Jul 2, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.search.engine.test;

/**
 * @author Roberto E. Escobar
 */
public class TestData {
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
