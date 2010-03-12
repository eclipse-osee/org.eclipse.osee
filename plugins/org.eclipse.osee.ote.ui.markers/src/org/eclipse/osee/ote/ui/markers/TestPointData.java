/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.markers;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class TestPointData {

   private boolean isFailed;
   private List<CheckPointData> data = new ArrayList<CheckPointData>();
   private String number;
   private StackTraceCollection stacktrace;
   
   public boolean isFailed() {
      return isFailed;
   }

   public void add(CheckPointData checkPoint) {
      data.add(checkPoint);
   }

   public void setFailed(boolean failed) {
      isFailed = failed;
   }

   public void setNumber(String number) {
      this.number = number;
   }

   public String getNumber(){
      return number;
   }
   
   public void setStackTrace(StackTraceCollection currentStackTrace) {
      this.stacktrace = currentStackTrace;
   }

   public List<CheckPointData> getCheckPointData() {
      return data;
   }

   public StackTraceCollection getStacktraceCollection() {
      return stacktrace;
   }
}
