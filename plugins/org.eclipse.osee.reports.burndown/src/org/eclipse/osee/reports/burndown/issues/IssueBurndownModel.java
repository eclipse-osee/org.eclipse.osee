/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.reports.burndown.issues;

/**
 * Model class to store the IssueBurndownLog
 * 
 * @author Praveen Joseph
 */
public class IssueBurndownModel {

  private static IssueBurndownLog log;

  /**
   * @return the IssueBurndownLog
   */
  public static IssueBurndownLog getLog() {
    return log;
  }

  /**
   * @param log sets the IssueBurndownLog
   */
  public static void setLog(final IssueBurndownLog log) {
    IssueBurndownModel.log = log;
  }

}
