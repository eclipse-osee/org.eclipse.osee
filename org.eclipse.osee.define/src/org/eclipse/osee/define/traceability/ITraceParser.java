/*
 * Created on Apr 1, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.traceability;

import java.nio.CharBuffer;
import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.CompositeKey;

/**
 * @author Roberto E. Escobar
 */
public interface ITraceParser {

   public Collection<TraceMark> getTraceMarks(CharBuffer fileBuffer);

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

      /* (non-Javadoc)
       * @see java.lang.Object#equals(java.lang.Object)
       */
      @Override
      public boolean equals(Object obj) {
         if (obj instanceof CompositeKey) {
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

      /* (non-Javadoc)
       * @see java.lang.Object#hashCode()
       */
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
}
