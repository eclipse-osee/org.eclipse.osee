/*
 * Created on May 30, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Donald G. Dunne
 */
public class XOptionHandler {

   private Set<XOption> xOptions = new HashSet<XOption>();

   public XOptionHandler(XOption... xOption) {
      set(xOption);
   }

   public static Collection<XOption> getCollection(XOption... ats) {
      Set<XOption> items = new HashSet<XOption>();
      for (XOption item : ats) {
         items.add(item);
      }
      return items;
   }

   public void add(XOption xOption) {
      if (xOption.name().startsWith("ALIGN_")) {
         xOptions.remove(XOption.ALIGN_CENTER);
         xOptions.remove(XOption.ALIGN_LEFT);
         xOptions.remove(XOption.ALIGN_RIGHT);
      } else if (xOption == XOption.HORIZONTAL_LABEL) {
         xOptions.remove(XOption.VERTICAL_LABEL);
      } else if (xOption == XOption.EDITABLE) {
         xOptions.remove(XOption.NOT_EDITABLE);
      } else if (xOption == XOption.NOT_EDITABLE) {
         xOptions.remove(XOption.EDITABLE);
      } else if (xOption == XOption.NOT_REQUIRED) {
         xOptions.remove(XOption.REQUIRED);
      } else if (xOption == XOption.REQUIRED) {
         xOptions.remove(XOption.NOT_REQUIRED);
      } else if (xOption == XOption.NOT_ENABLED) {
         xOptions.remove(XOption.ENABLED);
      } else if (xOption == XOption.ENABLED) {
         xOptions.remove(XOption.NOT_ENABLED);
      } else if (xOption == XOption.FILL_NONE) {
         xOptions.remove(XOption.FILL_HORIZONTALLY);
         xOptions.remove(XOption.FILL_VERTICALLY);
      } else if (xOption == XOption.VERTICAL_LABEL) {
         xOptions.remove(XOption.HORIZONTAL_LABEL);
      }
      xOptions.add(xOption);
   }

   public void add(XOption... xOption) {
      for (XOption xOpt : xOption) {
         add(xOpt);
      }
   }

   public void add(Collection<XOption> xOption) {
      for (XOption xOpt : xOption) {
         add(xOpt);
      }
   }

   public boolean contains(XOption xOption) {
      return xOptions.contains(xOption);
   }

   /**
    * @return the xOptions
    */
   public Set<XOption> getXOptions() {
      return xOptions;
   }

   /**
    * @param options the xOptions to set
    */
   public void set(Set<XOption> options) {
      this.xOptions.clear();
      // Must go through the add method to ensure values set properly
      for (XOption xOption : options) {
         add(xOption);
      }
   }

   /**
    * @param options the xOptions to set
    */
   public void set(XOption options[]) {
      this.xOptions.clear();
      // Must go through the add method to ensure values set properly
      for (XOption xOption : options) {
         add(xOption);
      }
   }

}
