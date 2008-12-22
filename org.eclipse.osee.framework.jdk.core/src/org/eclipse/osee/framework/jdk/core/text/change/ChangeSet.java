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
package org.eclipse.osee.framework.jdk.core.text.change;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.regex.Matcher;

/**
 * @author Ryan D. Brooks
 */
public class ChangeSet {
   private CharacterChanger firstChange;
   private CharacterChanger lastChange;
   private char[] sourceChars;
   private CharSequence source;

   public ChangeSet() {
      this("");
   }

   public ChangeSet(CharSequence source) {
      if (source instanceof CharBuffer) {
         CharBuffer charBuf = (CharBuffer) source;
         if (charBuf.hasArray()) {
            sourceChars = charBuf.array();
            return;
         }
      }
      if (source instanceof String) {
         sourceChars = ((String) source).toCharArray();
      }
      this.source = source;
   }

   private int copyFromSource(int srcStrartIndex, int srcEndIndex, char[] dest, int destPos) {
      if (sourceChars != null) {
         int length = srcEndIndex - srcStrartIndex;
         System.arraycopy(sourceChars, srcStrartIndex, dest, destPos, length);
         return destPos + length;
      } else {
         for (int i = srcStrartIndex; i < srcEndIndex; i++) {
            dest[destPos++] = source.charAt(i);
         }
         return destPos;
      }
   }

   private void writeFromSource(int srcStrartIndex, int srcEndIndex, Writer writer) throws IOException {
      if (sourceChars != null) {
         writer.write(sourceChars, srcStrartIndex, srcEndIndex - srcStrartIndex);
      } else {
         for (int i = srcStrartIndex; i < srcEndIndex; i++) {
            writer.write(source.charAt(i));
         }
      }
   }

   public int getSourceLength() {
      if (sourceChars == null) {
         return source.length();
      }
      return sourceChars.length;
   }

   public void applyChanges(char[] dest, int destPos) {
      int lastEndIndex = 0;

      CharacterChanger change = firstChange;
      while (change != null) {
         destPos = copyFromSource(lastEndIndex, change.getStartIndex(), dest, destPos);
         lastEndIndex = change.getEndIndex();
         destPos = change.applyChange(dest, destPos);
         change = change.next();
      }
      copyFromSource(lastEndIndex, getSourceLength(), dest, destPos);
   }

   /**
    * Make sure this writer is buffered if source is only a CharSequence
    * 
    * @param writer
    * @throws IOException
    */
   public void applyChanges(Writer writer) throws IOException {
      int lastEndIndex = 0;

      CharacterChanger change = firstChange;
      while (change != null) {
         writeFromSource(lastEndIndex, change.getStartIndex(), writer);
         lastEndIndex = change.getEndIndex();
         change.applyChange(writer);
         change = change.next();
      }
      writeFromSource(lastEndIndex, getSourceLength(), writer);
   }

   public void applyChanges(File outFile) throws IOException {
      BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
      applyChanges(writer);
      writer.close();
   }

   public void insertBefore(int index, char[] newChars, int offset, int length, boolean copy) {
      addChanger(new CharArrayChange(index, index, newChars, offset, length, copy));
   }

   public void insertBefore(int index, char[] newChars, int offset, int length) {
      addChanger(new CharArrayChange(index, index, newChars, offset, length));
   }

   public void insertBefore(int index, char[] newChars) {
      addChanger(new CharArrayChange(index, index, newChars));
   }

   public void insertBefore(int index, char newChar) {
      addChanger(new CharChange(index, index, newChar));
   }

   public void insertBefore(int index, String newChar) {
      insertBefore(index, newChar.toCharArray());
   }

   public void replace(int srcStartIndex, int srcEndIndex, char[] newChars, int offset, int length) {
      addChanger(new CharArrayChange(srcStartIndex, srcEndIndex, newChars, offset, length));
   }

   public void replace(int srcStartIndex, int srcEndIndex, char[] newChars) {
      addChanger(new CharArrayChange(srcStartIndex, srcEndIndex, newChars));
   }

   public void replace(int srcStartIndex, int srcEndIndex, char newChar) {
      addChanger(new CharChange(srcStartIndex, srcEndIndex, newChar));
   }

   public void replace(int srcStartIndex, int srcEndIndex, String newChar) {
      replace(srcStartIndex, srcEndIndex, newChar.toCharArray());
   }

   public void delete(int srcStartIndex, int srcEndIndex) {
      addChanger(new DeleteChange(srcStartIndex, srcEndIndex));
   }

   /**
    * Replaces may not overlap. Deletes may overlap, but they must be combined into a single delete. Optionally you may
    * combine adjacent deletes. Adjacent inserts may be done at the same index if start is < last end and not a delete
    * since all changes are placed in order and normalized as they are added to the list of changes is always in order
    * and normalized
    */
   private void addChanger(CharacterChanger changer) {
      if (firstChange == null) { // if the change set is currently empty
         firstChange = changer; // no normalization needed since there is only one changer
         lastChange = firstChange;
      } else {
         if (changer.getStartIndex() < lastChange.getStartIndex()) { // if change belongs somewhere before the end

            CharacterChanger current = firstChange;
            CharacterChanger previous = null;
            // search for insertion point
            while (current.getStartIndex() <= changer.getStartIndex()) {
               previous = current;
               current = current.next(); // there should always be a next since we already checked that this changer does not belong on the end
            }
            addNormalized(previous, changer, current);
         } else { // changer belongs on the end so skip search for insertion point
            addNormalized(lastChange, changer, null);
         }
      }
   }

   private void addNormalized(CharacterChanger previous, CharacterChanger changer, CharacterChanger next) {
      // assumptions: if next is non-null, changer.srcEndIndex < next.srcStartIndex
      //            previous.srcEndIndex <= changer.srcEndIndex 
      //					the current set of changes have no overlaps (i.e. is already normalized)

      // if these changes are adjacent deletes, just combine them
      if (lastChange instanceof DeleteChange && next instanceof DeleteChange) {
         //lastChange.setDeletionRange
      }

      if (previous == null) { // if belongs at the head
         firstChange = changer;
      } else { // else insert in between
         overlapping(previous.getEndIndex(), changer.getStartIndex());
         previous.setNext(changer);
      }
      if (next == null) {
         lastChange = changer;
      } else {
         overlapping(changer.getEndIndex(), next.getStartIndex());
         changer.setNext(next);
      }
   }

   private void overlapping(int a, int b) {
      if (a > b) {
         throw new IllegalArgumentException(
               "Overlapping changes are not currently allowed " + a + " > " + b + " " + new String(sourceChars, b - 4,
                     20));
      }
   }

   public int getLengthDelta() {
      int lengthDelta = 0;

      CharacterChanger change = firstChange;
      while (change != null) {
         lengthDelta += change.getLengthDelta();
         change = change.next();
      }
      return lengthDelta;
   }

   public CharSequence applyChangesToSelf() {
      this.sourceChars = toCharArray();
      this.source = CharBuffer.wrap(sourceChars);
      this.firstChange = null;
      this.lastChange = null;
      return source;
   }

   public char[] toCharArray() {
      char[] chars = new char[getLengthDelta() + getSourceLength()];
      applyChanges(chars, 0);
      return chars;
   }

   public String toString() {
      return new String(toCharArray());
   }

   /**
    * This function calls reset(source) on your matcher and will replace all matched items in the ChangesSet source.
    * 
    * @param matcher
    * @param replacement
    */
   public void replaceAll(Matcher matcher, String replacement) {
      matcher.reset(source);
      while (matcher.find()) {
         replace(matcher.start(), matcher.end(), replacement);
      }
   }
}