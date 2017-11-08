/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.program;

import org.eclipse.osee.ats.api.config.JaxNewAtsConfigObject;

/**
 * @author Donald G. Dunne
 */
public class JaxProgram extends JaxNewAtsConfigObject {

   long countryId;

   public long getCountryId() {
      return countryId;
   }

   public void setCountryId(long countryId) {
      this.countryId = countryId;
   }
}
