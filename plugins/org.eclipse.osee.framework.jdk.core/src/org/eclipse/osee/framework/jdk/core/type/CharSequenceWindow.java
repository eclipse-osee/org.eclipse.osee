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

/**
 * Provides a {@link CharSequence} implementation for a portion of a {@link CharSequence}. If the backing
 * {@link CharSequence} is mutable, changes to the contents of the backing {@link CharSequence} will be reflected in the
 * {@link CharSequenceWindow} and the {@link CharSequenceWindow} end points may become out of range resulting in an
 * {@link IndexOutOfBoundsException}.
 * <p>
 * The purpose of this class is to provide a &quot;substring view&quot; of a {@link CharSequence} without making a copy
 * of the source data.
 *
 * @author Loren K. Ashley
 */

public class CharSequenceWindow implements CharSequence {

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
    * @param start the inclusive starting character position for the {@link CharSequenceWindow} within the provided
    * {@link CharSequence}.
    * @throws NullPointerException when the parameter <code>charSequence</code> is <code>null</code>.
    * @throws IndexOutOfBounds when:
    * <ul>
    * <li><code>start</code> is less than zero or,</li>
    * <li><code>start</code> is greater than the string length.</li>
    * </ul>
    */

   public CharSequenceWindow(CharSequence charSequence, int start) {
      this.charSequence =
         Objects.requireNonNull(charSequence, "CharSequenceWindow::new, parameter \"charSequence\" cannot be null.");
      //@formatter:off
      try {
         this.stringRange =
            new StringRange
                   (
                      start,
                      charSequence.length(),
                        StringRange.NonNegativeEndpoints
                      | StringRange.NonNegativeRange
                   );
      } catch( IndexOutOfBoundsException ioobe ) {
         var exception =
            new IndexOutOfBoundsException
                   (
                      new Message()
                             .title( "CharSequenceWindow::new, window range out of bounds." )
                             .indentInc()
                             .segment( "CharSequence", charSequence )
                             .segment( "Start",        start        )
                             .toString()
                   );
         exception.initCause( ioobe );
         throw exception;
      }
      //@formatter:on
   }

   /**
    * Creates a {@link CharSequenceWindow} implementation for a portion of the provided {@link CharSequecne} between the
    * inclusive starting position and the exclusive ending position.
    *
    * @param charSequence the {@link CharSequence} to create a window for.
    * @param start the inclusive starting character position for the {@link CharSequenceWindow} within the provided
    * {@link CharSequence}.
    * @param end the exclusive character position for the end of the {@link CharSequenceWindow} within the provided
    * {@link CharSequence}.
    * @throws NullPointerException when the parameter <code>charSequence</code> is <code>null</code>.
    * @throws IndexOutOfBounds when:
    * <ul>
    * <li><code>start</code> is less than zero,</li>
    * <li><code>end</code> is less than zero,</li>
    * <li><code>end</code> is less than <code>start</code>, or</li>
    * <li><code>end</code> is greater than the character sequence length.</li>
    * </ul>
    */

   public CharSequenceWindow(CharSequence charSequence, int start, int end) {
      this.charSequence =
         Objects.requireNonNull(charSequence, "CharSequenceWindow::new, parameter \"charSequence\" cannot be null.");
      //@formatter:off
      if(end > charSequence.length() ) {
         throw
            new IndexOutOfBoundsException
                   (
                      new Message()
                             .title( "CharSequenceWindow::new, parameter \"end\" past end of \"charSequence\"." )
                             .indentInc()
                             .segment( "CharSequence", charSequence )
                             .segment( "Start",        start        )
                             .segment( "End",          end          )
                             .toString()
                   );
      }

      try
      {
         this.stringRange =
            new StringRange
                   (
                      start,
                      end,
                        StringRange.NonNegativeEndpoints
                      | StringRange.NonNegativeRange
                   );
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
    * Creates a {@link CharSequenceWindow} implementation for a portion of the provided {@link CharSequence}.
    *
    * @param charSequence the {@link CharSequence} to create a window for.
    * @param stringRange a {@link StringRange} containing the inclusive starting and exclusive ending character
    * positions for the {@link CharSequenceWindow} within the provided {@link CharSequenced}.
    * @throws NullPointerException when the parameter <code>charSequence</code> is <code>null</code>.
    * @throws IndexOutOfBounds when:
    * <ul>
    * <li><code>stringRange</code> has negative end points,</li>
    * <li><code>stringRange</code> is a negative range, or</li>
    * <li><code>stringRange</code> end is greater than the character sequence length.</li>
    * </ul>
    */

   private CharSequenceWindow(CharSequence charSequence, StringRange stringRange) {
      this.charSequence =
         Objects.requireNonNull(charSequence, "CharSequenceWindow::new, parameter \"charSequence\" cannot be null.");
      //@formatter:off
      if(stringRange.end() > charSequence.length() ) {
         throw
            new IndexOutOfBoundsException
                   (
                      new Message()
                             .title( "CharSequenceWindow::new, parameter \"stringRange\" past end of \"charSequence\"." )
                             .indentInc()
                             .segment( "CharSequence", charSequence )
                             .segment( "Range",        stringRange  )
                             .toString()
                   );
      }

      try {
         this.stringRange =
            new StringRange
                   (
                      stringRange.start(),
                      stringRange.end(),
                        StringRange.NonNegativeEndpoints
                      | StringRange.NonNegativeRange
                   );
      } catch( IndexOutOfBoundsException ioobe ) {
         var exception =
            new IndexOutOfBoundsException
                   (
                      new Message()
                      .title( "CharSequenceWindow::new, window range out of bounds." )
                      .indentInc()
                      .segment( "CharSequence", charSequence )
                      .segment( "Range",        stringRange  )
                      .toString()
                   );
         exception.initCause( ioobe );
         throw exception;
      }
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int length() {
      return this.stringRange.length();
   }

   /**
    * {@inheritDoc}
    *
    * @throws IndexOutOfBoundsException {@inheritDoc}
    */

   @Override
   public char charAt(int index) {
      var stringIndex = this.stringRange.start() + index;
      this.stringRange.requireInRange(stringIndex);
      return this.charSequence.charAt(stringIndex);
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
            new StringRange
                   (
                      start,
                      end,
                      this.stringRange.start(),
                        StringRange.NonNegativeEndpoints
                      | StringRange.NonNegativeRange
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
   public String toString() {
      return this.charSequence.subSequence(this.stringRange.start(), this.stringRange.end()).toString();
   }
}

/* EOF */
