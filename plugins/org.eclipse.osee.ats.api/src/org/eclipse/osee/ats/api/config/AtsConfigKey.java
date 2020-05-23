/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.config;

/**
 * @author Donald G. Dunne
 */
public enum AtsConfigKey {

   AJaxBasePath("Controls what is put on front of /ajax/... paths in html files when they are snapshots or save to file."),
   // Remove after 26.0 and convert peer defects guid tag to id; convert guid values to long (if want)
   PeerDefectAsGuid("True if store id as guid tag");

   private final String description;

   private AtsConfigKey(String description) {
      this.description = description;
   }

   public String getDescription() {
      return description;
   }

}
