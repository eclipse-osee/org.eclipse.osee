/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.reports.burndown.hours;

import org.eclipse.osee.reports.burndown.ui.AbstractBurndownTab;

/**
 * Class that represents the UI Component
 * 
 * @author Praveen Joseph
 */
public class HourBurndownTab extends AbstractBurndownTab {

   @Override
   public String getTabName() {
      return "Hour Burndown";
   }

   @Override
   public String getReport() {
      return "HourBurndown.rptdesign";
   }

}
