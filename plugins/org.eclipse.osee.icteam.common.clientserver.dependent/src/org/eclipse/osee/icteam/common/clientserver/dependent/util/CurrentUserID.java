/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
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
package org.eclipse.osee.icteam.common.clientserver.dependent.util;


/**
 * then class is to get get and set current userID
 * 
 * @author Ajay Chandrahasan
 */
public class CurrentUserID {

  private String currentUserId;


  /**
   * @return the currentUserId
   */
  public String getCurrentUserId() {
    return this.currentUserId != null ? this.currentUserId.toLowerCase() : this.currentUserId;
  }

  public void setCurrentUserId(final String currentUserId) {
    this.currentUserId = currentUserId;
  }

  /**
   * @return the currentLoggedInUser
   */
  public String getCurrentLoggedInUser() {
    return getCurrentUserId();
  }
}
