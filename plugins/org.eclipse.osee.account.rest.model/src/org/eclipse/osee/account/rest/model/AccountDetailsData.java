/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.account.rest.model;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class AccountDetailsData extends AccountInfoData {

   private AccountPreferencesData preferences;

   public void setPreferences(AccountPreferencesData preferences) {
      this.preferences = preferences;
   }

   public AccountPreferencesData getPreferences() {
      if (preferences == null) {
         preferences = new AccountPreferencesData();
      }
      return preferences;
   }

}
