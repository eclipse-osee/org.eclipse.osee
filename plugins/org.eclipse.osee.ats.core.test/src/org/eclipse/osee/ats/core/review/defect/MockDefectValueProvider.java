/*
 * Created on Jun 13, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.review.defect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.core.validator.IValueProvider;
import org.eclipse.osee.framework.jdk.core.util.AXml;

public class MockDefectValueProvider implements IValueProvider {

   private final List<ReviewDefectItem> defectItems;

   public MockDefectValueProvider(List<ReviewDefectItem> defectItems) {
      this.defectItems = defectItems;
   }

   @Override
   public String getName() {
      return "Defects";
   }

   @Override
   public boolean isEmpty() {
      return defectItems.isEmpty();
   }

   @Override
   public Collection<String> getValues() {
      List<String> values = new ArrayList<String>();
      for (ReviewDefectItem item : defectItems) {
         values.add(AXml.addTagData("Item", item.toXml()));
      }
      return values;
   }

   @Override
   public Collection<Date> getDateValues() {
      return null;
   }

}
