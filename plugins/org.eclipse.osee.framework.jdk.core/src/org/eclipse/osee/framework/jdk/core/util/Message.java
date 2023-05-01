/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.framework.jdk.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class for building formatted debug, error, and or exception messages.
 *
 * @author Loren K. Ashley
 */

public class Message {

   /**
    * Internal class to encapsulate the data for a message line. {@link Message} lines can be one of the following
    * forms:
    * <dl>
    * <dt>{@link LineType#BLANK}:</dt>
    * <dd>
    *
    * <pre>
    * "\n"
    * </pre>
    *
    * </dd>
    * <dt>{@link LineType#BLOCK}:</dt>
    * <dd>
    *
    * <pre>
    *    &lt;block-text&gt; "\n"
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>block-text</dt>
    * <dd>A single or multi-line block of text.
    * </dl>
    * </dd>
    * <dt>{@link LineType#SEGMENT}:</dt>
    * <dd>
    *
    * <pre>
    *    &lt;title&gt; ":" &lt;space&gt; &lt;value&gt; "\n"
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>title</dt>
    * <dd>A title for the value to be displayed.</dd>
    * <dt>space</dt>
    * <dd>A generated sequence of spaces to align the columns the value string start in.</dd>
    * <dt>value</dt>
    * <dd>String representation of the value.</dd>
    * </dl>
    * </dd>
    * <dt>{@link LineType#TITLE}:</dt>
    * <dd>
    *
    * <pre>
    *    &lt;title&gt; ":\n"
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>title</dt>
    * <dd>The text to be displayed.</dd>
    * </dl>
    */

   private class Line {

      /**
       * The indent level for the message line. Message lines can be one of the following types:
       */

      private final int indent;

      /**
       * Flag indicates the type of message line.
       */

      private final LineType lineType;

      /**
       * Saves the title text.
       */

      private CharSequence title;

      /**
       * Saves the string representation of the value.
       */

      private final CharSequence value;

      /**
       * Creates a new blank {@link Line}.
       */

      Line() {
         this.lineType = LineType.BLANK;
         this.indent = 0;
         this.title = "";
         this.value = "";
      }

      /**
       * Creates a new {@link Line} for an unindented block of text.
       *
       * @param title the single or multi-line text block.
       */

      Line(CharSequence title) {
         this.lineType = LineType.BLOCK;
         this.indent = -1;
         this.title = Message.copy(title);
         this.value = "";
      }

      /**
       * Creates a new {@link Line} for an indented title.
       *
       * @param indent the indent level for the title.
       * @param title the title text.
       */

      Line(int indent, CharSequence title) {
         this.lineType = LineType.TITLE;
         this.indent = indent;
         this.title = Message.copy(title);
         this.value = "";
      }

      /**
       * Creates a new {@link Line} for an indented title and column aligned value string.
       *
       * @param indent the indent level for the title.
       * @param title the title text.
       * @param value the string representation of the value.
       */

      Line(int indent, CharSequence title, CharSequence value) {
         this.lineType = LineType.SEGMENT;
         this.indent = indent;
         this.title = Message.copy(title);
         this.value = Message.copy(value);
      }

      /**
       * Creates a new {@link Line} that is a copy of another with an additional indent.
       *
       * @implNote If the title or value text in the other {@link Line} is not saved in an immutable {@link String}, a
       * new {@link StringBuilder} will be created and the text from the other {@link Line} will be copied into it.
       * @param indent the additional indent levels.
       * @param otherLine the other {@link Line} to be copied.
       */

      Line(int indent, Line otherLine) {
         this.lineType = otherLine.lineType;
         this.indent = indent + (otherLine.indent >= 0 ? otherLine.indent : 0);
         this.title = Message.copy(otherLine.title);
         this.value = Message.copy(otherLine.value);
      }

      /**
       * Appends the {@link Line} content to the {@link StringBuilder} according to the {@link Line#lineType}.
       *
       * @param message {@link StringBuilder} to be appended to.
       * @param indentColumnStartArray an array containing the value starting column for each indent level.
       */

      void append(StringBuilder message, int[] indentColumnStartArray) {

         switch (this.lineType) {

            case BLANK: {
               message.append(Message.lineEnding);
               return;
            }

            case BLOCK: {
               for (var line : this.getTitle().toString().split("\\v")) {
                  message.append(line).append(Message.lineEnding);
               }
               return;
            }

            case SEGMENT: {
               var maxIndent = indentColumnStartArray.length - 1;
               var indent = this.getIndent();
               indent = (indent <= maxIndent) ? (indent >= 0) ? indent : 0 : maxIndent;
               message.append(IndentedString.indentString(indent));
               message.append(this.getTitle()).append(": ");
               var space = indentColumnStartArray[indent] - this.title.length();
               for (int i = 0; i < space; i++) {
                  message.append(" ");
               }
               message.append(this.getValue()).append(Message.lineEnding);
               return;
            }

            case TITLE: {
               message.append(IndentedString.indentString(this.getIndent()));
               message.append(this.getTitle()).append(Message.lineEnding);
               return;
            }
         }

      }

      /**
       * Appends the provided text to the {@link Line#title}. If the existing storage is <code>null</code> or a
       * {@link String} the storage will be converted to a {@link StringBuilder}.
       *
       * @param text the text to be appended.
       */

      void appendTitle(CharSequence text) {

         switch (this.lineType) {
            case TITLE: {
               if (Objects.isNull(this.title)) {
                  this.title = new StringBuilder(Message.newSize(text.length()));
               } else if (this.title instanceof String) {
                  this.title =
                     new StringBuilder(Message.newSize(this.title.length() + text.length())).append(this.title);
               }
               ((StringBuilder) this.title).append(text);
            }
            default:
         }
      }

