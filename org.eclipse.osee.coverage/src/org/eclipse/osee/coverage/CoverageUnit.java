/*
 * Created on Sep 17, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class CoverageUnit {

   private String name;
   private String guid;
   private String location;
   private String previewHtml;
   private List<CoverageItem> items = new ArrayList<CoverageItem>();

}
