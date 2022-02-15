/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jaxrs;

import java.util.Set;
import java.util.TreeSet;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class JaxRsContributionInfo {

   private String bundleName;
   private String version;

   @XmlTransient
   private Set<ApplicationInfo> applications;

   @XmlTransient
   private Set<String> providers;

   @XmlTransient
   private Set<String> staticResources;

   public String getBundleName() {
      return bundleName;
   }

   public void setBundleName(String bundleName) {
      this.bundleName = bundleName;
   }

   public String getVersion() {
      return version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   @XmlElement
   public Set<ApplicationInfo> getApplications() {
      if (applications == null) {
         applications = new TreeSet<>();
      }
      return applications;
   }

   public void setApplications(Set<ApplicationInfo> applications) {
      this.applications = applications;
   }

   @XmlElement
   public Set<String> getProviders() {
      if (providers == null) {
         providers = new TreeSet<>();
      }
      return providers;
   }

   public void setProviders(Set<String> providers) {
      this.providers = providers;
   }

   @XmlElement
   public Set<String> getStaticResources() {
      if (staticResources == null) {
         staticResources = new TreeSet<>();
      }
      return staticResources;
   }

   public void setStaticResources(Set<String> staticResources) {
      this.staticResources = staticResources;
   }

}