      /**
       * Gets the indent level for the {@link Line}.
       *
       * @return the line's indent level.
       */

      int getIndent() {
         return this.indent;
      }

      /**
       * Gets the {@link LineType} of the {@link Line}.
       *
       * @return the {@link LineType}.
       */

      LineType getLineType() {
         return this.lineType;
      }

      /**
       * Gets the length of the title for a {@link LineType#SEGMENT} {@link Line}. For non-{@link LineType#SEGMENT}
       * {@link Line}s zero is returned.
       *
       * @return if the {@link Line} {@link LineType} is {@link LineType#SEGMENT}, the length of the member
       * {@link Line#title}; otherwise, zero.
       */

      int getSegmentTitleLength() {

         switch (this.lineType) {

            case SEGMENT: {
               return this.title.length();
            }

            default: {
               return 0;
            }
         }
      }

      /**
       * Gets the title for the {@link Line}.
       *
       * @return the title as a {@link CharSequence}.
       */

      CharSequence getTitle() {
         assert Objects.nonNull(this.title) : "Message.Line::getTitle, member \"title\" is null.";

         return this.title;
      }

      /**
       * Gets the value for the {@link Line}.
       *
       * @return the value as a {@link CharSequence}.
       */

      CharSequence getValue() {
         return this.value;
      }

      /**
       * Estimates the number of characters needed in a {@link StringBuilder} to accommodate the {@link Line}.
       *
       * @param indentColumnStartArray an array containing the value starting column for each indent level.
       * @return the estimated number of character in the array.
       */

      int size(int[] indentColumnStartArray) {

         switch (this.lineType) {

            case BLANK: {
               return Message.lineEndingSize;
            }

            case BLOCK: {
               return this.title.length() + Message.lineEndingSize;
            }

            case SEGMENT: {
               var maxIndent = indentColumnStartArray.length - 1;
               var indent = this.getIndent();
               indent = (indent <= maxIndent) ? (indent >= 0) ? indent : 0 : maxIndent;
               //@formatter:off
               return
                    IndentedString.indentSize() * this.getIndent()
                  + indentColumnStartArray[indent]
                  + this.value.length()
                  + Message.lineEndingSize;
              //@formatter:on
            }

            case TITLE: {
               //@formatter:off
               return
                    IndentedString.indentSize() * this.getIndent()
                  + this.title.length()
                  + Message.lineEndingSize;
               //@formatter:on
            }

            default: {
               return 0;
            }
         }
      }
   }

   /**
    * Enumeration of the types of lines in a message.
    */

   private enum LineType {

      /**
       * An empty line.
       */

      BLANK,

      /**
       * A single or multi-line block of text.
       */

      BLOCK,

      /**
       * A line with a title followed by column aligned value.
       */

      SEGMENT,

      /**
       * A line with just a title.
       */

      TITLE;
   }

   /**
    * The character sequence used for line endings.
    */

   private static final String lineEnding = "\n";

   /**
    * The length of the line ending character sequence.
    */

   private static final int lineEndingSize = lineEnding.length();

   /**
    * Makes a copy of text from another {@link CharSequence}. When the other text is a {@link String}, the
    * {@link String} is returned. When the other text is not a {@link String}, a new {@link StringBuilder} is created
    * and loaded with the contents of the other text. When the other text is <code>null</code> an empty {@link String}
    * is returned.
    *
    * @param otherText the {@link CharSequence} to copy.
    * @return the copy {@link CharSequence}.
    */

   private static CharSequence copy(CharSequence otherText) {
      if (Objects.isNull(otherText)) {
         return "";
      }
      if (otherText instanceof String) {
         return otherText;
      }
      return new StringBuilder(Message.newSize(otherText.length())).append(otherText);
   }

   /**
    * Calculates the size for a {@link StringBuilder} from the initial size of the text to be stored in it.
    *
    * @implNote The new size is the larger of 256 or twice the text size.
    * @param textSize the size of the text to be stored.
    * @return the calculated size for the new {@link StringBuidler}.
    */

   private static int newSize(int textSize) {
      var newSize = textSize * 2;
      newSize = newSize < 256 ? 256 : newSize;
      return newSize;
   }

   /**
    * Generates a string representation of the object using the method {@link Object#toString}.
    *
    * @param object the object. This parameter maybe <code>null</code>.
    * @return when object is not <code>null</code>, {@link Object#toString}; otherwise, "(null)".
    */

   private static String objectToString(Object object) {
      //@formatter:off
      return
         Objects.nonNull( object )
            ? object.toString()
            : "(null)";
      //@formatter:on
   }

   /**
    * A reference to the generated message string is saved in this member. Methods that modify the {@link Message} will
    * invalidate this cache.
    */

   private String cachedResult;

   /**
    * The indent level for the line. The actual number of spaces in the indent is determined by the class
    * {@link IndentedString}.
    */

   private int indent;

   /**
    * A {@link List} of the lines in the {@Link Message}.
    */

   private final List<Line> lines;

   /**
    * Creates a new empty {@link Message}.
    */

   public Message() {
      this.lines = new LinkedList<>();
      this.indent = 0;
      this.cachedResult = null;
   }

   /**
    * Appends the {@link CharSequence} to the last line, if it was a title. If there are no lines or the last line in
    * the {@link Message} was not a title, the additional text will be ignored.
    *
    * @param text the {@link CharSequence} to append.
    * @return this {@link Message}
    */

   public Message append(CharSequence text) {

      this.cachedResult = null;

      var size = this.lines.size();

      if (size <= 0) {
         return this;
      }

      var lastLine = this.lines.get(size - 1);

      if (LineType.TITLE.equals(lastLine.getLineType())) {
         lastLine.appendTitle(text);
      }

      return this;
   }

