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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * PaintListener that will turn any oseeimagelink:... into selectable hyperlink.
 *
 * @author Jaden W. Puckett
 */
public class XTextOseeImageLinkListener implements ModifyListener {

   private final XText xText;
   private final Set<OseeLinkWord> links = new HashSet<>();
   private Integer maxLength = 50000;
   public static Pattern oseeImageLinkPattern =
      Pattern.compile("<oseeimagelink>\\[(.*?)\\]-\\[(.*?)\\]</oseeimagelink>");
   private final BranchToken branchToken;

   public class OseeLinkWord {
      public String word;
      public int start;
      public long id;

      public OseeLinkWord(String word, int start, long id) {
         this.word = word;
         this.start = start;
         this.id = id;
      }
   }

   public XTextOseeImageLinkListener(final XText xText, BranchToken branchToken) {
      this.xText = xText;
      this.branchToken = branchToken;
      refreshStyleRanges();
      xText.getStyledText().addMouseListener(mouseListener);
      xText.getStyledText().addDisposeListener(new DisposeListener() {
         @Override
         public void widgetDisposed(DisposeEvent e) {
            if (xText.getStyledText() == null || xText.getStyledText().isDisposed()) {
               return;
            }
            xText.getStyledText().removeMouseListener(mouseListener);
         }
      });
   }

   private void getLinks(String str) {
      links.clear();
      Matcher m = oseeImageLinkPattern.matcher(str);
      while (m.find()) {
         String string = m.group();
         long id = Long.valueOf(m.group(1));
         OseeLinkWord sw = new OseeLinkWord(string, m.start(), id);
         links.add(sw);
      }
   }

   private void refreshStyleRanges() {
      String text = xText.getStyledText().getText();
      getLinks(text);
      for (OseeLinkWord link : links) {
         StyleRange styleRange = new StyleRange();
         styleRange.underlineStyle = SWT.UNDERLINE_LINK;
         styleRange.underline = true;
         styleRange.data = link.word;
         styleRange.start = link.start;
         styleRange.length = link.word.length();
         styleRange.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
         xText.getStyledText().setStyleRange(styleRange);
      }
   }

   private final MouseListener mouseListener = new MouseListener() {
      @SuppressWarnings("deprecation")
      @Override
      public void mouseUp(org.eclipse.swt.events.MouseEvent e) {

         StyledText styledText = xText.getStyledText();
         int offset = 0;
         try {
            offset = styledText.getOffsetAtLocation(new Point(e.x, e.y));
         } catch (IllegalArgumentException ex) {
            /*
             * Illegal argument exception happens when selected point is outside the range of the rectangle. Since it
             * does it's own calculation, just throw this exception away.
             */
            return;
         }
         for (OseeLinkWord sw : links) {
            if (sw.start < offset && sw.start + sw.word.length() > offset) {
               handleSelected(sw);
               break;
            }
         }
      };

      @Override
      public void mouseDoubleClick(MouseEvent e) {
         // do nothing
      }

      @Override
      public void mouseDown(MouseEvent e) {
         // do nothing
      }
   };

   private void handleSelected(final OseeLinkWord sw) {
      Job job = new Job(String.format("Opening editor for link [%s].", sw.word)) {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            long id = sw.id;
            Artifact artifact = ArtifactQuery.getArtifactFromId(ArtifactId.valueOf(id), branchToken);
            ArtifactEditor.editArtifact(artifact);
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   @Override
   public void modifyText(ModifyEvent e) {
      if (xText == null || xText.getStyledText() == null || xText.getStyledText().isDisposed()) {
         return;
      }
      if (xText != null) {
         refreshStyleRanges();
      }
   }

   public Integer getMaxLength() {
      return maxLength;
   }

   public void setMaxLength(Integer maxLength) {
      this.maxLength = maxLength;
   }
}