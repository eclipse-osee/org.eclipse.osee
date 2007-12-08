/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize;

import org.eclipse.osee.framework.jdk.core.util.AXml;

/**
 * @author Donald G. Dunne
 */
public class FilterData {

   private String filterText = "";
   private static final String FILTER_TAG = "xFilter";

   /**
    * @return the filterText
    */
   public String getFilterText() {
      return filterText;
   }

   /**
    * @param filterText the filterText to set
    */
   public void setFilterText(String filterText) {
      this.filterText = filterText;
   }

   public String getXml() {
      return AXml.addTagData(FILTER_TAG, filterText);
   }

   public void setFromXml(String xml) {
      filterText = AXml.getTagData(xml, FILTER_TAG);
   }
}