   /**
    * Adds a new empty line to the {@link Message}.
    *
    * @return the {@link Message}.
    */

   public Message blank() {
      this.cachedResult = null;
      this.lines.add(new Line());
      return this;
   }

   /**
    * Adds an unindented line or multi-line block of text to the {@link Message}.
    *
    * @param block the text block as a {@link CharSequece}.
    * @return this {@link Message}.
    */

   public Message block(CharSequence block) {
      this.cachedResult = null;
      this.lines.add(new Line(block));
      return this;
   }

   /**
    * Copies {@link Line}s from another {@link Message} and appends the copied {@link Line}s to this {@link Message}.
    *
    * @param otherMessage the {@link Message} to copy.
    * @return this {@link Message}.
    */

   public Message copy(Message otherMessage) {
      this.cachedResult = null;
      otherMessage.lines.forEach((otherLine) -> this.lines.add(new Line(this.indent, otherLine)));
      return this;
   }

   /**
    * Adds a title line with an indented text block. The lines will be formatted as follows:
    *
    * <pre>
    *    &lt;indent(n)&gt;   &lt;title&gt;
    *    &lt;indent(n+1)&gt; &lt;block&gt;
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>indent:</dt>
    * <dd>is an indent string the message's current indent level n.</dd>
    * <dt>title:</dt>
    * <dd>is the provided title text.</dd>
    * <dt>block</dt>
    * <dd>is the provided block text.</dd>
    * </dl>
    *
    * @param title the text for the title line.
    * @param block the text for indent text block.
    * @return this {@link Message}.
    * @throws NullPointerException when the either of the parameters <code>title</code> or <code>block</code> are
    * <code>null</code>.
    */

   public Message follows(CharSequence title, CharSequence block) {
      Objects.requireNonNull(title, "Message::follows, parameter \"title\" cannot be null.");
      Objects.requireNonNull(block, "Message::follows, parameter \"block\" cannot be null.");
      //@formatter:off
      return
         this
            .title( title )
            .indentInc()
            .block( block )
            .indentDec();
      //@formatter:on
   }

   /**
    * When the parameter <code>block</code> is non-<code>null</code>, adds a follows message as if the method
    * {@link Message#follows} had been called; otherwise, no action is performed.
    *
    * @param title the text for the title line.
    * @param block the text for indent text block.
    * @return this {@link Message}.
    * @throws NullPointerException when the parameter <code>title</code> is <code>null</code>.
    */

   public Message followsIfNonNull(CharSequence title, CharSequence block) {
      return Objects.nonNull(block) ? this.follows(title, block) : this;
   }

   /**
    * When the parameter <code>blockOptional</code> is non-<code>null</code> and has a value present, adds a follows
    * message as if the method {@link Message#follows} had been called; otherwise, no action is performed.
    *
    * @param title the text for the title line.
    * @param block a possibly empty {@link Optional} containing the block text to be added to the {@link Message}. This
    * parameter may be <code>null</code>.
    * @return this {@link Message}.
    * @throws NullPointerException when the parameter <code>title</code> is <code>null</code>.
    */

   public Message followsIfPresent(CharSequence title, Optional<CharSequence> blockOptional) {
      //@formatter:off
      return Objects.nonNull( blockOptional ) && blockOptional.isPresent()
                ? this.follows(title, blockOptional.get())
                : this;
   }

   /**
    * Sets the current indent level with a floor of zero for the {@link Message}.
    *
    * @param indent the new indent level.
    * @return this {@link Message}.
    */

   public Message indent(int indent) {
      //@formatter:off
      this.indent =
         indent > 0
            ? indent
            : indent == -1
                 ? this.indent
                 : 0;
      //@formatter:on

      return this;
   }

   /**
    * Reduces the current indent level by one with a floor of zero.
    *
    * @return this {@link Message}.
    */

   public Message indentDec() {
      this.indent = this.indent > 0 ? this.indent - 1 : 0;
      return this;
   }

   /**
    * Increases the current indent level by one.
    *
    * @return this {@link Message}.
    */

   public Message indentInc() {
      this.indent++;
      return this;
   }

   /**
    * Predicate to determine if the {@link Message} is empty.
    *
    * @return <code>true</code>, when the {@link Message} is empty; otherwise, <code>false</code>.
    */

   public boolean isEmpty() {
      return this.lines.size() == 0;
   }

   /**
    * Adds a title line with a block containing the message from the throwable. The lines will be formatted as follows:
    *
    * <pre>
    *    &lt;indent(n)&gt;   &lt;title&gt;
    *    &lt;indent(n+1)&gt; &lt;exception-class&gt;
    *    &lt;indent(n+1)&gt; &lt;exception-message&gt;
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>indent:</dt>
    * <dd>is an indent string the message's current indent level n.</dd>
    * <dt>title:</dt>
    * <dd>is the string from parameter <code>title</code>.</dd>
    * <dt>exception-class</dt>
    * <dd>it the class name of the {@link Throwable}.</dd>
    * <dt>exception-message</dt>
    * <dd>is the string from parameter <code>throwable</code> {@link Throwable#getMessage} method.</dd>
    * </dl>
    *
    * @param throwable the {@link Throwable} whose message is to be added to the {@link Message}.
    * @return this {@link Message}.
    * @throws NullPointerException when the parameter <code>throwable</code> is <code>null</code>.
    */

   public Message reasonFollows(String title, Throwable throwable) {
      Objects.requireNonNull(throwable, "Message::reasonFollows, parameter \"throwable\" cannot be null.");
      var exceptionClass = throwable.getClass().getName();
      var block = throwable.getMessage();
      if (Objects.isNull(block)) {
         block = "(null message)";
      }
      this.title(title);
      this.indentInc();
      this.title(exceptionClass);
      this.title(block);
      this.indentDec();
      return this;
   }

