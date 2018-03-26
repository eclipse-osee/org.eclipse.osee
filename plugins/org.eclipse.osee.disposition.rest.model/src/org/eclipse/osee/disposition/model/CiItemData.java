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
package org.eclipse.osee.disposition.model;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;

@XmlRootElement(name = "CiItemData")
public class CiItemData implements Identifiable<String> {

   private CiSetData setData;
   private String scriptName;
   private CiTestPoint testPoints;
   private List<DispoAnnotationData> annotations;

   @Override
   public String getGuid() {
      return null;
   }

   public String getScriptName() {
      return scriptName;
   }

   public void setScriptName(String scriptName) {
      this.scriptName = scriptName;
   }

   public CiTestPoint getTestPoints() {
      return testPoints;
   }

   public void setTestPoints(CiTestPoint testPoints) {
      this.testPoints = testPoints;
   }

   public List<DispoAnnotationData> getAnnotations() {
      return annotations;
   }

   public void setAnnotations(List<DispoAnnotationData> annotations) {
      this.annotations = annotations;
   }

   public CiSetData getSetData() {
      return setData;
   }

   public void setSetData(CiSetData setData) {
      this.setData = setData;
   }

}
