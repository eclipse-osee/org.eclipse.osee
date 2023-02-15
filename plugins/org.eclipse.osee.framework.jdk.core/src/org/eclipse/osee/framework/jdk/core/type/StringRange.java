/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.jdk.core.type;

import java.util.Objects;
import java.util.function.Supplier;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * An immutable integer range with an inclusive starting point and exclusive ending point intended for use with strings.
 *
 * @author Loren K. Ashley
 */

public class StringRange implements ToMessage {

   /**
    * Option flag that requires the start and end points to be greater than or equal to zero.
    */

   public static int NonNegativeEndpoints = 1;

   /**
    * Option flag that requires the end point to be greater than or equal to the start point.
    */

   public static int NonNegativeRange = 2;

   /**
    * The exclusive ending point of the range.
    */

   private final int end;

   /**
    * The inclusive number of integers between the starting point and the ending point minus one.
    */

   private final int length;

   /**
    * Saves the options this {@link StringRange} was created with for creating derived {@link StringRange} objects.
    */

   private final int options;

   /**
    * The inclusive starting point of the range.
    */

   private final int start;

   /**
    * Creates a new {@link StringRange} without any checks.
    *
    * @param start the starting point, inclusive.
    * @param end the ending point, exclusive.
    */

   public StringRange(int start, int end) {
      this.start = start;
      this.end = end;
      this.length = end - start;
      this.options = 0;
   }

   /**
    * Creates a new {@link StringRange} with the checks specified by <code>options</code>.
    * <dl>
    * <dt>NonNegativeRange</dt>
    * <dd>When specified validates, <code>end</code> &gt;= <code>start</code></dd>
    * <dt>NonNegativeEndpoints</dt>
    * <dd>When specified, <code>start</code> &gt;= 0 &amp; <code>end</code> &gt;= 0</dd>
    * </dl>
    *
    * @param start the starting point, inclusive.
    * @param end the ending point, exclusive.
    * @param options flags for end point checks to be performed.
    * @throws IndexOutOfBoundsException when an end point or the end points do not conform to check specified by the
    * parameter <code>options</code>.
    */

   public StringRange(int start, int end, int options) {

      this.start = start;
      this.end = end;
      this.length = end - start;
      this.options = options;

      //@formatter:off
      var status =   (
                        ( (    ( this.options & NonNegativeRange ) > 0 )
                            && ( this.length < 0                       ) )
                           ? 1 : 0
                     )
                   + (
                        ( (    ( this.options & NonNegativeEndpoints ) > 0 )
                            && (    ( this.start < 0 )
                                 || ( this.end   < 0 )                     ) )
                           ? 2 : 0
                     );

      if( status > 0 ) {

         String title;

         switch( status ) {
            case 1:
               title = "StringRange::new, range length is negative.";
               break;
            case 2:
               title = "StringRange::new, range end point(s) are negative.";
               break;
            case 3:
            default:
               title = "StringRange::new, range length is negative and range end point(s) are negative.";
               break;
         }

         throw
            new IndexOutOfBoundsException
                   (
                      new Message()
                             .title( title )
                             .indentInc()
                             .segment( "Start",  this.start  )
                             .segment( "End",    this.end    )
                             .segment( "Length", this.length )
                             .toString()
                   );
      }
      //@formatter:on
   }

   /**
    * Creates a new {@link StringRange} with an applied <code>offset</code> and the checks specified by
    * <code>options</code>.
    * <dl>
    * <dt>NonNegativeRange</dt>
    * <dd>When specified validates, <code>end</code> &gt;= <code>start</code></dd>
    * <dt>NonNegativeEndpoints</dt>
    * <dd>When specified, <code>start</code> &gt;= 0 &amp; <code>end</code> &gt;= 0</dd>
    * </dl>
    *
    * @param start the starting point, inclusive.
    * @param end the ending point, exclusive.
    * @param offset the <code>offset</code> is added to the <code>start</code> and <code>end</code> before performing
    * the checks and creating the {@link StringRange}.
    * @param options flags for end point checks to be performed.
    * @throws IndexOutOfBoundsException when an end point or the end points do not conform to check specified by the
    * parameter <code>options</code>.
    */

