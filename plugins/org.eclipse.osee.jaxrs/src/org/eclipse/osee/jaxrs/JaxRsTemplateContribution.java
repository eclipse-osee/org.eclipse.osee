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
package org.eclipse.osee.jaxrs;

import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class JaxRsTemplateContribution {

   private String bundleName;
   private String version;

   @XmlTransient
   private Set<JaxRsTemplateLink> templates;

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
   public Set<JaxRsTemplateLink> getTemplates() {
      if (templates == null) {
         templates = new TreeSet<JaxRsTemplateLink>();
      }
      return templates;
   }
}