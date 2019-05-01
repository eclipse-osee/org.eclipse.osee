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
package org.eclipse.osee.framework.jdk.core.type;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement(name = "MatchLocation")
public class MatchLocation implements Cloneable {
   private int startPosition;
   private int endPosition;

   public MatchLocation() {
      reset();
   }

   public MatchLocation(int startPosition, int endPosition) {
      this.startPosition = startPosition;
      this.endPosition = endPosition;
   }

   public void reset() {
      startPosition = 0;
      endPosition = 0;
   }

   @Override
   public String toString() {
      return String.format("startAt: [%s] endAt: [%s] ", startPosition, endPosition);
   }

   @Override
   public MatchLocation clone() {
      return new MatchLocation(this.startPosition, this.endPosition);
   }

   /**
    * @return the startPosition
    */
   public int getStartPosition() {
      return startPosition;
   }

   /**
    * @return the endPosition
    */
   public int getEndPosition() {
      return endPosition;
   }

   /**
    * @param startPosition the startPosition to set
    */
   public void setStartPosition(int startPosition) {
      this.startPosition = startPosition;
   }

   /**
    * @param endPosition the endPosition to set
    */
   public void setEndPosition(int endPosition) {
      this.endPosition = endPosition;
   }

   public MatchLocation set(int startPosition, int endPosition) {
      this.startPosition = startPosition;
      this.endPosition = endPosition;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + endPosition;
      result = prime * result + startPosition;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      MatchLocation other = (MatchLocation) obj;
      if (endPosition != other.endPosition) {
         return false;
      }
      if (startPosition != other.startPosition) {
         return false;
      }
      return true;
   }

}
