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
package org.eclipse.osee.framework.search.engine;

/**
 * @author Roberto E. Escobar
 */
public class MatchLocation {
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

}
