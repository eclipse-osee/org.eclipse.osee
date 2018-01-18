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
package org.eclipse.osee.disposition.rest.internal.importer;

import java.util.List;

/**
 * @author Dominic Guss
 */
public class TmzChildRecord {

   private long timeStamp;
   private int number;
   private List<String> location;
   private List<TmzTestPoint> testPoint;

   public TmzChildRecord() {
      //Do nothing
   }

   public int getNumber() {
      return number;
   }

   public void setNumber(int number) {
      this.number = number;
   }

   public long getTimeStamp() {
      return timeStamp;
   }

   public void setTimeStamp(long timeStamp) {
      this.timeStamp = timeStamp;
   }

   public List<String> getLocation() {
      return location;
   }

   public void setLocation(List<String> location) {
      this.location = location;
   }

   public List<TmzTestPoint> getTestPoint() {
      return testPoint;
   }

   public void setTestPoint(List<TmzTestPoint> testPoint) {
      this.testPoint = testPoint;
   }
}
