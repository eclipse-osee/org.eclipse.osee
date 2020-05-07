/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.server.application.internal.operations;

/**
 * @author Donald G. Dunne
 */
public class OseeResource {
   private String name;
   private String orgUrl;
   private String amsUrl;

   public OseeResource(String name, String amsUrl, String orgUrl) {
      this.name = name;
      this.orgUrl = orgUrl;
      this.amsUrl = amsUrl;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getOrgUrl() {
      return orgUrl;
   }

   public void setOrgUrl(String orgUrl) {
      this.orgUrl = orgUrl;
   }

   public String getAmsUrl() {
      return amsUrl;
   }

   public void setAmsUrl(String amsUrl) {
      this.amsUrl = amsUrl;
   }
}
