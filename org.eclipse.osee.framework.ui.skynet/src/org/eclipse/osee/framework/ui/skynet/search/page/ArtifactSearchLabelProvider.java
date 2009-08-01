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
package org.eclipse.osee.framework.ui.skynet.search.page;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactDecorator;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchResult;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactSearchLabelProvider extends LabelProvider implements IStyledLabelProvider {
   private static final String HIGHLIGHT_BG_COLOR_NAME = "org.eclipse.jdt.ui.ColoredLabels.match_highlight"; //$NON-NLS-1$

   private static final Styler HIGHLIGHT_STYLE = StyledString.createColorRegistryStyler(null, HIGHLIGHT_BG_COLOR_NAME);
   private static final Styler DELETED_ARTIFACT_STYLE = StyledString.createColorRegistryStyler("red", null);

   private static final String fgEllipses = " ... "; //$NON-NLS-1$

   private final AbstractArtifactSearchViewPage fPage;
   private final Comparator fMatchComparator;
   private final ArtifactDecorator artifactDecorator;

   private final Map<Image, Image> disabledImageMap;

   public ArtifactSearchLabelProvider(AbstractArtifactSearchViewPage page, ArtifactDecorator artifactDecorator) {
      this.artifactDecorator = artifactDecorator;
      this.disabledImageMap = new HashMap<Image, Image>();
      fPage = page;
      fMatchComparator = new Comparator() {
         public int compare(Object o1, Object o2) {
            return ((AttributeMatch) o1).getOriginalOffset() - ((AttributeMatch) o2).getOriginalOffset();
         }
      };
   }

   @Override
   public String getText(Object object) {
      return getStyledText(object).getString();
   }

   public StyledString getStyledText(Object element) {
      if (element instanceof AttributeLineElement) {
         return getLineElementLabel((AttributeLineElement) element);
      }

      if (!(element instanceof Artifact)) return new StyledString(String.format("Undefined: %s %s", element.getClass(),
            element));

      Artifact artifact = (Artifact) element;
      String name = artifact.getName();
      int matchCount = getMatchCount(artifact);
      if (matchCount > 0) {
         StyledString artifactString = getColoredLabelWithCounts(artifact, matchCount, new StyledString(name));
         return getArtifactText(artifact, artifactString);
      } else {
         return new StyledString(name, StyledString.DECORATIONS_STYLER);
      }
   }

   private int getMatchCount(Object element) {
      AbstractArtifactSearchResult result = fPage.getInput();
      if (result == null) {
         return -1;
      }
      return result.getMatchCount(element);
   }

   private StyledString getArtifactText(Artifact artifact, StyledString coloredName) {
      if (artifact.isDeleted()) {
         coloredName.append(' ').append("<Deleted>", DELETED_ARTIFACT_STYLE);
      }
      if (artifactDecorator != null) {
         if (artifactDecorator.showArtVersion()) {
            coloredName.append(' ').append("-" + artifact.getGammaId() + "-", StyledString.DECORATIONS_STYLER);
         }

         if (artifactDecorator.showArtIds()) {
            coloredName.append(' ').append("(" + artifact.getArtId() + ")", StyledString.DECORATIONS_STYLER);
         }
         try {
            if (artifactDecorator.showArtType()) {
               coloredName.append(' ').append("<" + artifact.getArtifactTypeName() + ">",
                     StyledString.DECORATIONS_STYLER);
            }

            String selectedAttributes = artifactDecorator.getSelectedAttributeData(artifact);
            if (Strings.isValid(selectedAttributes)) {
               coloredName.append(' ').append(selectedAttributes, StyledString.DECORATIONS_STYLER);
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
      return coloredName;
   }

   @SuppressWarnings("unchecked")
   private StyledString getLineElementLabel(AttributeLineElement lineElement) {
      String lineNumberString = String.format("%s, %s ", lineElement.getLine(), lineElement.getOffset());
      StyledString str = new StyledString(lineNumberString, StyledString.QUALIFIER_STYLER);

      Match[] matches = lineElement.getMatches(fPage.getInput());
      Arrays.sort(matches, fMatchComparator);

      String content = lineElement.getContents();

      int pos = evaluateLineStart(matches, content, lineElement.getOffset());

      int length = content.length();

      int charsToCut = getCharsToCut(length, matches); // number of characters to leave away if the line is too long
      for (int i = 0; i < matches.length; i++) {
         AttributeMatch match = (AttributeMatch) matches[i];
         int start = Math.max(match.getOriginalOffset() - lineElement.getOffset(), 0);
         // append gap between last match and the new one
         if (pos < start) {
            if (charsToCut > 0) {
               charsToCut = appendShortenedGap(content, pos, start, charsToCut, i == 0, str);
            } else {
               str.append(content.substring(pos, start));
            }
         }
         // append match
         int end =
               Math.min(match.getOriginalOffset() + match.getOriginalLength() - lineElement.getOffset(),
                     lineElement.getLength());
         str.append(content.substring(start, end), HIGHLIGHT_STYLE);
         pos = end;
      }
      // append rest of the line
      if (charsToCut > 0) {
         appendShortenedGap(content, pos, length, charsToCut, false, str);
      } else {
         str.append(content.substring(pos));
      }
      return str;
   }

   private static final int MIN_MATCH_CONTEXT = 10; // minimal number of characters shown after and before a match

   private int appendShortenedGap(String content, int start, int end, int charsToCut, boolean isFirst, StyledString str) {
      int gapLength = end - start;
      if (!isFirst) {
         gapLength -= MIN_MATCH_CONTEXT;
      }
      if (end < content.length()) {
         gapLength -= MIN_MATCH_CONTEXT;
      }
      if (gapLength < MIN_MATCH_CONTEXT) { // don't cut, gap is too small
         str.append(content.substring(start, end));
         return charsToCut;
      }

      int context = MIN_MATCH_CONTEXT;
      if (gapLength > charsToCut) {
         context += gapLength - charsToCut;
      }

      if (!isFirst) {
         str.append(content.substring(start, start + context)); // give all extra context to the right side of a match
         context = MIN_MATCH_CONTEXT;
      }

      str.append(fgEllipses, StyledString.QUALIFIER_STYLER);

      if (end < content.length()) {
         str.append(content.substring(end - context, end));
      }
      return charsToCut - gapLength + fgEllipses.length();
   }

   private int getCharsToCut(int contentLength, Match[] matches) {
      if (contentLength <= 256 || !"win32".equals(SWT.getPlatform()) || matches.length == 0) { //$NON-NLS-1$
         return 0; // no shortening required
      }
      // XXX: workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=38519
      return contentLength - 256 + Math.max(matches.length * fgEllipses.length(), 100);
   }

   private int evaluateLineStart(Match[] matches, String lineContent, int lineOffset) {
      int max = lineContent.length();
      if (matches.length > 0) {
         AttributeMatch match = (AttributeMatch) matches[0];
         max = match.getOriginalOffset() - lineOffset;
         if (max < 0) {
            return 0;
         }
      }
      for (int i = 0; i < max; i++) {
         char ch = lineContent.charAt(i);
         if (!Character.isWhitespace(ch) || ch == '\n' || ch == '\r') {
            return i;
         }
      }
      return max;
   }

   private StyledString getColoredLabelWithCounts(Object element, int matchCount, StyledString coloredName) {
      String countInfo = String.format("(%s match%s)", matchCount, matchCount > 1 ? "es" : "");
      coloredName.append(' ').append(countInfo, StyledString.COUNTER_STYLER);
      return coloredName;
   }

   @Override
   public Image getImage(Object element) {
      Image toReturn = null;
      if (element instanceof AttributeLineElement) {
         toReturn = ImageManager.getImage(FrameworkImage.LINE_MATCH);
      } else if (element instanceof Artifact) {
         Image artImage = ImageManager.getImage((Artifact) element);
         int matchCount = getMatchCount(element);
         if (matchCount > 0) {
            toReturn = artImage;
         } else {
            Image disabledImage = disabledImageMap.get(artImage);
            if (disabledImage == null) {
               disabledImage = new Image(artImage.getDevice(), artImage, SWT.IMAGE_DISABLE);
               disabledImageMap.put(artImage, disabledImage);
            }
            toReturn = disabledImage;
         }
      }
      return toReturn;
   }

   private static StyledString decorateColoredString(StyledString string, String decorated, Styler color) {
      String label = string.getString();
      int originalStart = decorated.indexOf(label);
      if (originalStart == -1) {
         return new StyledString(decorated); // the decorator did something wild
      }
      if (originalStart > 0) {
         StyledString newString = new StyledString(decorated.substring(0, originalStart), color);
         newString.append(string);
         string = newString;
      }
      if (decorated.length() > originalStart + label.length()) { // decorator appended something
         return string.append(decorated.substring(originalStart + label.length()), color);
      }
      return string; // no change
   }

}
