/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.server.ide.api.client.model;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class ClientInfo {

   private static Pattern namePattern = Pattern.compile("Name: \\[(.*)\\]");
   private static Pattern versionPattern = Pattern.compile("Version:\\[(.*)\\]");
   private static Pattern buildTypePattern = Pattern.compile("BuildType:\\[(.*)\\]");
   private static Pattern userIdPattern = Pattern.compile("User Id:\\[(.*)\\]");
   private static Pattern installationPattern = Pattern.compile("Installation Location: \\[(.*)\\]");
   private String infoStr;
   private boolean alive;
   private final Date date;

   public ClientInfo() {
      this("");
   }

   public ClientInfo(String infoStr) {
      this.infoStr = infoStr;
      this.date = new Date();
   }

   public String getName() {
      return getValue(namePattern);
   }

   public String getVersion() {
      return getValue(versionPattern);
   }

   public String getUserId() {
      return getValue(userIdPattern);
   }

   public String getBuildType() {
      return getValue(buildTypePattern);
   }

   public String getInstallation() {
      return getValue(installationPattern);
   }

   public String getDate() {
      return DateUtil.get(date, DateUtil.MMDDYYHHMM);
   }

   public String getValue(Pattern pattern) {
      String name = "unknown";
      Matcher m = pattern.matcher(infoStr);
      if (m.find()) {
         name = m.group(1);
      }
      return name;
   }

   @Override
   public String toString() {
      return "ClientInfo [info=" + infoStr + ", getName()=" + getName() + ", getVersion()=" + getVersion() + ", getUserId()=" + getUserId() + ", getBuildType()=" + getBuildType() + ", getInstallationPattern()=" + getInstallation() + "]";
   }

   public void setInfoStr(String infoStr) {
      this.infoStr = infoStr;
   }

   public boolean isAlive() {
      return alive;
   }

   public void setAlive(boolean alive) {
      this.alive = alive;
   }

   public String getInfoStr() {
      return infoStr;
   }

}
