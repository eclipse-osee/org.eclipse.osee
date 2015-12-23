/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.internal;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.navigate.IAtsNavigateItem;
import org.eclipse.osee.ats.reports.AtsReport;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;

/**
 * Class to create the navigate items
 * 
 * @author Praveen Joseph
 */
public class ProjectModuleReports implements IAtsNavigateItem {

   private static final String EXTENSION_POINT_ID = "org.eclipse.osee.ats.reports.AtsReportItem";
   private static final String CLASSNAME = "classname";
   private static final String EXTENSION_POINT_NAME = "AtsReportItem";
   private static final String PROJECT_REPORTS = "Project Reports";

   @Override
   public List<XNavigateItem> getNavigateItems(final XNavigateItem parentItem) {
      XNavigateItem parent = new XNavigateItem(parentItem, PROJECT_REPORTS, AtsImage.REPORT);
      ExtensionDefinedObjects<AtsReport<?, ?>> atsReports =
         new ExtensionDefinedObjects<AtsReport<?, ?>>(EXTENSION_POINT_ID, EXTENSION_POINT_NAME, CLASSNAME, true);
      for (AtsReport<?, ?> reportItem : atsReports.getObjects()) {
         createReportItem(parent, reportItem);
      }
      return Collections.singletonList(parent);
   }

   private <IN, OUT> XNavigateItem createReportItem(XNavigateItem parent, AtsReport<IN, OUT> atsReport) {
      return new AtsReportXNavigateItemAction<IN, OUT>(parent, atsReport);
   }

}
