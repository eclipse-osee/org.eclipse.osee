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
 * An immutable integer range intended for use with {@link CharSequence} implementations. A positive range has a
 * starting point that is less than or equal to the ending point. A negative range has a starting point that is greater
 * than the ending point. The inclusivity of the end points is determined by the range sense as follows:
 * <dl>
 * <dt>Positive Range</dt>
 * <dd>
 * <ul>
 * <li>Starting point is inclusive.</li>
 * <li>Ending point is exclusive.</li>
 * </ul>
 * </dd>
 * <dt>Negative Range</dt>
 * <dd>
 * <ul>
 * <li>Starting point is exclusive.</li>
 * <li>Ending point is inclusive.</li>
 * </ul>
 * </dd>
 * </dl>
 *
 * @author Loren K. Ashley
 */

public class StringRange implements ToMessage {

   /**
    * Option flag that requires the start and end points to be greater than or equal to zero.
    */

   public static int NONNEGATIVE_ENDPOINTS = 0x01;

   /**
    * Option flag that requires the end point to be greater than or equal to the start point.
    */

   public static int NONNEGATIVE_RANGE = 0x02;

   /**
    * Array of error message titles.
    * <dl>
    * <dt>0</dt>
    * <dd>No error condition, this entry is not used.</dd>
    * <dt>1</dt>
    * <dd>Error message for a negative range.</dd>
    * <dt>2</dt>
    * <dd>Error message for a range endpoint being negative.</dd>
    * <dt>3</dt>
    * <dd>Error message for when the range is negative and a range endpoint being negative</dd>
    * </dl>
    */

   //@formatter:off
   private static String[] errorTitles =
   {
      "",
      "StringRange::new, range length is negative.",
      "StringRange::new, range end point(s) are negative.",
      "StringRange::new, range length is negative and range end point(s) are negative."
   };
   //@formatter:on

   /**
    * The range ending point according to the range sense as follows:
    * <dl>
    * <dt>Positive Range</dt>
    * <dd>The exclusive ending point of the range.</dd>
    * <dt>Negative Range</dt>
    * <dd>The inclusive ending point of the range.</dd>
    * </dl>
    */

   private final int end;

   /**
    * For a positive range, the number of characters in the range. For a negative range, minus 1 times the number of
    * characters in the range.
    */

   private final int length;

   /**
    * Saves the options this {@link StringRange} was created with for creating derived {@link StringRange} objects.
    */

   private final int options;

   /**
    * The range starting point according to the range sense as follows:
    * <dl>
    * <dt>Positive Range</dt>
    * <dd>The inclusive starting point of the range.</dd>
    * <dt>Negative Range</dt>
    * <dd>The exclusive starting point of the range.</dd>
    * </dl>
    */

   private final int start;

   /**
    * Creates a new {@link StringRange} without any checks.
    * <dl>
    * <dt>Positive Range</dt>
    * <dd>
    * <ul>
    * <li>Starting point is inclusive.</li>
    * <li>Ending point is exclusive.</li>
    * </ul>
    * </dd>
    * <dt>Negative Range</dt>
    * <dd>
    * <ul>
    * <li>Starting point is exclusive.</li>
    * <li>Ending point is inclusive.</li>
    * </ul>
    * </dd>
    * </dl>
    *
    * @param start the starting point.
    * @param end the ending point.
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
    * The inclusivity of the end points is as follows:
    * <p>
    * <dl>
    * <dt>Positive Range</dt>
    * <dd>
    * <ul>
    * <li>Starting point is inclusive.</li>
    * <li>Ending point is exclusive.</li>
    * </ul>
    * </dd>
    * <dt>Negative Range</dt>
    * <dd>
    * <ul>
    * <li>Starting point is exclusive.</li>
    * <li>Ending point is inclusive.</li>
    * </ul>
    * </dd>
    * </dl>
    *
    * @param start the starting point.
    * @param end the ending point.
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
                        ( (    ( this.options & NONNEGATIVE_RANGE ) > 0 )
                            && ( this.length < 0                        ) )
                           ? 1 : 0
                     )
                   + (
                        ( (    ( this.options & NONNEGATIVE_ENDPOINTS ) > 0 )
                            && (    ( this.start < 0 )
                                 || ( this.end   < 0 )                      ) )
                           ? 2 : 0
                     );

      if( status > 0 ) {

         throw
            new IndexOutOfBoundsException
                   (
                      new Message()
                             .title( StringRange.errorTitles[ status ] )
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
    * <dd>When specified, <code>start + offset</code> &gt;= 0 &amp; <code>end + offset</code> &gt;= 0</dd>
    * </dl>
    * The inclusivity of the end points is as follows:
    * <p>
    * <dl>
    * <dt>Positive Range</dt>
    * <dd>
    * <ul>
    * <li>Starting point is inclusive.</li>
    * <li>Ending point is exclusive.</li>
    * </ul>
    * </dd>
    * <dt>Negative Range</dt>
    * <dd>
    * <ul>
    * <li>Starting point is exclusive.</li>
    * <li>Ending point is inclusive.</li>
    * </ul>
    * </dd>
    * </dl>
    *
    * @param start the starting point.
    * @param end the ending point.
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
    * Gets the number of characters in the range. The value is always non-negative.
    *
    * @return the number of characters in the range.
    */

