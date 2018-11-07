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
 * @author Dominic A. Guss
 */
public class TmzTestPoint {

   private boolean pass;
   private String testPointName;
   private String actual;
   private String expected;
   private long elapsedTime;
   private int numTransmissions;
   private String groupName;
   private List<TmzTestPoint> testPoints;
   private List<TmzTestPoint> testPoint;
   private String operation;

   public TmzTestPoint() {
      //testPoints = new LinkedList<>();
   }

   public boolean getPass() {
      return pass;
   }

   public void setPass(boolean pass) {
      this.pass = pass;
   }

   public String getTestPointName() {
      return testPointName;
   }

   public void setTestPointName(String testPointName) {
      this.testPointName = testPointName;
   }

   public String getActual() {
      return actual;
   }

   public void setActual(String actual) {
      this.actual = actual;
   }

   public String getExpected() {
      return expected;
   }

   public void setExpected(String expected) {
      this.expected = expected;
   }

   public long getElapsedTime() {
      return elapsedTime;
   }

   public void setElapsedTime(long elapsedTime) {
      this.elapsedTime = elapsedTime;
   }

   public int getNumTransmissions() {
      return numTransmissions;
   }

   public void setNumTransmissions(int numTransmissions) {
      this.numTransmissions = numTransmissions;
   }

   public String getGroupName() {
      return groupName;
   }

   public void setGroupName(String groupName) {
      this.groupName = groupName;
   }

   public List<TmzTestPoint> getTestPoints() {
      return testPoints;
   }

   public void setTestPoints(List<TmzTestPoint> testPoints) {
      this.testPoints = testPoints;
   }

   public String getOperation() {
      return operation;
   }

   public void setOperation(String operation) {
      this.operation = operation;
   }

   public List<TmzTestPoint> getTestPoint() {
      return testPoint;
   }

   public void setTestPoint(List<TmzTestPoint> testPoint) {
      this.testPoint = testPoint;
   }
}
