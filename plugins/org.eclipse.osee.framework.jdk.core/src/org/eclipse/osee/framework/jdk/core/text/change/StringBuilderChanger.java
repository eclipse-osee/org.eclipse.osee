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

package org.eclipse.osee.framework.jdk.core.text.change;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * An implementation of the {@link CharacterChanger} interface for {@link StringBuilder} objects.
 *
 * @author Loren K. Ashley
 */

class StringBuilderChanger implements CharacterChanger, ToMessage {

   /**
    * The inclusive starting index of the character range to be replaced in the {@link ChangeSet}.
    */

   private final int srcStartIndex;

   /**
    * The exclusive ending index of the character range to be replaced in the {@link ChangeSet}.
    */

   private final int srcEndIndex;

   /**
    * The number of characters in the {@link ChangeSet} to be replaced.
    */

   private final int length;

   /**
    * A pointer to the next {@link CharacterChanger} in the linked list of {@link CharacterChanger} objects owned by the
    * {@link ChangeSet}.
    */

   private CharacterChanger next;

   /**
    * A {@link StringBuilder} containing the new text.
    */

   private final StringBuilder stringBuilder;

   /**
    * Creates a {@CharacterChanger} implementation where the change text is backed by a {@link StringBuilder}.
    *
    * @param srcStartIndex the inclusive starting index of the character range to be replaced in the {@link ChangeSet}.
    * @param srcEndIndex the exclusive ending index of the character range to be replaced in the {@link ChangeSet}.
    * @param stringBuilder the replacement text.
    * @throws NullPointerException when the parameter <code>stringBuilder</code> is <code>null</code>.
    * @throws IndexOutOfBoundsException when any of the following are true:
    * <ul>
    * <li><code>srcStartIndex</code> is less than zero, or</li>
    * <li><code>srcEndIndex</code> is less than <code>srcStartIndex</code>.</li>
    * </ul>
    */

   public StringBuilderChanger(int srcStartIndex, int srcEndIndex, StringBuilder stringBuilder) {
      this.stringBuilder = Objects.requireNonNull(stringBuilder,
         "StringBuilderChanger::new, parameter \"stringBuilder\" cannot be null.");
      this.srcStartIndex = srcStartIndex;
      this.srcEndIndex = srcEndIndex;
      this.length = srcEndIndex - srcStartIndex;
      this.next = null;

      //@formatter:off
      if (
               ( this.srcStartIndex < 0 )
            || ( this.srcEndIndex < this.srcStartIndex ) ) {

         throw
            new IndexOutOfBoundsException
                   (
                      new Message()
                             .title( "StringBuilderChanger::new, change set start and/or end index out of bounds." )
                             .indentInc()
                             .segment( "srcStartIndex", srcStartIndex )
                             .segment( "srcEndIndex",   srcEndIndex   )
                             .toString()
                   );
      }
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    * <p>
    * Gets the inclusive starting index of the character range to be replaced in the {@link ChangeSet}.
    */

   @Override
   public int getStartIndex() {
      return this.srcStartIndex;
   }

   /**
    * {@inheritDoc}
    * <p>
    * Gets the exclusive ending index of the character range to be replaced in the {@link ChangeSet}.
    */

   @Override
   public int getEndIndex() {
      return this.srcEndIndex;
   }

   /**
    * {@inheritDoc}
    * <p>
    * Copies the replacement text into the <code>dest</code> character array at the position specified by
    * <code>destPos</code>.
    *
    * @param dest the <code>char</code> array to write the replacement text into.
    * @param destPos the index in the <code>dest</code> array to copy the replacement text to.
    * @return the <code>destPos</code> plus the number of characters copied.
    * @throws IndexOutOfBoundsException when any of the following are true:
    * <ul>
    * <li><code>dstPos</code> is negative, or</li>
    * <li><code>dstPos</code> plus the number of replacement characters is greater than <code>dest.length</code>.</li>
    * </ul>
    */

   @Override
   public int applyChange(char[] dest, int destPos) {
      this.stringBuilder.getChars(0, this.stringBuilder.length(), dest, destPos);
      return destPos + this.stringBuilder.length();
   }

   /**
    * {@inheritDoc}
    * <p>
    * Copies the replacement text into a {@link Writer}.
    *
    * @param writer the {@link Writer} to copy the replacement text into.
    * @throws IOException when an I/O error occurs.
    */

   @Override
   public void applyChange(Writer writer) throws IOException {
      writer.append(this.stringBuilder);
   }

   /**
    * {@inheritDoc}
    * <p>
    * Gets the {@link CharacterChanger} linked list {@link #next} reference.
    *
    * @return the next {@link CharacterChanger}.
    */

   @Override
   public CharacterChanger next() {
      return this.next;
   }

   /**
    * {@inheritDoc}
    * <p>
    * Sets the {@link CharacterChanger} linked list {@link #next} reference.
    */

   @Override
   public void setNext(CharacterChanger next) {
      this.next = next;
   }

   /**
    * {@inheritDoc}
    * <p>
    * The difference between the number of replacement characters and the number of characters to be replaced.
    *
    * @return the number of replacement characters minus the number of characters to be replaced.
    */

   @Override
   public int getLengthDelta() {
      return this.stringBuilder.length() - this.length;
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
         .title( "StringBuilderChanger" )
         .indentInc()
         .segment( "srcStartIndex",        this.srcStartIndex          )
         .segment( "srcEndIndex",          this.srcEndIndex            )
         .segment( "length",               this.length                 )
         .segment( "stringBulder",         this.stringBuilder          )
         .segment( "stringBuilder length", this.stringBuilder.length() )
         .segment( "next",                 Objects.nonNull( this.next ) ? "set" : "unset" )
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
