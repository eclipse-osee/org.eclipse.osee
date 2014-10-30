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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Angel Avila
 */
@XmlRootElement
public class DataRightInput implements Iterable<DataRightEntry> {

   @XmlTransient
   private Set<DataRightEntry> data;

   public Set<DataRightEntry> getData() {
      if (data == null) {
         data = new HashSet<DataRightEntry>();
      }
      return data;
   }

   public void setData(Set<DataRightEntry> data) {
      this.data = data;
   }

   public boolean isEmpty() {
      return data == null || data.isEmpty();
   }

   public void clear() {
      data = null;
   }

   public void addData(String guid, String classification, PageOrientation orientation, int index) {
      DataRightEntry toAdd = new DataRightEntry();
      toAdd.setClassification(classification);
      toAdd.setGuid(guid);
      toAdd.setOrientation(orientation);
      toAdd.setIndex(index);

      getData().add(toAdd);
   }

   @Override
   public Iterator<DataRightEntry> iterator() {
      return data.iterator();
   }

}