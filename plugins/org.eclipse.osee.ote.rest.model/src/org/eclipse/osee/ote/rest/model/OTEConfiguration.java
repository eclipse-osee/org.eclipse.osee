/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.rest.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Andrew M. Finkbeiner
 */
@XmlRootElement
public class OTEConfiguration {

   private OTEConfigurationIdentity identity;
   
   private List<OTEConfigurationItem> items;
   
   private boolean install;
 
   public OTEConfiguration(){
	   items = new ArrayList<OTEConfigurationItem>();
	   this.install = true;
   }
   
   public OTEConfigurationIdentity getIdentity(){
	   return identity;
   }
   
   @XmlElementWrapper
   @XmlElement(name="OTEConfigurationItem")
   public List<OTEConfigurationItem> getItems(){
	   return items;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((identity == null) ? 0 : identity.hashCode());
      result = prime * result + ((items == null) ? 0 : items.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      OTEConfiguration other = (OTEConfiguration) obj;
//      if (identity == null) {
//         if (other.identity != null)
//            return false;
//      } else if (!identity.equals(other.identity))
//         return false;
      if (items == null) {
         if (other.items != null)
            return false;
      } else if (!items.equals(other.items))
         return false;
      return true;
   }

   public void setIdentity(OTEConfigurationIdentity identity) {
	   this.identity = identity;
   }

   public void addItem(OTEConfigurationItem item){
	   items.add(item);
   }
   
   public void setInstall(boolean install) {
      this.install = install;
   }
   
   public boolean getInstall(){
      return this.install;
   }

}
