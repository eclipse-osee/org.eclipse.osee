/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal.importer;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author Dominic Guss
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
