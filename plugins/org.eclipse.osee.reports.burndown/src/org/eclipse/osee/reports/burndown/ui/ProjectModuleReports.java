/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.reports.burndown.ui;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.navigate.IAtsNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.reports.efficiency.ui.EfficiencyItem;
import org.eclipse.osee.reports.split.ui.DistributionItem;

/**
 * Class to create the navigate items
 * 
 * @author Praveen Joseph
 */
public class ProjectModuleReports implements IAtsNavigateItem {

   /**
    * Default Constructor
    */
   public ProjectModuleReports() {
      //
   }

   @Override
   public List<XNavigateItem> getNavigateItems(final XNavigateItem parentItem) {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();

      XNavigateItem autoseeReportItem = new XNavigateItem(parentItem, "ProjectModule Reports", AtsImage.REPORT);
      new BurndownItem(autoseeReportItem);
      new DistributionItem(autoseeReportItem);
      new EfficiencyItem(autoseeReportItem);
      items.add(autoseeReportItem);
      return items;
   }

}
