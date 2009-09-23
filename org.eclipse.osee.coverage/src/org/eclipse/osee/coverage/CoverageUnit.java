/*
 * Created on Sep 17, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Donald G. Dunne
 */
public class CoverageUnit {

   private String name;
   private final String guid = GUID.create();
   private String previewHtml;
   private final List<CoverageItem> coverageItems = new ArrayList<CoverageItem>();
   private String location;
   private final List<CoverageUnit> coverageUnits = new ArrayList<CoverageUnit>();

   public CoverageUnit(String name, String location) {
      super();
      this.name = name;
      this.location = location;
   }

   public void addCoverageUnit(CoverageUnit coverageUnit) {
      coverageUnits.add(coverageUnit);
   }

   public List<CoverageUnit> getCoverageUnits() {
      return coverageUnits;
   }

   public void addCoverageItem(CoverageItem coverageItem) {
      coverageItems.add(coverageItem);
   }

   public List<CoverageItem> getCoverageItems() {
      return coverageItems;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public String getPreviewHtml() {
      return previewHtml;
   }

   public void setPreviewHtml(String previewHtml) {
      this.previewHtml = previewHtml;
   }

   public String getGuid() {
      return guid;
   }

}