   /**
    * Adds a title line with a block containing the message from the throwable. The lines will be formatted as follows:
    *
    * <pre>
    *    &lt;indent(n)&gt;   &lt;title&gt;
    *    &lt;indent(n+1)&gt; &lt;block&gt;
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>indent:</dt>
    * <dd>is an indent string the message's current indent level n.</dd>
    * <dt>title:</dt>
    * <dd>is the string "Reason Follows:"</dd>
    * <dt>block</dt>
    * <dd>is the string from parameter <code>throwable</code> {@link Throwable#getMessage} method.</dd>
    * </dl>
    *
    * @param throwable the {@link Throwable} whose message is to be added to the {@link Message}.
    * @return this {@link Message}.
    * @throws NullPointerException when the parameter <code>throwable</code> is <code>null</code>.
    */

   public Message reasonFollows(Throwable throwable) {
      return this.reasonFollows("Reason Follows", throwable);
   }

   /**
    * When the parameter <code>throwable</code> is non-<code>null</code>, adds a reason follows message as if the method
    * {@link Message#reasonFollows} had been called; otherwise, no action is performed.
    *
    * @param throwable the {@link Throwable} whose message is to be added to the {@link Message}. This parameter maybe
    * <code>null</code>.
    * @return this {@link Message}.
    */

   public Message reasonFollowsIfNonNull(Throwable throwable) {
      return Objects.nonNull(throwable) ? this.reasonFollows(throwable) : this;
   }

   /**
    * When the parameter <code>throwableOptional</code> is non-<code>null</code> and has a value present, adds a reason
    * follows message as if the method {@link Message#reasonFollows} had been called with
    * <code>throwableOptional.get()</code>; otherwise, no action is performed.
    *
    * @param throwable a possibly empty {@link Optional} containing the {@link Throwable} whose message is to be added
    * to the {@link Message}.
    * @return this {@link Message}.
    */

   public Message reasonFollowsIfPresent(Optional<? extends Throwable> throwableOptional) {
      //@formatter:off
      return Objects.nonNull(throwableOptional) && throwableOptional.isPresent()
                ? this.reasonFollows(throwableOptional.get())
                : this;
      //@formatter:on
   }

   /**
    * Adds a title line with a block containing the message from the throwable. The lines will be formatted as follows:
    *
    * <pre>
    *    &lt;indent(n)&gt;   &lt;title&gt;
    *    &lt;indent(n+1)&gt; &lt;exception-class&gt;
    *    &lt;indent(n+1)&gt; &lt;exception-message&gt;
    *    &lt;indent(n+1)&gt; &lt;exception-trace&gt;
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>indent:</dt>
    * <dd>is an indent string the message's current indent level n.</dd>
    * <dt>title:</dt>
    * <dd>is the string from parameter <code>title</code>.</dd>
    * <dt>exception-class</dt>
    * <dd>it the class name of the {@link Throwable}.</dd>
    * <dt>exception-message</dt>
    * <dd>is the string from parameter <code>throwable</code> {@link Throwable#getMessage} method.</dd>
    * <dt>exception-trace</dt>
    * <dd>the printed trace from the {@link Throwable}.</dd>
    * </dl>
    *
    * @param throwable the {@link Throwable} whose message is to be added to the {@link Message}.
    * @return this {@link Message}.
    * @throws NullPointerException when the parameter <code>throwable</code> is <code>null</code>.
    */

   public Message reasonFollowsWithTrace(String title, Throwable throwable) {
      Objects.requireNonNull(throwable, "Message::reasonFollows, parameter \"throwable\" cannot be null.");
      var exceptionClass = throwable.getClass().getName();
      var block = throwable.getMessage();
      if (Objects.isNull(block)) {
         block = "(null message)";
      }
      var stringWriter = new StringWriter();
      var printWriter = new PrintWriter(stringWriter);
      throwable.printStackTrace(printWriter);
      var trace = stringWriter.toString();
      if (Objects.isNull(trace)) {
         trace = "(null trace)";
      }
      this.title(title);
      this.indentInc();
      this.title(exceptionClass);
      this.title(block);
      this.title(trace);
      this.indentDec();
      return this;
   }

   /**
    * Adds a title line with a block containing the message from the throwable. The lines will be formatted as follows:
    *
    * <pre>
    *    &lt;indent(n)&gt;   &lt;title&gt;
    *    &lt;indent(n+1)&gt; &lt;exception-class&gt;
    *    &lt;indent(n+1)&gt; &lt;exception-message&gt;
    *    &lt;indent(n+1)&gt; &lt;exception-trace&gt;
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>indent:</dt>
    * <dd>is an indent string the message's current indent level n.</dd>
    * <dt>title:</dt>
    * <dd>is the string "Reason Follows".</dd>
    * <dt>exception-class</dt>
    * <dd>it the class name of the {@link Throwable}.</dd>
    * <dt>exception-message</dt>
    * <dd>is the string from parameter <code>throwable</code> {@link Throwable#getMessage} method.</dd>
    * <dt>exception-trace</dt>
    * <dd>the printed trace from the {@link Throwable}.</dd>
    * </dl>
    *
    * @param throwable the {@link Throwable} whose message is to be added to the {@link Message}.
    * @return this {@link Message}.
    * @throws NullPointerException when the parameter <code>throwable</code> is <code>null</code>.
    */

   public Message reasonFollowsWithTrace(Throwable throwable) {
      return this.reasonFollowsWithTrace("Reason Follows", throwable);
   }

   /**
    * Adds a multi-line segment generated from the specified object's {@link ToMessage#toMessage} method.
    *
    * @param toMessage an object implementing the {@link ToMessage} interface.
    * @return this {@link Message}.
    */

