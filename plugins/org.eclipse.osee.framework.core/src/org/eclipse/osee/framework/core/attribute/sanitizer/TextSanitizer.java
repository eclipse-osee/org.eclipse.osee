/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.framework.core.attribute.sanitizer;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Jaden W. Puckett
 */
public class TextSanitizer {

   // Unicode "space separator" chars except normal ASCII space.
   // Tabs/newlines are NOT in \p{Zs}, so they are preserved.
   private static final Pattern ILLEGAL_SPACE_CHARS = Pattern.compile("[\\p{Zs}&&[^\\u0020]]");

   // Combining marks (diacritics) after decomposition
   private static final Pattern COMBINING_MARKS = Pattern.compile("\\p{M}+");

   /**
    * Non-ASCII characters that are allowed to remain in the value (not removed).
    */
   private static final Set<Integer> ALLOWED_NON_ASCII = Set.of(0x00BD, // ½
      0x00B0, // °
      0x00B1 // ±
   );

   // Common punctuation / symbol mappings to ASCII
   private static final Map<Integer, String> ASCII_REPLACEMENTS = new HashMap<>();
   static {
      // Quotes/apostrophes
      ASCII_REPLACEMENTS.put(0x2018, "'"); // ‘
      ASCII_REPLACEMENTS.put(0x2019, "'"); // ’
      ASCII_REPLACEMENTS.put(0x201B, "'"); // ‛
      ASCII_REPLACEMENTS.put(0x201C, "\""); // “
      ASCII_REPLACEMENTS.put(0x201D, "\""); // ”
      ASCII_REPLACEMENTS.put(0x201E, "\""); // „

      // Dashes/hyphens
      ASCII_REPLACEMENTS.put(0x2010, "-"); // ‐
      ASCII_REPLACEMENTS.put(0x2011, "-"); // ‑
      ASCII_REPLACEMENTS.put(0x2012, "-"); // ‒
      ASCII_REPLACEMENTS.put(0x2013, "-"); // –
      ASCII_REPLACEMENTS.put(0x2014, "-"); // —
      ASCII_REPLACEMENTS.put(0x2212, "-"); // − minus sign

      // Bullet-like
      ASCII_REPLACEMENTS.put(0x00B7, "-"); // · (middle dot)
      ASCII_REPLACEMENTS.put(0x2022, "-"); // • bullet

      // Ellipsis
      ASCII_REPLACEMENTS.put(0x2026, "..."); // …

      // “Special spaces” / invisibles
      ASCII_REPLACEMENTS.put(0x200B, ""); // zero-width space
      ASCII_REPLACEMENTS.put(0xFEFF, ""); // BOM/ZWNBSP
      ASCII_REPLACEMENTS.put(0x00AD, ""); // soft hyphen

      // Common symbols
      ASCII_REPLACEMENTS.put(0x00A9, "(c)"); // ©
      ASCII_REPLACEMENTS.put(0x00AE, "(r)"); // ®
      ASCII_REPLACEMENTS.put(0x2122, "TM"); // ™
   }

   // Protect allowlisted characters from NFKD decomposition using BMP Private Use Area placeholders.
   private static final int PUA_START = 0xE000;
   private static final int[] ALLOWED_SORTED =
      ALLOWED_NON_ASCII.stream().mapToInt(Integer::intValue).sorted().toArray();

   private static final Map<Integer, Integer> ALLOWED_TO_PUA = new HashMap<>();
   private static final Map<Integer, Integer> PUA_TO_ALLOWED = new HashMap<>();
   static {
      for (int i = 0; i < ALLOWED_SORTED.length; i++) {
         int allowed = ALLOWED_SORTED[i];
         int pua = PUA_START + i;
         ALLOWED_TO_PUA.put(allowed, pua);
         PUA_TO_ALLOWED.put(pua, allowed);
      }
   }

   private static boolean isAllowedNonAscii(int cp) {
      return ALLOWED_NON_ASCII.contains(cp);
   }

   /**
    * Quick pre-check used to avoid running the sanitizer when no disallowed non-ASCII is present.
    *
    * @param text input text (may be {@code null})
    * @return {@code true} if {@code text} contains any non-ASCII character that is not in {@link #ALLOWED_NON_ASCII}
    */
   public static boolean shouldSanitizeToAscii(String text) {
      if (text == null || text.isEmpty()) {
         return false;
      }
      return text.codePoints().anyMatch(cp -> cp > 0x7F && !isAllowedNonAscii(cp));
   }

   /**
    * Sanitizes text to an ASCII-oriented form.
    *
    * @param text input text (may be {@code null})
    * @param nonAsciiFallback replacement for unsupported non-ASCII characters; {@code null} uses {@code "?"}
    * @return sanitized text, or {@code null} if {@code text} is {@code null}
    */
   public static String sanitizeToAscii(String text, String nonAsciiFallback) {
      if (text == null) {
         return null;
      }
      if (Strings.isInvalid(text)) {
         return text;
      }

      final String fallback = (nonAsciiFallback == null) ? "?" : nonAsciiFallback;

      // 1) Normalize illegal Unicode spaces to ASCII space
      text = ILLEGAL_SPACE_CHARS.matcher(text).replaceAll(" ");

      // 2) Apply known replacements and protect allowlisted symbols from NFKD decomposition
      StringBuilder sb = new StringBuilder(text.length());
      text.codePoints().forEach(cp -> {
         Integer pua = ALLOWED_TO_PUA.get(cp);
         if (pua != null) {
            sb.appendCodePoint(pua);
            return;
         }

         String repl = ASCII_REPLACEMENTS.get(cp);
         if (repl != null) {
            sb.append(repl);
         } else {
            sb.appendCodePoint(cp);
         }
      });
      text = sb.toString();

      // 3) Decompose and strip diacritics
      text = Normalizer.normalize(text, Normalizer.Form.NFKD);
      text = COMBINING_MARKS.matcher(text).replaceAll("");

      // 4) Restore allowlisted symbols and replace remaining non-ASCII with fallback
      StringBuilder out = new StringBuilder(text.length());
      text.codePoints().forEach(cp -> {
         Integer restored = PUA_TO_ALLOWED.get(cp);
         if (restored != null) {
            out.appendCodePoint(restored);
         } else if (cp <= 0x7F || isAllowedNonAscii(cp)) {
            out.appendCodePoint(cp);
         } else {
            out.append(fallback);
         }
      });

      return out.toString();
   }

   public static String removeEdgeSpacesUnicode(String input) {
      return input == null ? null : input.strip();
   }
}