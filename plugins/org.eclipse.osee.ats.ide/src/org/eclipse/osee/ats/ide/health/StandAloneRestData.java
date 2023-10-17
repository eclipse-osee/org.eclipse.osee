/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.ide.health;

public class StandAloneRestData {

   public String name;
   public String url;
   public String mediaType;
   public String resultStr;

   public StandAloneRestData(String name, String url, String mediaType, String resultStr) {
      this.name = name;
      this.url = url;
      this.mediaType = mediaType;
      this.resultStr = resultStr;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getResultStr() {
      return resultStr;
   }

   public void setResultStr(String resultStr) {
      this.resultStr = resultStr;
   }

   public String getMediaType() {
      return mediaType;
   }

   public void setMediaType(String mediaType) {
      this.mediaType = mediaType;
   }

}