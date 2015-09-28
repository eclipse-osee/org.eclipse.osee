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

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class AccountPreferencesInput {

   private Map<String, String> preferences;

   public Map<String, String> getMap() {
      if (preferences == null) {
         preferences = new HashMap<>();
      }
      return preferences;
   }

   public void setMap(Map<String, String> preferences) {
      this.preferences = preferences;
   }

}
