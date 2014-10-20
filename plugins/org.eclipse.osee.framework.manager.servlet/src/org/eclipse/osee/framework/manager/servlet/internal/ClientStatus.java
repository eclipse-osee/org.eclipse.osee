/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.manager.servlet.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class ClientStatus {

   private Date date = new Date();
   private String dateStr = DateUtil.get(date, DateUtil.MMDDYYHHMM);
   private List<ReleaseTypeStatus> releaseByType = new ArrayList<ReleaseTypeStatus>();
   private List<ReleaseStatus> releases = new ArrayList<ReleaseStatus>();

   public List<ReleaseStatus> getReleases() {
      return releases;
   }

   public void setReleases(List<ReleaseStatus> releases) {
      this.releases = releases;
   }

   public Date getDate() {
      return date;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public String getDateStr() {
      return dateStr;
   }

   public void setDateStr(String dateStr) {
      this.dateStr = dateStr;
   }

   public List<ReleaseTypeStatus> getReleaseByType() {
      return releaseByType;
   }

   public void setReleaseByType(List<ReleaseTypeStatus> releaseByType) {
      this.releaseByType = releaseByType;
   }

}
