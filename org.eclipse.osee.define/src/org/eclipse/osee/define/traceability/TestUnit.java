/*
 * Created on Apr 1, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.traceability;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.define.traceability.ITraceParser.TraceMark;

/**
 * @author Roberto E. Escobar
 */
public class TestUnit {
   private final String testUnitType;
   private final String name;
   private final Collection<TraceMark> traceMarks;

   public TestUnit(String testUnitType, String name) {
      this.name = name;
      this.testUnitType = testUnitType;
      this.traceMarks = new HashSet<TraceMark>();
   }

   public String getTestUnitType() {
      return testUnitType;
   }

   public String getName() {
      return name;
   }

   public void addAllTraceMarks(Collection<TraceMark> traceItems) {
      traceMarks.addAll(traceItems);
   }

   public void addTraceMark(TraceMark traceMark) {
      traceMarks.add(traceMark);
   }

   public Collection<TraceMark> getTraceMarks() {
      return traceMarks;
   }

   public Set<String> getTraceMarkTypes() {
      Set<String> toReturn = new HashSet<String>();
      for (TraceMark traceMark : traceMarks) {
         toReturn.add(traceMark.getTraceType());
      }
      return toReturn;
   }

   public Collection<TraceMark> getTraceMarksByType(String type) {
      Set<TraceMark> toReturn = new HashSet<TraceMark>();
      for (TraceMark traceMark : traceMarks) {
         if (traceMark.getTraceType().equalsIgnoreCase(type)) {
            toReturn.add(traceMark);
         }
      }
      return toReturn;
   }
}
