/*********************************************************************
 * Copyright (c) 2021 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.reqif.export;

/**
 * @author Manjunath Sangappa
 */
public class WordMLContent {

  private String inputString;

  /**
   * @return the inputString
   */
  public String getInputString() {
    return this.inputString;
  }

  /**
   * @param inputString the inputString to set
   */
  public void setInputString(final String inputString) {
    this.inputString = inputString;
  }

  /**
   * @return the bold
   */
  public boolean isBold() {
    return this.bold;
  }

  /**
   * @param bold the bold to set
   */
  public void setBold(final boolean bold) {
    this.bold = bold;
  }

  /**
   * @return the italic
   */
  public boolean isItalic() {
    return this.italic;
  }

  /**
   * @param italic the italic to set
   */
  public void setItalic(final boolean italic) {
    this.italic = italic;
  }

  private boolean bold;
  private boolean italic;

}