   public int absLength() {
      return this.length >= 0 ? this.length : -this.length;
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
    * Calculates the index for the underlying storage from an index within the {@link StringRange}.
    *
    * @param index the index within the {@link StringRange}.
    * @return the index for the underlying storage.
    * @throws IndexOutOfBoundException when the provided <code>index</code> is not within the range.
    */

   public int baseIndex(int index) {
      //@formatter:off
      var baseIndex =
           this.start
         + ( this.isPositive()
                ? index
                : -index -1 );
      //@formatter:on
      this.requireInRange(baseIndex);

      return baseIndex;
   }

   /**
    * Gets ending point of the range.
    * <dl>
    * <dt>Positive Range</dt>
    * <dd>Ending point is exclusive.</dd>
    * <dt>Negative Range</dt>
    * <dd>Ending point is inclusive.</dd>
    * </dl>
    *
    * @return the ending point.
    */

   public int end() {
      return this.end;
   }

   /**
    * {@inheritDoc}
    * <p>
    * Only the members {@link #start} and {@link #end} are compared. The {@link #options} are not compared.
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
    * <dd>The result is in range (<code>true</code>) when {@link #start} &lt;= <code>index</code> &lt;
    * {@link #end}.</dd>
    * <dt>Negative Range ({@link #start} &lt; {@link #end})</dt>
    * <dd>The result is in range (<code>true</code>) when {@link #end} &lt;= <code>index</code> &lt;
    * {@link #start}.</dd>
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
    *          *******
    *          ^
    *          |
    *          +- in range
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
            :    ( index >= this.end   )
              && ( index <  this.start );
      //@formatter:on
   }

   /**
    * Predicate to determine if the specified <code>range</code> is within the range.
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
    *   ( t >= s ) && ( f <= e )
    *
    * NonNegative Range (s=3,e=7) Negative Check Range (t=6,f=4)
    *          s       e
    *          |       |
    *          v       v
    *    0 1 2 3 4 5 6 7 8 9
    *          *******           <- Range
    *            ***             <- Check Range
    *            ^   ^
    *            |   |
    *            f   t
    *
    *   ( f >= s ) && ( t <= e )
    *
    * Negative Range (s=7,e=3) NonNegative Check Range (t=4,f=6)
    *
    *          e       s
    *          |       |
    *          v       v
    *    0 1 2 3 4 5 6 7 8 9 A
    *          *******           <- Range
    *            ***             <- Check Range
    *            ^   ^
    *            |   |
    *            t   f
    *
    *    ( t >= e ) && ( f <= s )
    *
    * Negative Range (s=7,e=3) Negative Check Range (t=6,f=4)
    *          e       s
    *          |       |
    *          v       v
    *    0 1 2 3 4 5 6 7 8 9 A
    *          *******             <- Range
    *            ***               <- Check Range
    *            ^   ^
    *            |   |
    *            f   t
    *
    *    ( f >= e ) && ( t <= s )
    * </pre>
    *
    * @param range the range to test.
    * @return <code>true</code>, when the <code>range</code> is within this range; otherwise, <code>false</code>.
    */

   public boolean isInRange(StringRange range) {

      //@formatter:off
      return
            Math.min( range.start, range.end ) >= Math.min( this.start, this.end )
         && Math.max( range.start, range.end ) <= Math.max( this.start, this.end );
      //@formatter:on
   }

   /**
    * Predicate to determine if the range is positive or negative.
    *
    * @return <code>true<code>, when {@link #start} &lt;= {@link #end}; otherwise <code>false</code>.
    */

   public boolean isPositive() {
      return this.start <= this.end;
   }

   /**
    * The inclusive number of integers between the starting point and the ending point minus one.
    *
    * @return the number of integers in the range, a negative range will have a negative length.
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
    * Gets the starting point of the range.
    * <dl>
    * <dt>Positive Range</dt>
    * <dd>Starting point is inclusive.</dd>
    * <dt>Negative Range</dt>
    * <dd>Starting point is exclusive.</dd>
    * </dl>
    *
    * @return the starting point.
    */

   public int start() {
      return this.start;
   }

   /**
    * Creates a new {@link StringRange} that is a sub range of this one with the same options as this
    * {@link StringRange}.
    * <p>
    * The inclusivity of the end points is as follows:
    * <p>
    * <dl>
    * <dt>Positive Range</dt>
    * <dd>
    * <ul>
    * <li>Starting point is inclusive.</li>
    * <li>Ending point is exclusive.</li>
    * </ul>
    * </dd>
    * <dt>Negative Range</dt>
    * <dd>
    * <ul>
    * <li>Starting point is exclusive.</li>
    * <li>Ending point is inclusive.</li>
    * </ul>
    * </dd>
    * </dl>
    *
    * @param start the index for the start of the subrange.
    * @param end the index for the end of the subrange.
    * @return a new {@link StringRange} that is a subrange of this one.
    */

   StringRange subRange(int start, int end) {
      return this.subRange(start, end, this.options);
   }

   /**
    * Creates a new {@link StringRange} that is a sub range of this one with the checks specified by
    * <code>options</code>. The sense of the returned range is determined as follows:
    * <p>
    * <table border="1">
    * <tr>
    * <th>Range Sense</th>
    * <th>Sub-Range Sense</th>
    * <th>Returned Range Sense</th>
    * </tr>
    * <tr>
    * <td>Positive</td>
    * <td>Positive</td>
    * <td>Positive</td>
    * </tr>
    * <tr>
    * <td>Positive</td>
    * <td>Negative</td>
    * <td>Negative</td>
    * </tr>
    * <tr>
    * <td>Negative</td>
    * <td>Positive</td>
    * <td>Negative</td>
    * </tr>
    * <tr>
    * <td>Negative</td>
    * <td>Negative</td>
    * <td>Positive</td>
    * </tr>
    * </table>
    * <dl>
    * <dt>NonNegativeRange</dt>
    * <dd>When specified validates, <code>end</code> &gt;= <code>start</code></dd>
    * <dt>NonNegativeEndpoints</dt>
    * <dd>When specified, <code>start</code> &gt;= 0 &amp; <code>end</code> &gt;= 0</dd>
    * </dl>
    * The inclusivity of the end points is as follows:
    * <p>
    * <dl>
    * <dt>Positive Range</dt>
    * <dd>
    * <ul>
    * <li>Starting point is inclusive.</li>
    * <li>Ending point is exclusive.</li>
    * </ul>
    * </dd>
    * <dt>Negative Range</dt>
    * <dd>
    * <ul>
    * <li>Starting point is exclusive.</li>
    * <li>Ending point is inclusive.</li>
    * </ul>
    * </dd>
    * </dl>
    *
    * @param start the index for the start of the subrange.
    * @param end the index for the end of the subrange.
    * @param options flags for end point checks to be performed.
    */

   StringRange subRange(int start, int end, int options) {
      //@formatter:off
      return
         this.isPositive()
            ? new StringRange(  start,  end, this.start, options )
            : new StringRange( -start, -end, this.start, options );
      //@formatter:on
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
