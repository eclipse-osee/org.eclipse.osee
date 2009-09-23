/*
 * Created on Sep 17, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Donald G. Dunne
 */
public class CoverageImport {

   private final String guid = GUID.create();
   private final Date runDate;
   private List<CoverageUnit> coverageUnits = new ArrayList<CoverageUnit>();

   public CoverageImport() {
      this(new Date());
   }

   public CoverageImport(Date runDate) {
      super();
      this.runDate = runDate;
   }

   public void addCoverageUnit(CoverageUnit coverageUnit) {
      coverageUnits.add(coverageUnit);
   }

   public List<CoverageUnit> getCoverageUnits() {
      return coverageUnits;
   }

   public String getGuid() {
      return guid;
   }

   public Date getRunDate() {
      return runDate;
   }

   public void setCoverageUnits(List<CoverageUnit> coverageUnits) {
      this.coverageUnits = coverageUnits;
   }
}
