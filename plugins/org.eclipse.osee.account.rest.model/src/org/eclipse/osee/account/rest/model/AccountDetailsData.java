/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.account.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

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
