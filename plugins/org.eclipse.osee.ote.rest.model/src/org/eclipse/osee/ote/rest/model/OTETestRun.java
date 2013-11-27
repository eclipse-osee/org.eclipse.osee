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
public class OTETestRun {

   private OTEConfiguration jarConfiguration;
   private Properties globalProperties;
   private List<Properties> tests;
 
   public OTETestRun(){
      tests = new ArrayList<Properties>();
      globalProperties = new Properties();
   }
   
   public Properties getGlobalProperties(){
	   return globalProperties;
   }
   
   @XmlElementWrapper
   @XmlElement(name="Properties")
   public List<Properties> getTests(){
	   return tests;
   }

   public void setGlobalProperties(Properties globalProperties) {
	   this.globalProperties = globalProperties;
   }

   public void addTest(Properties test){
	   tests.add(test);
   }

   public OTEConfiguration getJarConfiguration() {
      return jarConfiguration;
   }

   public void setJarConfiguration(OTEConfiguration jarConfiguration) {
      this.jarConfiguration = jarConfiguration;
   }

   
}
