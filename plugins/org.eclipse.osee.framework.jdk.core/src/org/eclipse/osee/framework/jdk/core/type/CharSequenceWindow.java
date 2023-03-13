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
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Provides a {@link CharSequence} implementation for a portion of a {@link CharSequence}. If the backing
 * {@link CharSequence} is mutable, changes to the contents of the backing {@link CharSequence} will be reflected in the
 * {@link CharSequenceWindow} and the {@link CharSequenceWindow} end points may become out of range resulting in an
 * {@link IndexOutOfBoundsException}.
 * <p>
 * This implementation supports negative ranges where the starting index is greater than the ending index. A
 * {@link CharSequenceWindow} with a negative range will provide the characters in the reverse order of the characters
 * in the backing {@link CharSequence}.
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
 * The purpose of this class is to provide a &quot;substring view&quot; of a {@link CharSequence} without making a copy
 * of the source data.
 *
 * @author Loren K. Ashley
 */

public class CharSequenceWindow implements CharSequence, ToMessage {

   /**
    * The {@link CharSequence} to have a portion be windowed as a {@link CharSequence}.
    */

   private final CharSequence charSequence;

   /**
    * The character index range of the string to be windowed.
    */

   private final StringRange stringRange;

   /**
    * Creates a {@link CharSequenceWindow} implementation for a portion of the provided {@link CharSequence} from the
    * inclusive stating position through the end of the provided {@link CharSequence}.
    *
    * @param charSequence the {@link CharSequence} to create a window for.
    * @param start the starting character position for the {@link CharSequenceWindow} within the provided
    * {@link CharSequence}.
    * @throws NullPointerException when the parameter <code>charSequence</code> is <code>null</code>.
    * @throws IndexOutOfBounds when the range specified by <code>start</code> and the length of the provided
    * <code>charSequence</code> is outside the range of the provided <code>charSequence</code>.
    */

   public CharSequenceWindow(CharSequence charSequence, int start) {

      this.charSequence =
         Objects.requireNonNull(charSequence, "CharSequenceWindow::new, parameter \"charSequence\" cannot be null.");

      //@formatter:off
      try {

         var checkStringRange = new StringRange(0,charSequence.length());

         this.stringRange =
            new StringRange
                   (
                      start,
                      charSequence.length(),
                      StringRange.NONNEGATIVE_ENDPOINTS
                   );

         checkStringRange.requireInRange(this.stringRange);

      } catch( IndexOutOfBoundsException ioobe ) {

         var exception =
            new IndexOutOfBoundsException
                   (
                      new Message()
                             .title( "CharSequenceWindow::new, window range out of bounds." )
                             .indentInc()
                             .segment( "CharSequence", charSequence          )
                             .segment( "Start",        start                 )
                             .segment( "End",          charSequence.length() )
                             .toString()
                   );

         exception.initCause( ioobe );

         throw exception;
      }
      //@formatter:on
   }

   /**
    * Creates a {@link CharSequenceWindow} implementation for a portion of the provided {@link CharSequecne} between the
    * starting position and the ending position. This constructor maybe used to create a {@link CharSequence} with a
    * negative range.
    *
    * @param charSequence the {@link CharSequence} to create a window for.
    * @param start the starting character position for the {@link CharSequenceWindow} within the provided
    * {@link CharSequence}.
    * @param end the ending character position for the {@link CharSequenceWindow} within the provided
    * {@link CharSequence}.
    * @throws NullPointerException when the parameter <code>charSequence</code> is <code>null</code>.
    * @throws IndexOutOfBounds when the range specified by <code>start</code> and <code>end</code> is outside the range
    * of the provided <code>charSequence</code>.
    */

   public CharSequenceWindow(CharSequence charSequence, int start, int end) {

      this.charSequence =
         Objects.requireNonNull(charSequence, "CharSequenceWindow::new, parameter \"charSequence\" cannot be null.");

      //@formatter:off
      try
      {
         var checkStringRange = new StringRange(0,charSequence.length());

         this.stringRange =
            new StringRange
                   (
                      start,
                      end,
                      StringRange.NONNEGATIVE_ENDPOINTS
                   );

         checkStringRange.requireInRange( this.stringRange );

      } catch( IndexOutOfBoundsException ioobe ) {

         var exception =
            new IndexOutOfBoundsException
                   (
                      new Message()
                             .title( "CharSequenceWindow::new, window range out of bounds." )
                             .indentInc()
                             .segment( "CharSequence", charSequence )
                             .segment( "Start",        start        )
                             .segment( "End",          end          )
                             .toString()
                   );

         exception.initCause( ioobe );

         throw exception;
      }
      //@formatter:on
   }

   /**
    * Private constructor creates a {@link CharSequenceWindow} implementation for a portion of the provided
    * {@link CharSequence}. The <code>charSequence</code> must already be validated as non-null before calling this
    * constructor. The <code>stringRange</code> must already be validated before calling this constructor.
    *
    * @param charSequence the {@link CharSequence} to create a window for.
    * @param stringRange a {@link StringRange} containing the starting and ending character positions for the
    * {@link CharSequenceWindow} within the provided {@link CharSequenced}.
    */

   private CharSequenceWindow(CharSequence charSequence, StringRange stringRange) {
      this.charSequence = charSequence;
      this.stringRange = stringRange;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int length() {
      return this.stringRange.absLength();
   }

   /**
    * {@inheritDoc}
    *
    * @throws IndexOutOfBoundsException {@inheritDoc}
    */

   @Override
   public char charAt(int index) {
      var baseIndex = this.stringRange.baseIndex(index);
      return this.charSequence.charAt(baseIndex);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public CharSequence subSequence(int start, int end) {

      StringRange offsetStringRange;

      //@formatter:off
      try {

         offsetStringRange =
            this.stringRange.subRange
               (
                  start,
                  end,
                  StringRange.NONNEGATIVE_ENDPOINTS
               );

      } catch( IndexOutOfBoundsException ioobe ) {
         var exception =
            new IndexOutOfBoundsException
                   (
                      new Message()
                         .title( "CharSequenceWindow:subSequence, specified window range is invalid." )
                         .indentInc()
                         .segment( "CharSequenceWindow", this  )
                         .segment( "Start",              start )
                         .segment( "End",                end   )
                         .toString()
                   );

         exception.initCause( ioobe );

         throw exception;
      }

      this.stringRange.requireInRange
         (
            offsetStringRange,
            () -> new Message()
                     .title( "CharSequenceWindow::subSequence, specified window range is out of this CharSequenceWindow's range." )
                     .indentInc()
                     .segment( "CharSequenceWindow", this  )
                     .segment( "Start",              start )
                     .segment( "End",                end   )
                     .toString()
         );
      //@formatter:on

      return new CharSequenceWindow(this.charSequence, offsetStringRange);
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
         .title( "CharSequenceWindow" )
         .indentInc()
         .segment( "CharSequence", this.charSequence )
         .segment( "StringRange",  this.stringRange  )
         .indentDec()
         ;
      //@formatter:off

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {

      if (this.stringRange.isPositive()) {

         return this.charSequence.subSequence(this.stringRange.start(), this.stringRange.end()).toString();

      } else {

         var charArray = new char[this.stringRange.absLength()];

         //@formatter:off
         for( int i = this.stringRange.start() - 1, e = this.stringRange.end(), j = 0;
              i >= e;
              i--, j++ ) {
         //@formatter:on

            charArray[j] = this.charSequence.charAt(i);
         }

         return new String(charArray);
      }
   }
}

/* EOF */
