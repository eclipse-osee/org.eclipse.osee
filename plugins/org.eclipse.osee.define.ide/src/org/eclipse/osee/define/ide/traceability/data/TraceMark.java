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
package org.eclipse.osee.define.ide.traceability.data;

/**
 * @author Roberto E. Escobar
 */
public class TraceMark {
   private final String traceType;
   private final String rawTraceMark;

   public TraceMark(String traceType, String rawTraceMark) {
      super();
      this.traceType = traceType;
      this.rawTraceMark = rawTraceMark;
   }

   public String getTraceType() {
      return traceType;
   }

   public String getRawTraceMark() {
      return rawTraceMark;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof TraceMark) {
         final TraceMark other = (TraceMark) obj;
         boolean result = true;
         if (other.getTraceType() != null && getTraceType() != null) {
            result &= other.getTraceType().equals(getTraceType());
         } else {
            result &= other.getTraceType() == null && getTraceType() == null;
         }
         if (other.getRawTraceMark() != null && getRawTraceMark() != null) {
            result &= other.getRawTraceMark().equals(getRawTraceMark());
         } else {
            result &= other.getRawTraceMark() == null && getRawTraceMark() == null;
         }
         return result;
      }
      return false;
   }

   @Override
   public int hashCode() {
      final int prime = 37;
      int result = 17;
      if (getTraceType() != null) {
         result = prime * result + getTraceType().hashCode();
      } else {
         result = prime * result;
      }
      if (getRawTraceMark() != null) {
         result = prime * result + getRawTraceMark().hashCode();
      } else {
         result = prime * result;
      }
      return result;
   }

   @Override
   public String toString() {
      return String.format("<%s:%s>", getTraceType(), getRawTraceMark());
   }
}