   public Message segmentToMessage(ToMessage toMessage) {
      return toMessage.toMessage(this.indent, this);
   }

   /**
    * Adds a multi-line segment with a title generated from the specified object's {@link ToMessage#toMessage} method.
    *
    * @param toMessage an object implementing the {@link ToMessage} interface.
    * @return this {@link Message}.
    */

   public Message segmentToMessage(CharSequence title, ToMessage toMessage) {
      this.title(title);
      this.indentInc();
      toMessage.toMessage(this.indent, this);
      this.indentDec();
      return this;
   }

   /**
    * Adds a new segment line with the value string generated from a {@link List}. This method behaves as though the
    * following method were called:
    *
    * <pre>
    * message.segment(&quot;title&quot;, theList, Function.identity());
    * </pre>
    *
    * @param <T> the type of values on the <code>valueList</code>.
    * @param title the title {@link CharSequence} for the line.
    * @param valueList a list of values to generate the value string from.
    * @return this {@link Message}.
    */

   public <T> Message segment(CharSequence title, List<T> valueList) {
      return this.segment(title, valueList, Function.identity());
   }

   /**
    * Adds a new segment line with a title and a value string generated from a {@link List}. The invocation of this
    * method generates the value string from the <code>valueList</code>. Changes to the <code>valueList</code> after
    * this method completes will not be reflected in the {@link Message}. The segment line will be formatted as follows:
    *
    * <pre>
    *    &lt;title&gt; &quot;: &quot; &lt;space&gt; &quot;[ &quot; [ &lt;element-value&gt; { &quot;, &quot; &lt;element-value&gt;  } ] &quot; ]&quot;
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>title</dt>
    * <dd>A title for the value to be displayed.</dd>
    * <dt>space</dt>
    * <dd>A generated sequence of spaces to align the columns the value string start in.</dd>
    * <dt>element-value</dt>
    * <dd>The <code>valueExtractor</code> function is applied to each element of the <code>valueList</code> and the
    * method {@link #toString} is used to generate a string representation of each value.</dd>
    * </dl>
    *
    * @param <T> the type of values on the <code>valueList</code>.
    * @param title the title {@link CharSequence} for the line.
    * @param valueList the list of values to generate the value string from.
    * @param valueExtractor a {@link Function} used to extract the value for the message from each list element. If the
    * <code>valueList</code> may contain <code>null</code> elements, the provided <code>valueExtractor</code>
    * {@link Function} should be able to process <code>null</code> inputs without throwing an {@link Exception}.
    * @return the {@link Message}.
    */

   public <T> Message segment(CharSequence title, List<T> valueList, Function<T, Object> valueExtractor) {

      this.cachedResult = null;

      //@formatter:off
      var valueString = Objects.isNull( valueList )
                           ? "(null)"
                           : valueList.isEmpty()
                                ? "(empty)"
                                : valueList.stream().map(valueExtractor).map(Message::objectToString).collect(Collectors.joining(", ","[ "," ]"));
      //@formatter:on

      this.lines.add(new Line(this.indent, title, valueString));

      return this;
   }

   /**
    * Adds a new segment line with the provided value. This method behaves as though the following method were called:
    *
    * <pre>
    * message.segment(&quot;title&quot;, value, Function.identity());
    * </pre>
    *
    * @param <T> the type of the value.
    * @param title the title {@link CharSequence} for the line.
    * @param value the value to generate the value string from.
    * @return this {@link Message}.
    */

   public <T> Message segment(CharSequence title, T value) {
      return this.segment(title, value, Function.identity());
   }

   /**
    * Adds a new segment line with a title and a value string generated by applying the <code>valueExtractor</code> to
    * the provided <code>value</code> and then generating the value string from the <code>valueExtractor</code> result
    * with the method {@link #toString}. The segment line will be formatted as follows:
    *
    * <pre>
    *    &lt;title&gt; &quot;: &quot; &lt;space&gt; &lt;value&gt;
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>title</dt>
    * <dd>A title for the value to be displayed.</dd>
    * <dt>space</dt>
    * <dd>A generated sequence of spaces to align the columns the value string start in.</dd>
    * <dt>value</dt>
    * <dd>The <code>valueExtractor</code> function is applied to the provided <code>value</code> and the method
    * {@link #toString} is used to generate a string representation of the <code>valueExtractor</code> result.</dd>
    * </dl>
    *
    * @param <T> the type of the value.
    * @param title the title {@link CharSequence} for the line.
    * @param value the value to extract the message value from.
    * @param valueExtractor a {@link Function} used to extract the value for the message from the provided value.
    * @return the {@link Message}.
    */

   public <T> Message segment(CharSequence title, T value, Function<T, Object> valueExtractor) {

      this.cachedResult = null;

      //@formatter:off
      var valueString = Objects.isNull( value )
                           ? "(null)"
                           : Message.objectToString( valueExtractor.apply( value ) );
      //@formatter:on

      this.lines.add(new Line(this.indent, title, valueString));

      return this;
   }

   /**
    * Provides the {@link Message} to the <code>appender</code> {@link Consumer} and returns this {@link Message}.
    *
    * @param appender a {@link Consumer} that appends to the provided {@link Message}.
    * @return this {@link Message}.
    */

   public Message segment(Consumer<Message> appender) {
      appender.accept(this);
      return this;
   }

