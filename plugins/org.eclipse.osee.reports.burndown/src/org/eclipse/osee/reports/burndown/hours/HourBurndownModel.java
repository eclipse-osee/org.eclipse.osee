/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.reports.burndown.hours;

/**
 * Model class to store the HourBurndownLog
 * 
 * @author Praveen Joseph
 */
public class HourBurndownModel {

  private static HourBurndownLog log;

  /**
   * @return the HourBurndownLog
   */
  public static HourBurndownLog getLog() {
    return log;
  }

  /**
   * @param log : sets the HourBurndownLog
   */
  public static void setLog(final HourBurndownLog log) {
    HourBurndownModel.log = log;
  }

}
