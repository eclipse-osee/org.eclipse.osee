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
package org.eclipse.osee.define.report.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Angel Avila
 */
@XmlRootElement
public class DataRightResult {
   private static final String NEW_PAGE_TEMPLATE =
      "<w:p><w:pPr><w:spacing w:after=\"0\"/><w:sectPr>%s</w:sectPr></w:pPr></w:p>";
   private static final String SAME_PAGE_TEMPLATE = "<w:sectPr>%s</w:sectPr>";

   @XmlTransient
   private List<DataRightAnchor> dataRightAnchors;

   @XmlTransient
   private List<DataRight> dataRights;

   private Map<DataRightId, DataRight> dataRightIdToDataRight;

   private Map<String, DataRightAnchor> guidToAnchor;

   @XmlElement
   public Collection<DataRightAnchor> getDataRightAnchors() {
      if (dataRightAnchors == null) {
         dataRightAnchors = new ArrayList<DataRightAnchor>();
      }
      return dataRightAnchors;
   }

   public void setDataRightAnchors(List<DataRightAnchor> dataRightAnchors) {
      this.dataRightAnchors = dataRightAnchors;
   }

   @XmlElement
   public Collection<DataRight> getDataRights() {
      if (dataRights == null) {
         dataRights = new ArrayList<DataRight>();
      }
      return dataRights;
   }

   public void setDataRights(List<DataRight> dataRights) {
      this.dataRights = dataRights;
   }

   public String getContent(String guid, PageOrientation orientation) {

      checkInitialized();
      String toReturn;

      DataRightAnchor anchor = guidToAnchor.get(guid);
      DataRightId key = anchor.getDataRightId();
      boolean needsPageBreak = anchor.isNeedsPageBreak();
      boolean isNextDifferent = anchor.isNextDifferent();

      DataRight dataRight = dataRightIdToDataRight.get(key);
      String partialFooter = dataRight.getContent();

      if (orientation.isLandscape()) {
         toReturn = partialFooter;
      } else if (isNextDifferent || needsPageBreak) {
         if (needsPageBreak) {
            toReturn = String.format(NEW_PAGE_TEMPLATE, partialFooter);
         } else {
            toReturn = String.format(SAME_PAGE_TEMPLATE, partialFooter);
         }
      } else {
         toReturn = "";
      }

      return toReturn;
   }

   public void reset() {
      dataRightIdToDataRight = null;
      guidToAnchor = null;
   }

   private void checkInitialized() {
      if (dataRightIdToDataRight == null || guidToAnchor == null) {
         dataRightIdToDataRight = new HashMap<DataRightId, DataRight>();
         guidToAnchor = new HashMap<String, DataRightAnchor>();
         for (DataRightAnchor anchor : dataRightAnchors) {
            guidToAnchor.put(anchor.getId(), anchor);
         }

         for (DataRight dataRight : dataRights) {
            dataRightIdToDataRight.put(dataRight.getId(), dataRight);
         }
      }
   }
}