   /**
    * Adds a new segment line when the <code>value</code> is not equal to the <code>notValue</code>. This method behaves
    * as though the following method were called when <code>value</code> is not equal to the <code>notValue</code>:
    *
    * <pre>
    * message.segment(&quot;title&quot;, value, Function.identity());
    * </pre>
    *
    * @param <T> the type of the value.
    * @param title the title {@link CharSequence} for the line.
    * @param value the value to generate the value string from.
    * @param notValue the gate value, the segment line is only added when <code>value</code> is not equal to
    * <code>noValue</code>.
    * @return this {@link Message}.
    */
   public <T> Message segmentIfNot(CharSequence title, T value, T notValue) {
      //@formatter:off
      return
         (    ( Objects.isNull( value ) && Objects.isNull( notValue ) )
           || ( Objects.nonNull( value ) ? value.equals( notValue ) : false ) )
            ? this.segment( title, value )
            : this;
      //@formatter:on
   }

   /**
    * Adds a new segment line with the value string generated from an Array. This method behaves as though the following
    * method were called:
    *
    * <pre>
    * message.segment(&quot;title&quot;, theArray, Function.identity());
    * </pre>
    *
    * @param <T> the type of values in the Array.
    * @param title the title {@link CharSequence} for the line.
    * @param valueArray an array of values to generate the value string from.
    * @return this {@link Message}.
    */

   public <T> Message segmentIndexedArray(CharSequence title, T[] valueArray) {
      return this.segmentIndexedArray(title, valueArray, (t) -> t);
   }

   /**
    * Adds a new segment line with a title and a value string generated from an array. The invocation of this method
    * generates the value string from the array. Changes to the array after this method completes will not be reflected
    * in the {@link Message}. The segment line will be formatted as follows:
    *
    * <pre>
    *    &lt;title&gt; ": " { &lt;null-or-empty&gt; } "\n"
    *    { &lt;indent+2&gt; "[" &lt;index&gt; "]:\n" &lt;indent+3&gt; &lt;member-element-value&gt; }
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>title:</dt>
    * <dd>Is the member name as specified by the parameter <code>title</code>.</dd>
    * <dt>null-or-empty:</dt>
    * <dd>This string is determined by the parameter <code>memberArray</code> as follows:
    * <dl>
    * <dt><code>null</code>:</dt>
    * <dd>the string "(null)".</dd>
    * <dt><code>length</code> == 0:</dt>
    * <dd>the string "(empty)".</dd>
    * <dt>not <code>null</code> and <code>length</code> != 0:</dt>
    * <dd>the empty string.</dd>
    * </dl>
    * <dt>index:</dt>
    * <dd>Is the index of the array element.</dd>
    * <dt>member-element-value:</dt>
    * <dd>Is a string representation of the object's value. When the member array element implements the
    * {@link ToMessage} interface the member value message is generated with the {@link ToMessage#toMessage} method.
    * Otherwise the method {@link ToMessage#toString} is used to generate the member value message.</dd>
    * </dl>
    *
    * @param title the class member name for the member array.
    * @param memberArray the array of objects to generate a member value message for.
    * @return this {@link Message}.
    */

   public <T> Message segmentIndexedArray(CharSequence title, T[] valueArray, Function<T, Object> valueExtractor) {

      this.cachedResult = null;

      if (Objects.isNull(valueArray)) {
         this.lines.add(new Line(this.indent, title, "(null)"));
         return this;
      }

      if (valueArray.length <= 0) {
         this.lines.add(new Line(this.indent, title, "(empty)"));
         return this;
      }

      this.lines.add(new Line(this.indent++, title));

      int i = 0;
      var listElementTitle = new StringBuilder();
      for (var value : valueArray) {
         var displayValue = valueExtractor.apply(value);
         listElementTitle.setLength(0);
         listElementTitle.append("[").append(i).append("]");
         if (value instanceof ToMessage) {
            this.lines.add(new Line(this.indent, listElementTitle));
            ((ToMessage) value).toMessage(this.indent + 1, this);
         } else {
            this.lines.add(new Line(this.indent, listElementTitle.toString(), Message.objectToString(displayValue)));
         }
      }

      this.indent--;

      return this;
   }

   /**
    * Adds a new segment line with the value string generated from a {@link List}. This method behaves as though the
    * following method were called:
    *
    * <pre>
    * message.segment(&quot;title&quot;, theList, Function.identity());
    * </pre>
    *
    * @param <T> the type of values in the {@link List}.
    * @param title the title {@link CharSequence} for the line.
    * @param valueList a @{link List} of values to generate the value string from.
    * @return this {@link Message}.
    */

   public <T> Message segmentIndexedList(CharSequence title, List<T> valueList) {
      return this.segmentIndexedList(title, valueList, (t) -> t);
   }

   public <T> Message segmentIndexedList(CharSequence title, List<T> valueList, Function<T, Object> valueExtractor) {
      return this.segmentIndexedList(title, valueList, valueExtractor, -1);
   }

   /**
    * Adds a new segment line with a title and a value string generated from a {@link List}. The invocation of this
    * method generates the value string from the {@link List}. Changes to the {@link List} after this method completes
    * will not be reflected in the {@link Message}. The segment line will be formatted as follows:
    *
    * <pre>
    *    &lt;title&gt; ": " { &lt;null-or-empty&gt; } "\n"
    *    { &lt;indent+2&gt; "[" &lt;index&gt; "]:\n" &lt;indent+3&gt; &lt;member-element-value&gt; }
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>title:</dt>
    * <dd>Is the member name as specified by the parameter <code>title</code>.</dd>
    * <dt>null-or-empty:</dt>
    * <dd>This string is determined by the parameter <code>memberArray</code> as follows:
    * <dl>
    * <dt><code>null</code>:</dt>
    * <dd>the string "(null)".</dd>
    * <dt><code>length</code> == 0:</dt>
    * <dd>the string "(empty)".</dd>
    * <dt>not <code>null</code> and <code>length</code> != 0:</dt>
    * <dd>the empty string.</dd>
    * </dl>
    * <dt>index:</dt>
    * <dd>Is the index of the {@link List} element.</dd>
    * <dt>member-element-value:</dt>
    * <dd>Is a string representation of the object's value. When the member {@link List} element implements the
    * {@link ToMessage} interface the member value message is generated with the {@link ToMessage#toMessage} method.
    * Otherwise the method {@link ToMessage#toString} is used to generate the member value message.</dd>
    * </dl>
    *
    * @param <T> the type of values in the {@link List}.
    * @param title the title {@link CharSequence} for the line.
    * @param valueList a @{link List} of values to generate the value string from.
    * @param valueExtractor a {@link Function} used to extract the value for the message from each list element.
    * @param limit the maximum number of list values to be included in the message. A value of zero or less disables the
    * limit check.
    * @return this {@link Message}.
    */

