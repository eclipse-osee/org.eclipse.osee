/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.disposition.rest.internal.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Dominic A. Guss
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmzProperties {
   private String version_revision;
   private String version_lastModificationDate;

   public String getVersion_revision() {
      return version_revision;
   }

   public void setVersion_revision(String version_revision) {
      this.version_revision = version_revision;
   }

   public String getVersion_lastModificationDate() {
      return version_lastModificationDate;
   }

   public void setVersion_lastModificationDate(String version_lastModificationDate) {
      this.version_lastModificationDate = version_lastModificationDate;
   }
}
