/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Angel Avila
 */

@XmlRootElement(name = "LocationRange")
public class LocationRange {
   private int start;
   private int end;

   public LocationRange() {
   }

   public LocationRange(int start) {
      this.start = start;
      this.end = start;
   }

   public LocationRange(int start, int end) {
      this.start = start;
      this.end = end;
   }

   public int getStart() {
      return start;
   }

   public int getEnd() {
      return end;
   }

   public void setStart(int start) {
      this.start = start;
   }

   public void setEnd(int end) {
      this.end = end;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(start);
      if (end != start) {
         sb.append("-");
         sb.append(end);
      }
      return sb.toString();
   }
}
