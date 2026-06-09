/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.orcs.rest.model;

/**
 * Model representing the site-wide banner configuration read from Global Preferences. If content is non-empty the
 * banner is shown; if empty/blank the banner is hidden.
 */
public class SiteBannerConfig {

   private String content;

   public SiteBannerConfig() {
      this.content = "";
   }

   public SiteBannerConfig(String content) {
      this.content = content;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }
}