   public <T> Message segmentIndexedList(CharSequence title, List<T> valueList, Function<T, Object> valueExtractor,
      int limit) {

      this.cachedResult = null;

      if (Objects.isNull(valueList)) {
         this.lines.add(new Line(this.indent, title, "(null)"));
         return this;
      }

      if (valueList.isEmpty()) {
         this.lines.add(new Line(this.indent, title, "(empty)"));
         return this;
      }

      this.lines.add(new Line(this.indent++, title));

      int safety = 0;
      int i = 0;
      var listElementTitle = new StringBuilder();
      for (var value : valueList) {
         var displayValue = valueExtractor.apply(value);
         listElementTitle.setLength(0);
         listElementTitle.append("[").append(i++).append("]");
         if (value instanceof ToMessage) {
            this.lines.add(new Line(this.indent, listElementTitle));
            ((ToMessage) value).toMessage(this.indent + 1, this);
            this.indent--;
         } else {
            this.lines.add(new Line(this.indent, listElementTitle.toString(), Message.objectToString(displayValue)));
         }

         if ((limit > 0) && (++safety >= limit)) {
            break;
         }
      }

      this.indent--;

      return this;
   }

   /**
    * Adds a new segment line with the value string generated from a {@link Map}. This method behaves as though the
    * following method were called:
    *
    * <pre>
    * message.segmentMap(&quot;title&quot;, theMap, Function.identity());
    * </pre>
    *
    * @param <K> the type of the keys in the {@link Map}.
    * @param <V> the type of values in the {@link Map}.
    * @param title the title {@link CharSequence} for the line.
    * @param valueMap a @{link Map} of keys and values to generate the value string from.
    * @return this {@link Message}.
    */

   public <K, V> Message segmentMap(CharSequence title, Map<K, V> valueMap) {
      return this.segmentMap(title, valueMap, (v) -> v);
   }

   /**
    * Adds a new segment line with a title and a value string generated from a {@link Map}. The invocation of this
    * method generates the value string from the {@link Map}. Changes to the {@link Map} after this method completes
    * will not be reflected in the {@link Message}. The segment line will be formatted as follows:
    *
    * <pre>
    *    &lt;title&gt; ": " { &lt;null-or-empty&gt; } "\n"
    *    { &lt;indent+2&gt; "[" &lt;key&gt; "]:\n" &lt;indent+3&gt; &lt;member-element-value&gt; }
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>title:</dt>
    * <dd>Is the member name as specified by the parameter <code>title</code>.</dd>
    * <dt>null-or-empty:</dt>
    * <dd>This string is determined by the parameter <code>valueMap</code> as follows:
    * <dl>
    * <dt><code>null</code>:</dt>
    * <dd>the string "(null)".</dd>
    * <dt><code>size</code> == 0:</dt>
    * <dd>the string "(empty)".</dd>
    * <dt>not <code>null</code> and <code>size</code> != 0:</dt>
    * <dd>the empty string.</dd>
    * </dl>
    * <dt>key:</dt>
    * <dd>Is the map key of the {@link Map} element.</dd>
    * <dt>member-element-value:</dt>
    * <dd>Is a string representation of the map value. When the member {@link Map} element implements the
    * {@link ToMessage} interface the member value message is generated with the {@link ToMessage#toMessage} method.
    * Otherwise the method {@link ToMessage#toString} is used to generate the member value message.</dd>
    * </dl>
    *
    * @param <K> the type of the keys in the {@link Map}.
    * @param <V> the type of values in the {@link Map}.
    * @param title the title {@link CharSequence} for the line.
    * @param valueMap a @{link Map} of keys and values to generate the value string from.
    * @param valueExtractor a {@link Function} used to extract the value for the message from each map value.
    * @return this {@link Message}.
    */

   public <K, V> Message segmentMap(CharSequence title, Map<K, V> valueMap, Function<V, Object> valueExtractor) {

      this.cachedResult = null;

      if (Objects.isNull(valueMap)) {
         this.lines.add(new Line(this.indent, title, "(null)"));
         return this;
      }

      if (valueMap.isEmpty()) {
         this.lines.add(new Line(this.indent, title, "(empty)"));
         return this;
      }

      this.lines.add(new Line(this.indent++, title));

      var listElementTitle = new StringBuilder();

      for (var entry : valueMap.entrySet()) {
         var displayValue = valueExtractor.apply(entry.getValue());
         listElementTitle.setLength(0);
         listElementTitle.append("[").append(Message.objectToString(entry.getKey())).append("]");
         if (displayValue instanceof ToMessage) {
            this.lines.add(new Line(this.indent, listElementTitle));
            ((ToMessage) displayValue).toMessage(this.indent + 1, this);
            this.indent--;
         } else {
            this.lines.add(new Line(this.indent, listElementTitle.toString(), Message.objectToString(displayValue)));
         }
      }

      this.indent--;

      return this;
   }

