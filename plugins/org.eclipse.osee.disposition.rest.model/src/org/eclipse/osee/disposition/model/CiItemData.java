/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.disposition.model;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;

/**
 * @author Angel Avila
 */
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