   public StringRange(int start, int end, int offset, int options) {
      this(start + offset, end + offset, options);
   }

   /**
    * Creates a new {@link StringRange} with <code>offset</code> added to the {@link #start} and {@link #end} of this
    * {@link StringRange} and the same options.
    *
    * @param offset the amount to be added to both the {@link #start} and {@link #end} of this {@link StringRange}.
    * @return a new {@link StringRange} that is offset by <code>offset</code> from this {@link StringRange}.
    * @throws IndexOutOfBoundsException when an end point of the new {@link StringRange} do not conform to the check
    * options this {@link StringRange} was created with.
    */

   public StringRange addOffset(int offset) {
      return new StringRange(this.start() + offset, this.end() + offset, this.options);
   }

   /**
    * Gets the exclusive ending point of the range.
    *
    * @return the ending point, exclusive.
    */

   public int end() {
      return this.end;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean equals(Object other) {
      //@formatter:off
      return
            ( other instanceof StringRange )
         && ( this.start == ((StringRange) other).start )
         && ( this.end   == ((StringRange) other).end   );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    *
    * @implNote Hash code implemented with the "elegant pairing function".
    */

   @Override
   public int hashCode() {
      //@formatter:off
      int o =   ( ( this.start < 0 ) ? 2 : 0 )
              + ( ( this.end   < 0 ) ? 1 : 0 );

      int e = this.start < this.end
                 ? this.end   * this.end   + this.start
                 : this.start * this.start + this.start + this.end;

      return o + 4 * e;
      //@formatter:on
   }

   /**
    * Predicate to determine if the specified <code>index</code> is within the range. The result is determined according
    * to the range as follows:
    * <dl>
    * <dt>NonNegative Range ({@link #start} &gt;= {@link #end})</dt>
    * <dd>The result is in range (<code>true</code>) when {@link start} &lt;= <code>index</code> and <code>index</code>
    * &lt; {@link #end}.</dd>
    * <dt>Negative Range ({@link #start} &lt; {@link #end})</dt>
    * <dd>The result is in range (<code>true</code>) when {@link start} &lt;= <code>index</code> or <code>index</code>
    * &lt; {@link #end}.</dd>
    * </dl>
    * <h2>Examples</h2>
    *
    * <pre>
    * NonNegative Range (s=3,e=7):
    *
    *    start-+       +-end
    *          |       |
    *          v       v
    *    0 1 2 3 4 5 6 7 8 9
    *          *******
    *          ^
    *          |
    *          +- in range
    *
    * Negative Range (s=7,e=3):
    *
    *      end-+       +-start
    *          |       |
    *          v       v
    *    0 1 2 3 4 5 6 7 8 9
    *  <-*****         *****->
    *    ^             ^
    *    |             |
    *    +- in range   +- in range
    * </pre>
    *
    * @param index the value to test.
    * @return <code>true</code>, when the <code>index</code> is within the range; otherwise, <code>false</code>.
    */

   public boolean isInRange(int index) {
      //@formatter:off
      return
         ( this.start <= this.end )
            ?    ( index >= this.start )
              && ( index <  this.end   )
            :    ( index >= this.start )
              || ( index <  this.end   );
      //@formatter:on
   }

   /**
    * Predicate to determine if the specified <code>range</code> is within the range. The result is determined according
    * to the range senses as follows:
    * <dl>
    * <dt>NonNegative Range, NonNegative Check Range</dt>
    * <dd><code>range.start</code> &gt;= {@link #start} && <code>range.end</code> &lt;= {@link #end}</dd>
    * <dt>NonNegative Range, Negative Check Range</dt>
    * <dd><code>false</code> always</dd>
    * <dt>Negative Range, NonNegative Check Range</dt>
    * <dd><code>range.start</code> &gt;= {@link #start} || <code>range.end</code> &lt;= {@link #end}</dd>
    * <dt>Negative Range, Negative Check Range</dt>
    * <dd><code>range.start</code> &gt;= {@link #start} && <code>range.end</code> &lt;= {@link #end}</dd>
    * </dl>
    * <h2>Examples</h2>
    *
    * <pre>
    * NonNegative Range (s=3,e=7) NonNegative Check Range (t=4,f=6)
    *          s       e
    *          |       |
    *          v       v
    *    0 1 2 3 4 5 6 7 8 9
    *          *******           <- Range
    *            ***             <- Check Range
    *            ^   ^
    *            |   |
    *            t   f
    *
    *    The check range can fit within the range.
    *
    * NonNegative Range (s=3,e=7) Negative Check Range (t=6,f=4)
    *          s       e
    *          |       |
    *          v       v
    *    0 1 2 3 4 5 6 7 8 9
    *          *******           <- Range
    *  <-*******     *******->   <- Check Range
    *            ^   ^
    *            |   |
    *            f   t
    *
    *    The check range can never fit within the range. The range if finite. The
    *    check range extends to positive and negative infinity.
    *
    * Negative Range (s=7,e=2) NonNegative Check Range (ta=0,fa=2)
    *                          NonNegative Check Range (tb=8,fb=10)
    *          e       s
    *          |       |
    *          v       v
    *    0 1 2 3 4 5 6 7 8 9 A
    *  <-*****         *******->   <- Range
    *    ***             ***       <- Check Ranges a and b
    *    ^   ^           ^   ^
    *    |   |           |   |
    *    ta  fa          tb  fb
    *
    *    The check range can fit within the portion of the range extending to
    *    positive infinity or it can fit within the portion of the range extending
    *    to negative infinity.
    *
    * Negative Range (s=7,e=2) Negative Check Range (t=8,f=2)
    *          e       s
    *          |       |
    *          v       v
    *    0 1 2 3 4 5 6 7 8 9 A
    *  <-*****         *******->   <- Range
    *  <-***             *****->   <- Check Range
    *        ^           ^
    *        |           |
    *        f           t
    *
    *    The check range fits within the range when the upper portion of the check range
    *    starts with or after the upper portion of the range and the lower portion of the
    *    check range ends before or with the lower portion of the range.
    * </pre>
    *
    * @param range the range to test.
    * @return <code>true</code>, when the <code>range</code> is within this range; otherwise, <code>false</code>.
    */

   public boolean isInRange(StringRange range) {
      //@formatter:off
      switch(    ( this.start <= this.end   ? 0 : 2 )
               + ( range.start <= range.end ? 0 : 1 ) )
      {
         /* this nonNegativeRange, range nonNegativeRange */
         case 0:
         /* this negativeRange,    range negativeRange    */
         case 3:
            return
                   ( range.start >= this.start )
               &&  ( range.end   <= this.end   );

         /* this nonNegativeRange, range negativeRange    */
         case 1:
            return false;

         /* this negativeRange,    range nonNegativeRange */
         case 2:
            return
                   ( range.start >= this.start )
               ||  ( range.end   <= this.end   );
      }
      //@formatter:on
      /* Cannot get here */
      return false;
   }

   /**
    * The inclusive number of integers between the starting point and the ending point minus one.
    *
    * @return the number of integers in the range.
    */

   public int length() {
      return this.length;
   }

   /**
    * Generates the message for an {@link IndexOutOfBoundsException}.
    *
    * @param index the index that was out of range.
    * @param message an optional description to be added to the message.
    * @return the generated error message.
    */

   private String outOfRangeMessage(int index, String message) {
      //@formatter:off
      return
         new Message()
            .title( message )
            .title( "Index out of bounds for StringRange." )
            .indentInc()
            .segment( "index", index )
            .segment( "range", this  )
            .toString();
      //@formatter:on
   }

   /**
    * Generates the message for an {@link IndexOutOfBoundsException}.
    *
    * @param checkRange the {@link StringRange} that was outside the range of this {@link StringRange}.
    * @param message an optional description to be added to the message.
    * @return the generated error message.
    */

   private String rangeOutOfRangeMessage(StringRange checkRange, String message) {
      //@formatter:off
      return
         new Message()
            .title( message )
            .title( "StringRange is out of bounds for StringRange." )
            .indentInc()
            .segment( "this range",  this       )
            .segment( "check range", checkRange )
            .toString();
      //@formatter:on
   }

   /**
    * Checks the provided <code>index</code> is within the range.
    *
    * @param index the value to be checked.
    * @throws IndexOutOfBoundException when the provided <code>index</code> is not within the range.
    */

   public void requireInRange(int index) {
      if (!this.isInRange(index)) {
         //@formatter:off
         throw
            new IndexOutOfBoundsException
                   (
                      this.outOfRangeMessage(index,null)
                   );
         //@formatter:on
      }
   }

   /**
    * Checks the provided <code>index</code> is within the range.
    *
    * @param index the value to be checked.
    * @param message detail message to be added to the standard message in the event that a
    * {@link IndexOutOfBoundsException} is thrown. This parameter maybe <code>null</code>.
    * @throws IndexOutOfBoundException when the provided <code>index</code> is not within the range.
    */

   public void requireInRange(int index, String message) {
      if (!this.isInRange(index)) {
         //@formatter:off
         throw
            new IndexOutOfBoundsException
                   (
                      this.outOfRangeMessage(index, message)
                   );
         //@formatter:on
      }
   }

   /**
    * Checks the provided <code>index</code> is within the range.
    *
    * @param index the value to be checked.
    * @param messageSupplier a {@link Supplier} of the detail message to be added to the standard message in the event
    * that a {@link IndexOutOfBoundsException} is thrown. This parameter maybe <code>null</code>. The string returned by
    * the {@link Supplier} may also be <code>null</code>.
    * @throws IndexOutOfBoundException when the provided <code>index</code> is not within the range.
    */

   public void requireInRange(int index, Supplier<String> messageSupplier) {
      if (!this.isInRange(index)) {
         //@formatter:off
         throw
            new IndexOutOfBoundsException
                   (
                      this.outOfRangeMessage
                         (
                            index,
                            Objects.nonNull( messageSupplier )
                               ? messageSupplier.get()
                               : null
                         )
                   );
         //@formatter:on
      }
   }

   /**
    * Checks the provided <code>checkRange</code> is within the range.
    *
    * @param checkRange the {@link StringRange} to be checked.
    * @throws IndexOutOfBoundException when the provided <code>checkRange</code> is not within the range.
    */

   public void requireInRange(StringRange checkRange) {
      if (!this.isInRange(checkRange)) {
         //@formatter:off
         throw
            new IndexOutOfBoundsException
                   (
                      this.rangeOutOfRangeMessage(checkRange, null)
                   );
         //@formatter:on
      }
   }

   /**
    * Checks the provided <code>checkRange</code> is within the range.
    *
    * @param checkRange the {@link StringRange} to be checked.
    * @param message detail message to be added to the standard message in the event that a
    * {@link IndexOutOfBoundsException} is thrown. This parameter maybe <code>null</code>.
    * @throws IndexOutOfBoundException when the provided <code>checkRange</code> is not within the range.
    */

   public void requireInRange(StringRange checkRange, String message) {
      if (!this.isInRange(checkRange)) {
         //@formatter:off
         throw
            new IndexOutOfBoundsException
                   (
                      this.rangeOutOfRangeMessage(checkRange, message)
                   );
         //@formatter:on
      }
   }

   /**
    * Checks the provided <code>checkRange</code> is within the range.
    *
    * @param checkRange the {@link StringRange} to be checked.
    * @param messageSupplier a {@link Supplier} of the detail message to be added to the standard message in the event
    * that a {@link IndexOutOfBoundsException} is thrown. This parameter maybe <code>null</code>. The string returned by
    * the {@link Supplier} may also be <code>null</code>.
    * @throws IndexOutOfBoundException when the provided <code>checkRange</code> is not within the range.
    */

   public void requireInRange(StringRange checkRange, Supplier<String> messageSupplier) {
      if (!this.isInRange(checkRange)) {
         //@formatter:off
         throw
            new IndexOutOfBoundsException
                   (
                      this.rangeOutOfRangeMessage
                         (
                            checkRange,
                            Objects.nonNull( messageSupplier )
                               ? messageSupplier.get()
                               : null
                         )
                   );
         //@formatter:on
      }
   }

   /**
    * Gets the inclusive starting point of the range.
    *
    * @return the starting point, inclusive.
    */

   public int start() {
      return this.start;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "StringRange" )
         .indentInc()
         .segment( "start",  this.start  )
         .segment( "end",    this.end    )
         .segment( "length", this.length )
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }
}

/* EOF */
