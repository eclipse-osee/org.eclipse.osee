/*
 * Created on Sep 17, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

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
   private final CoverageUnit parentCoverageUnit;

   public CoverageUnit(CoverageUnit parentCoverageUnit, String name, String location) {
      super();
      this.parentCoverageUnit = parentCoverageUnit;
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

   public List<CoverageItem> getCoverageItems(boolean recurse) {
      if (!recurse) {
         return coverageItems;
      }
      List<CoverageItem> items = new ArrayList<CoverageItem>(coverageItems);
      for (CoverageUnit coverageUnit : coverageUnits) {
         items.addAll(coverageUnit.getCoverageItems(true));
      }
      return items;
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

   public CoverageUnit getParentCoverageUnit() {
      return parentCoverageUnit;
   }

}