   /**
    * Adds a new segment line with the value string generated from a {@link Set}. This method behaves as though the
    * following method were called:
    *
    * <pre>
    * message.segmentSet(&quot;title&quot;, theSet, Function.identity());
    * </pre>
    *
    * @param <V> the type of values in the {@link Set}.
    * @param title the title {@link CharSequence} for the line.
    * @param valueSet a @{link Set} values to generate the value string from.
    * @return this {@link Message}.
    */

   public <V> Message segmentSet(CharSequence title, Set<V> valueSet) {
      return this.segmentSet(title, valueSet, (v) -> v);
   }

   /**
    * Adds a new segment line with a title and a value string generated from a {@link Set}. The invocation of this
    * method generates the value string from the {@link Set}. Changes to the {@link Set} after this method completes
    * will not be reflected in the {@link Message}. The segment line will be formatted as follows:
    *
    * <pre>
    *    &lt;title&gt; ": " { &lt;null-or-empty&gt; } "\n"
    *    { &lt;indent+2&gt; "[" &lt;integer-count&gt; "]:\n" &lt;indent+3&gt; &lt;member-element-value&gt; }
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>title:</dt>
    * <dd>Is the member name as specified by the parameter <code>title</code>.</dd>
    * <dt>null-or-empty:</dt>
    * <dd>This string is determined by the parameter <code>valueSet</code> as follows:
    * <dl>
    * <dt><code>null</code>:</dt>
    * <dd>the string "(null)".</dd>
    * <dt><code>size</code> == 0:</dt>
    * <dd>the string "(empty)".</dd>
    * <dt>not <code>null</code> and <code>size</code> != 0:</dt>
    * <dd>the empty string.</dd>
    * </dl>
    * <dt>integer-count:</dt>
    * <dd>Is incremented for each value drawn from the unordered {@link Set} element.</dd>
    * <dt>member-element-value:</dt>
    * <dd>Is a string representation of the set value. When the member {@link Set} element implements the
    * {@link ToMessage} interface the member value message is generated with the {@link ToMessage#toMessage} method.
    * Otherwise the method {@link ToMessage#toString} is used to generate the member value message.</dd>
    * </dl>
    *
    * @param <V> the type of values in the {@link Set}.
    * @param title the title {@link CharSequence} for the line.
    * @param valueSet a @{link Set} of values to generate the value string from.
    * @param valueExtractor a {@link Function} used to extract the value for the message from each set value.
    * @return this {@link Message}.
    */

   public <V> Message segmentSet(CharSequence title, Set<V> valueSet, Function<V, Object> valueExtractor) {

      this.cachedResult = null;

      if (Objects.isNull(valueSet)) {
         this.lines.add(new Line(this.indent, title, "(null)"));
         return this;
      }

      if (valueSet.isEmpty()) {
         this.lines.add(new Line(this.indent, title, "(empty)"));
         return this;
      }

      this.lines.add(new Line(this.indent++, title));

      var listElementTitle = new StringBuilder();
      int count = 0;

      for (var entry : valueSet) {
         var displayValue = valueExtractor.apply(entry);
         listElementTitle.setLength(0);
         listElementTitle.append("[").append(count++).append("]");
         if (displayValue instanceof ToMessage) {
            this.lines.add(new Line(this.indent, listElementTitle));
            ((ToMessage) displayValue).toMessage(this.indent + 1, this);
            this.indent--;
         } else {
            this.lines.add(new Line(this.indent, listElementTitle.toString(), Message.objectToString(displayValue)));
         }
      }

      this.indent--;

      return this;
   }

   /**
    * Adds a new title line with the provided title when non-<code>null</code>.
    *
    * @param title the title for the line, maybe <code>null</code>.
    * @return this {@link Message}.
    */

   public Message title(CharSequence title) {
      this.cachedResult = null;
      if (Objects.nonNull(title)) {
         this.lines.add(new Line(this.indent, title));
      }
      return this;
   }

   /**
    * Uses the {@link ToMessage} interface of the provided <code>value</code> to generate a string representation of the
    * value for the {@link Message}.
    *
    * @param value the <code>value</code> to generate a message for.
    * @return this {@link Message}.
    */

   public Message toMessage(ToMessage value) {
      return value.toMessage(-1, this);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      //@formatter:off
      return
         Objects.nonNull( this.cachedResult )
            ? this.cachedResult
            : ( this.cachedResult = this.toStringBuilder(null).toString() );
      //@formatter:on
   }

   /**
    * Formats the lines of the message and appends them to the provided {@link StringBuilder}.
    *
    * @param message the {@link StringBuilder} to append the message to. This parameter maybe <code>null</code>. When
    * <code>null</code> a new {@link StringBuilder} is created.
    * @return the provided or a new {@link StringBuilder}.
    */

   public StringBuilder toStringBuilder(StringBuilder message) {

      var maxIndent = this.lines.stream().mapToInt(Line::getIndent).max().orElse(0);
      var indentColumnStartArray = new int[maxIndent + 1];

      //@formatter:off
      this.lines.stream().filter( ( line ) -> LineType.SEGMENT.equals( line.getLineType() ) )
         .forEach
            (
               ( line ) ->
               {
                  var indent = line.getIndent();
                  indent = (indent <= maxIndent) ? (indent >= 0) ? indent : 0 : maxIndent;
                  var titleLength = line.getSegmentTitleLength() + 2;
                  indentColumnStartArray[indent] = indentColumnStartArray[indent] > titleLength ? indentColumnStartArray[indent] : titleLength;
               }
            );
      //@formatter:on

      var estimatedSize = this.lines.stream().mapToInt((line) -> line.size(indentColumnStartArray)).sum();

      var outMessage = Objects.nonNull(message) ? message : new StringBuilder(estimatedSize);

      this.lines.forEach((line) -> line.append(outMessage, indentColumnStartArray));

      return outMessage;
   }
}

/* EOF */
