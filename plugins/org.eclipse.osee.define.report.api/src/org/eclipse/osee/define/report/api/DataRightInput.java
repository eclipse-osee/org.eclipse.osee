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
package org.eclipse.osee.define.report.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Angel Avila
 */
@XmlRootElement
public class DataRightInput implements Iterable<DataRightEntry> {

   @XmlTransient
   private List<DataRightEntry> data;

   public List<DataRightEntry> getData() {
      if (data == null) {
         data = new ArrayList<DataRightEntry>();
      }
      return data;
   }

   public void setData(List<DataRightEntry> data) {
      this.data = data;
   }

   public boolean isEmpty() {
      return data == null || data.isEmpty();
   }

   public void addData(String guid, String classification, PageOrientation orientation) {
      DataRightEntry toAdd = new DataRightEntry();
      toAdd.setClassification(classification);
      toAdd.setGuid(guid);
      toAdd.setOrientation(orientation);

      getData().add(toAdd);
   }

   @Override
   public Iterator<DataRightEntry> iterator() {
      return data.iterator();
   }

}