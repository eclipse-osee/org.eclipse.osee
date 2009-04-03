/*
 * Created on Apr 1, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.traceability;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

/**
 * @author Roberto E. Escobar
 */
public class TestUnit {
   private final String testUnitType;
   private final String name;
   private final HashCollection<String, String> traceMarkCollection;

   public TestUnit(String testUnitType, String name) {
      this.name = name;
      this.testUnitType = testUnitType;
      this.traceMarkCollection = new HashCollection<String, String>(false, HashSet.class);
   }

   public String getTestUnitType() {
      return testUnitType;
   }

   public String getName() {
      return name;
   }

   public void addAllTraceMarks(String type, Collection<String> traceMarks) {
      traceMarkCollection.put(type, traceMarks);
   }

   public void addTraceMark(String type, String traceMark) {
      traceMarkCollection.put(type, traceMark);
   }

   public Set<String> getTraceMarkTypes() {
      return traceMarkCollection.keySet();
   }

   public Collection<String> getTraceMarksByType(String type) {
      return traceMarkCollection.getValues(type);
   }
}
