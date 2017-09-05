/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

/**
 * @author Donald G. Dunne
 */
public enum AtsConfigKey {

   AJaxBasePath("Controls what is put on front of /ajax/... paths in html files when they are snapshots or save to file.");

   private final String description;

   private AtsConfigKey(String description) {
      this.description = description;
   }

   public String getDescription() {
      return description;
   }

}
