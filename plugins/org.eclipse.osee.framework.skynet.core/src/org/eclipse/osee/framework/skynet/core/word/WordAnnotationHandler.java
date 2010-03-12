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
package org.eclipse.osee.framework.skynet.core.word;

/**
 * @author Theron Virgin
 */
public class WordAnnotationHandler {

   public static boolean containsWordAnnotations(String wordml) {
      return (wordml.contains("<w:delText>") || wordml.contains("w:type=\"Word.Insertion\"") || wordml.contains("w:type=\"Word.Formatting\""));
   }

   public static String removeAnnotations(String wordml) {
      String annotation = "<.??aml:annotation.*?>";
      String content = "<.??aml:content.*?>";
      String deletions = "<w:delText>.*?</w:delText>";
      wordml = wordml.replaceAll(annotation, "");
      wordml = wordml.replaceAll(content, "");
      wordml = wordml.replaceAll(deletions, "");
      return wordml;
   }
}
