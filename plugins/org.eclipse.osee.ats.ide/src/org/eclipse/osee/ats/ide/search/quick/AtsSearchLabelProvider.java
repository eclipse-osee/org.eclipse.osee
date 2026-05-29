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

package org.eclipse.osee.ats.ide.search.quick;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.skynet.ArtifactDecorator;
import org.eclipse.osee.framework.ui.skynet.search.page.AbstractArtifactSearchViewPage;
import org.eclipse.osee.framework.ui.skynet.search.page.ArtifactSearchLabelProvider;
import org.eclipse.osee.framework.ui.skynet.search.page.AttributeLineElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

/**
 * Label provider for ATS search results that displays: &lt;ATS Id&gt; - &lt;Team Def&gt; - &lt;state&gt; -
 * &lt;title&gt;
 *
 * @author Donald G. Dunne
 */
public class AtsSearchLabelProvider extends ArtifactSearchLabelProvider {

   private final AbstractArtifactSearchViewPage page;

   private static final Styler BOLD_STYLER = new Styler() {
      @Override
      public void applyStyles(TextStyle textStyle) {
         textStyle.font = getBoldFont();
      }
   };

   private static final Styler STATE_STYLER = new Styler() {
      @Override
      public void applyStyles(TextStyle textStyle) {
         textStyle.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
      }
   };

   private static final Styler ATTR_TYPE_STYLER = new Styler() {
      @Override
      public void applyStyles(TextStyle textStyle) {
         textStyle.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW);
      }
   };

   private static Font boldFont;

   private static Font getBoldFont() {
      if (boldFont == null || boldFont.isDisposed()) {
         Display display = Display.getDefault();
         FontData[] fontData = display.getSystemFont().getFontData();
         for (FontData fd : fontData) {
            fd.setStyle(fd.getStyle() | SWT.BOLD);
         }
         boldFont = new Font(display, fontData);
      }
      return boldFont;
   }

   public AtsSearchLabelProvider(AbstractArtifactSearchViewPage page, ArtifactDecorator artifactDecorator) {
      super(page, artifactDecorator);
      this.page = page;
   }

   @Override
   public StyledString getStyledText(Object element) {
      if (element instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) element;
         StyledString styledLabel = getAtsStyledLabel(awa);
         int matchCount = getAtsMatchCount(awa);
         if (matchCount > 0) {
            String countInfo = String.format(" (%s match%s)", matchCount, matchCount > 1 ? "es" : "");
            styledLabel.append(countInfo, StyledString.COUNTER_STYLER);
         }
         return styledLabel;
      }
      if (element instanceof AttributeLineElement) {
         String attrTypeName = getAttributeTypeName((AttributeLineElement) element);
         StyledString result = new StyledString(attrTypeName + ": ", ATTR_TYPE_STYLER);
         // Let parent render the match-highlighted content
         StyledString parentLabel = super.getStyledText(element);
         result.append(parentLabel);
         return result;
      }
      return super.getStyledText(element);
   }

   private String getAttributeTypeName(AttributeLineElement lineElement) {
      try {
         Artifact parent = lineElement.getParent();
         if (parent != null && parent.hasAttribute(lineElement.getAttribute())) {
            Attribute<?> attr = parent.getAttributeById(lineElement.getAttribute(), false);
            if (attr != null) {
               return attr.getAttributeType().getName();
            }
         }
      } catch (Exception ex) {
         // fall through
      }
      return "Attribute";
   }

   private StyledString getAtsStyledLabel(AbstractWorkflowArtifact awa) {
      StyledString str = new StyledString();
      str.append(awa.getAtsId(), BOLD_STYLER);
      str.append(" - ");
      str.append(getTeamDefName(awa));
      str.append(" - ");
      str.append(awa.getCurrentStateName(), STATE_STYLER);
      str.append(" - ");
      str.append(awa.getName());
      return str;
   }

   private String getTeamDefName(AbstractWorkflowArtifact awa) {
      try {
         if (awa instanceof TeamWorkFlowArtifact) {
            return ((TeamWorkFlowArtifact) awa).getTeamName();
         }
         if (awa.getParentTeamWorkflow() != null) {
            return AtsApiService.get().getTeamDefinitionService().getTeamDefinition(
               awa.getParentTeamWorkflow()).getName();
         }
      } catch (Exception ex) {
         // fall through
      }
      return "";
   }

   private int getAtsMatchCount(Object element) {
      try {
         if (page != null && page.getInput() != null) {
            return page.getInput().getMatchCount(element);
         }
      } catch (Exception ex) {
         // fall through
      }
      return 0;
   }
}
