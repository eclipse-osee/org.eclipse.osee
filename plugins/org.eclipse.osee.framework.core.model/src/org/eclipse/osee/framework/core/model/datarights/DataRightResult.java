/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.datarights;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.util.PageOrientation;
import org.eclipse.osee.framework.core.util.ReportConstants;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Angel Avila
 */
public class DataRightResult {

   public static final String UNSPECIFIED =
      "<w:ftr w:type=\"odd\"><w:p><w:pPr><w:pStyle w:val=\"para8pt\"/><w:jc w:val=\"center\"/></w:pPr><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"/></w:rPr><w:instrText> PAGE </w:instrText></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"/></w:rPr><w:fldChar w:fldCharType=\"separate\"/></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"/><w:noProof/></w:rPr><w:t>5</w:t></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"/></w:rPr><w:fldChar w:fldCharType=\"end\"/></w:r></w:p><w:p><w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr><w:t>UNSPECIFIED - PLEASE TAG WITH CORRECT DATA RIGHTS ATTRIBUTE!!!</w:t></w:r></w:p><w:p><w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr><w:t>Contract No.: </w:t></w:r></w:p><w:p><w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr><w:t>Contractor Name: The Boeing Company</w:t></w:r></w:p><w:p><w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr><w:t>Contractor Address: 5000 E. McDowell Road; Mesa, AZ 85215-9797 </w:t></w:r></w:p><w:p><w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr><w:t></w:t></w:r></w:p><w:p><w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr></w:p><w:p><w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr><w:t>The Government's rights to use, modify, reproduce, release, perform, display, or disclose this software are restricted by paragraph (b)(3) of the Rights in Noncommercial Computer Software and Noncommercial Computer Software Documentation clause contained in the above identified contract.  Any reproduction of computer software or portions thereof marked with this legend must also reproduce the markings.  Any person, other than the Government, who has been provided access to such software must promptly notify the above named Contractor. </w:t></w:r></w:p><w:p><w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr></w:p><w:p><w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr><w:t>Copyright (c) 2017 – The Boeing Company</w:t></w:r></w:p></w:ftr>";

   private List<DataRightAnchor> dataRightAnchors;
   private List<DataRight> dataRights;
   private Map<ArtifactId, DataRightAnchor> idToAnchor;

   public Collection<DataRightAnchor> getDataRightAnchors() {
      if (dataRightAnchors == null) {
         dataRightAnchors = new ArrayList<>();
      }
      return dataRightAnchors;
   }

   public void setDataRightAnchors(List<DataRightAnchor> dataRightAnchors) {
      this.dataRightAnchors = dataRightAnchors;
   }

   public Collection<DataRight> getDataRights() {
      if (dataRights == null) {
         dataRights = new ArrayList<>();
      }
      return dataRights;
   }

   public void setDataRights(List<DataRight> dataRights) {
      this.dataRights = dataRights;
   }

   public String getContent(ArtifactId id, PageOrientation orientation) {

      checkInitialized();
      String footer = null;

      // account for orientation
      String portrait = String.format(ReportConstants.PAGE_ADDS, ReportConstants.PORTRAIT_ORIENT);
      String landscape = String.format(ReportConstants.PAGE_ADDS, ReportConstants.LANDSCAPE_ORIENT);
      String page_adds = orientation.isLandscape() ? landscape : portrait;

      DataRightAnchor anchor = idToAnchor.get(id);
      if (anchor != null) {
         boolean isSetDataRightFooter = anchor.isSetDataRightFooter();
         boolean isContinuous = anchor.isContinuous();
         if (isSetDataRightFooter) {
            // set footer section since next footer differs
            DataRight dataRight = anchor.getDataRight();
            if (dataRight != null) {
               footer = normalize(dataRight.getContent());
               footer = String.format(ReportConstants.NEW_PAGE_TEMPLATE, footer + page_adds);
            }
         } else if (!isContinuous) {
            // set page break since next footer differs;
            footer = String.format(ReportConstants.NEW_PAGE_TEMPLATE, page_adds);
         }
      }
      return Strings.isValid(footer) ? footer : "";
   }

   private String normalize(String partialFooter) {
      String toReturn = partialFooter;
      if (UNSPECIFIED.equals(toReturn)) {
         toReturn = String.format("<w:r><w:t>%s</w:t></w:r>", toReturn);
      }
      return toReturn;
   }

   public void reset() {
      idToAnchor = null;
   }

   private void checkInitialized() {
      if (idToAnchor == null) {
         idToAnchor = new HashMap<>();
         for (DataRightAnchor anchor : dataRightAnchors) {
            idToAnchor.put(anchor.getId(), anchor);
         }
      }
   }
}