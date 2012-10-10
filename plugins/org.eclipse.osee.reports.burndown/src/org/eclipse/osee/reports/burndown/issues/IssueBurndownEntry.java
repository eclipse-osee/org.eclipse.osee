/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.reports.burndown.issues;

import java.util.Calendar;
import java.util.Date;

/**
 * Model class representing a single entry for a given date.
 * 
 * @author Praveen Joseph
 */
public class IssueBurndownEntry {

  private final Date date;
  private int issuesClosed;
  private int issuesRemaining;
  private double idealIssuesRemaining;
  private double burndownRate;
  private boolean actualData;

  /**
   * @param date sets the current date
   */
  public IssueBurndownEntry(final Date date) {
    this.date = date;
    Date currentTime = Calendar.getInstance().getTime();
    if (currentTime.after(date)) {
      this.actualData = true;
    }
    else {
      this.actualData = false;
    }
  }

  /**
   * @return the number of issues closed
   */
  public int getIssuesClosed() {
    return this.issuesClosed;
  }

  /**
   * @param issuesClosed : sets the number of issues closed
   */
  public void setIssuesClosed(final int issuesClosed) {
    this.issuesClosed = issuesClosed;
  }

  /**
   * @return the number of issues remaining
   */
  public int getIssuesRemaining() {
    return this.issuesRemaining;
  }

  /**
   * @param issuesRemaining : sets the number of issues remaining
   */
  public void setIssuesRemaining(final int issuesRemaining) {
    this.issuesRemaining = issuesRemaining;
  }

  /**
   * @return the number of ideal issues remaining
   */
  public double getIdealIssuesRemaining() {
    return this.idealIssuesRemaining;
  }

  /**
   * @param idealIssuesRemaining : sets the ideal number of issues remaining
   */
  public void setIdealIssuesRemaining(final double idealIssuesRemaining) {
    this.idealIssuesRemaining = idealIssuesRemaining;
  }

  /**
   * @return the burndownRate
   */
  public double getBurndownRate() {
    return this.burndownRate;
  }

  /**
   * @param requiredBurndown : sets the Burndownrate
   */
  public void setBurndownRate(final double requiredBurndown) {
    this.burndownRate = requiredBurndown;
  }

  /**
   * @return the actualData
   */
  public boolean isActualData() {
    return this.actualData;
  }

  /**
   * @param actualData : sets the actual data
   */
  public void setActualData(final boolean actualData) {
    this.actualData = actualData;
  }

  /**
   * @return the current date
   */
  public Date getDate() {
    return this.date;
  }

}
