/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.xx.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.util.MessageSummaryNote;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Roberto E. Escobar
 */
public final class XWidgetUtility {

   private static final boolean debugLayout = false; // turns on background colors for composites
   private static Integer formLineLimit;
   private static final String ATTR_FORM_PART_LINE_LIMIT = "attr.form.part.line.limit";

   public static void computeXTextSize(XXTextWidget xText) {
      if (Widgets.isAccessible(xText.getStyledText())) {
         int lineCount = Math.min(xText.getStyledText().getLineCount(), getLineLimit());
         int height = lineCount * xText.getStyledText().getLineHeight();
         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
         gd.heightHint = xText.isFillVertically() && height < 60 ? 60 : height;
         gd.widthHint = 200;
         xText.getStyledText().setLayoutData(gd);
      }
   }

   private static int getLineLimit() {
      if (formLineLimit == null) {
         formLineLimit = 2000;
         if (ClientSessionManager.isSessionValid()) {
            try {
               String value = OseeInfo.getCachedValue(ATTR_FORM_PART_LINE_LIMIT);
               if (Strings.isNumeric(value)) {
                  formLineLimit = Integer.valueOf(value);
               }
            } catch (OseeCoreException ex) {
               // use default
            }
         }
      }
      return formLineLimit;
   }

   public static void debugLayout(Composite parent, int color) {
      if (debugLayout) {
         parent.setBackground(Displays.getSystemColor(color));
      }
   }

   public static boolean isWidgetAccesible(Widget widget) {
      return widget != null && !widget.isDisposed();
   }

   /**
    * Adds a mouse wheel listener to a StyledText that propagates scroll events to the parent ScrolledForm or
    * ScrolledComposite when the text widget reaches its scroll boundary (top or bottom).
    * <p>
    * This improves UX in forms with multiple text widgets - users can scroll the entire form by continuing to scroll
    * when a text box reaches its limit.
    *
    * @param styledText the StyledText widget to add scroll propagation to
    */
   public static void addScrollPropagation(StyledText styledText) {
      if (styledText == null || styledText.isDisposed()) {
         return;
      }
      styledText.addMouseWheelListener(new MouseWheelListener() {
         @Override
         public void mouseScrolled(MouseEvent e) {
            // Only propagate if the StyledText has vertical scroll capability
            int topIndex = styledText.getTopIndex();
            int lineCount = styledText.getLineCount();
            int visibleLines = styledText.getClientArea().height / styledText.getLineHeight();
            boolean atTop = topIndex == 0;
            boolean atBottom = topIndex + visibleLines >= lineCount;
            // Scrolling up (positive count) and at top, or scrolling down (negative count) and at bottom
            boolean shouldPropagate = (e.count > 0 && atTop) || (e.count < 0 && atBottom);
            if (shouldPropagate) {
               ScrolledForm scrolledForm = findParentScrolledForm(styledText);
               if (scrolledForm != null) {
                  Point origin = scrolledForm.getOrigin();
                  int scrollAmount = e.count * styledText.getLineHeight();
                  scrolledForm.setOrigin(origin.x, origin.y - scrollAmount);
               } else {
                  ScrolledComposite scrolledComp = findParentScrolledComposite(styledText);
                  if (scrolledComp != null) {
                     Point origin = scrolledComp.getOrigin();
                     int scrollAmount = e.count * styledText.getLineHeight();
                     scrolledComp.setOrigin(origin.x, origin.y - scrollAmount);
                  }
               }
            }
         }
      });
   }

   /**
    * Finds the nearest parent ScrolledForm in the widget hierarchy.
    *
    * @param control the control to start searching from
    * @return the parent ScrolledForm, or null if not found
    */
   public static ScrolledForm findParentScrolledForm(Control control) {
      Composite parent = control.getParent();
      while (parent != null) {
         if (parent instanceof ScrolledForm) {
            return (ScrolledForm) parent;
         }
         parent = parent.getParent();
      }
      return null;
   }

   /**
    * Finds the nearest parent ScrolledComposite in the widget hierarchy.
    *
    * @param control the control to start searching from
    * @return the parent ScrolledComposite, or null if not found
    */
   public static ScrolledComposite findParentScrolledComposite(Control control) {
      Composite parent = control.getParent();
      while (parent != null) {
         if (parent instanceof ScrolledComposite) {
            return (ScrolledComposite) parent;
         }
         parent = parent.getParent();
      }
      return null;
   }

   public static List<XWidget> findXWidgetsInControl(Widget parent) {
      List<XWidget> xWidgets = new ArrayList<>();
      XWidget xWidget = asXWidget(parent);
      if (xWidget != null) {
         xWidgets.add(xWidget);
      }
      if (parent instanceof Composite) {
         Composite container = (Composite) parent;
         for (Control child : container.getChildren()) {
            xWidgets.addAll(findXWidgetsInControl(child));
         }
      }
      return xWidgets;
   }

   public static XWidget asXWidget(Widget widget) {
      XWidget toReturn = null;
      if (widget != null) {
         Object object = widget.getData(XWidget.XWIDGET_DATA_KEY);
         toReturn = object instanceof XWidget ? (XWidget) object : null;
      }
      return toReturn;
   }

   public static boolean hasXWidget(Widget widget) {
      boolean result = false;
      if (widget != null) {
         Object object = widget.getData(XWidget.XWIDGET_DATA_KEY);
         result = object instanceof XWidget;
      }
      return result;
   }

   /**
    * Adds red hyperlink at top and lists errors upon selection
    */
   public static void addMessageDecoration(IManagedForm managedForm, ScrolledForm form) {
      form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter() {

         @Override
         public void linkActivated(HyperlinkEvent e) {
            String title = e.getLabel();
            Object href = e.getHref();
            if (href instanceof IMessage[]) {
               Point noteLocation = ((Control) e.widget).toDisplay(0, 0);
               noteLocation.x += 10;
               noteLocation.y += 10;

               MessageSummaryNote note = new MessageSummaryNote(managedForm, title, (IMessage[]) href);
               note.setLocation(noteLocation);
               note.open();
            }
         }

      });
   }

   public static void setStatus(XResultData status, XWidget xWidget) {
      if (!status.isOK()) {
         int severity = IStatus.OK;
         if (status.isErrors()) {
            severity = IStatus.ERROR;
         } else if (status.isWarnings()) {
            severity = IStatus.WARNING;
         }
         xWidget.setControlCausedMessageByObject(status.toString(), toMessageProviderLevel(severity));
      } else {
         xWidget.removeControlCausedMessageByObject();
      }
   }

   public static boolean isValueInRange(int value, int min, int max) {
      return min <= value && value < max;
   }

   public static int toMessageProviderLevel(int level) {
      int toReturn = IMessageProvider.NONE;
      if (level == IStatus.INFO) {
         toReturn = IMessageProvider.INFORMATION;
      } else if (level == IStatus.WARNING) {
         toReturn = IMessageProvider.WARNING;
      } else if (level == IStatus.ERROR) {
         toReturn = IMessageProvider.ERROR;
      }
      return toReturn;
   }

   /**
    * Writes an HTML file comparing three XWidgetData snapshots (original builder, widget default, merged result) and
    * opens it in the system browser.
    */
   public static StringBuilder debugWidDataToHtml(String widgetLabel, String origLabel, XWidgetData original,
      String incomingLabel, XWidgetData incoming, XWidgetData merged) {
      StringBuilder sb = new StringBuilder();
      try {
         sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>XWidgetData Compare: ");
         sb.append(escHtml(widgetLabel));
         sb.append("</title><style>");
         sb.append("body{font-family:sans-serif;margin:20px}");
         sb.append("h2{color:#333}");
         sb.append("table{border-collapse:collapse;width:100%;margin-bottom:30px}");
         sb.append("th,td{border:1px solid #ccc;padding:6px 10px;text-align:left;vertical-align:top}");
         sb.append("th{background:#f0f0f0}");
         sb.append("tr:nth-child(even){background:#fafafa}");
         sb.append(".diff{background:#fff3cd}");
         sb.append("</style></head><body>");
         sb.append("<h2>XWidgetData Compare: ").append(escHtml(widgetLabel)).append("</h2>");
         sb.append(
            "<table width=\"98\"><tr><th>Field</th><th>" + origLabel + "</th><th>" + incomingLabel + "</th><th>Merged Result</th></tr>");

         addRow(sb, "label", str(original.getLabel()), str(incoming.getLabel()), str(merged.getLabel()));
         addRow(sb, "widgetId", str(original.getWidgetId()), str(incoming.getWidgetId()), str(merged.getWidgetId()));
         addRow(sb, "artifactType", str(original.getArtifactType()), str(incoming.getArtifactType()),
            str(merged.getArtifactType()));
         addRow(sb, "attributeType", str(original.getAttributeType()), str(incoming.getAttributeType()),
            str(merged.getAttributeType()));
         addRow(sb, "attributeType2", str(original.getAttributeType2()), str(incoming.getAttributeType2()),
            str(merged.getAttributeType2()));
         addRow(sb, "toolTip", str(original.getToolTip()), str(incoming.getToolTip()), str(merged.getToolTip()));
         addRow(sb, "defaultValue", str(original.getDefaultValue()), str(incoming.getDefaultValue()),
            str(merged.getDefaultValue()));
         addRow(sb, "xOptions", str(original.getXOptionHandler().getXOptions()),
            str(incoming.getXOptionHandler().getXOptions()), str(merged.getXOptionHandler().getXOptions()));
         addRow(sb, "oseeImage", str(original.getOseeImage()), str(incoming.getOseeImage()),
            str(merged.getOseeImage()));
         addRow(sb, "height", str(original.getHeight()), str(incoming.getHeight()), str(merged.getHeight()));
         addRow(sb, "beginComposite", str(original.getBeginComposite()), str(incoming.getBeginComposite()),
            str(merged.getBeginComposite()));
         addRow(sb, "beginGroupComposite", str(original.getBeginGroupComposite()),
            str(incoming.getBeginGroupComposite()), str(merged.getBeginGroupComposite()));
         addRow(sb, "endComposite", str(original.isEndComposite()), str(incoming.isEndComposite()),
            str(merged.isEndComposite()));
         addRow(sb, "endGroupComposite", str(original.isEndGroupComposite()), str(incoming.isEndGroupComposite()),
            str(merged.isEndGroupComposite()));
         addRow(sb, "fillVertically", str(original.isFillVertically()), str(incoming.isFillVertically()),
            str(merged.isFillVertically()));
         addRow(sb, "fillHorizontally", str(original.isFillHorizontally()), str(incoming.isFillHorizontally()),
            str(merged.isFillHorizontally()));
         addRow(sb, "required", str(original.isRequired()), str(incoming.isRequired()), str(merged.isRequired()));
         addRow(sb, "requiredForCompletion", str(original.isRequiredForCompletion()),
            str(incoming.isRequiredForCompletion()), str(merged.isRequiredForCompletion()));
         addRow(sb, "horizontalLabel", str(original.isHorizontalLabel()), str(incoming.isHorizontalLabel()),
            str(merged.isHorizontalLabel()));
         addRow(sb, "border", str(original.isBorder()), str(incoming.isBorder()), str(merged.isBorder()));
         addRow(sb, "keyedBranchName", str(original.getKeyedBranchName()), str(incoming.getKeyedBranchName()),
            str(merged.getKeyedBranchName()));
         addRow(sb, "relationTypeSide", str(original.getRelationTypeSide()), str(incoming.getRelationTypeSide()),
            str(merged.getRelationTypeSide()));
         addRow(sb, "artifact", str(original.getArtifact()), str(incoming.getArtifact()), str(merged.getArtifact()));
         addRow(sb, "teamId", str(original.getTeamId()), str(incoming.getTeamId()), str(merged.getTeamId()));
         addRow(sb, "groupName", str(original.getGroupName()), str(incoming.getGroupName()),
            str(merged.getGroupName()));
         addRow(sb, "doubleClickText", str(original.getDoubleClickText()), str(incoming.getDoubleClickText()),
            str(merged.getDoubleClickText()));
         addRow(sb, "userGroup", str(original.getUserGroup()), str(incoming.getUserGroup()),
            str(merged.getUserGroup()));
         addRow(sb, "min", str(original.getMin()), str(incoming.getMin()), str(merged.getMin()));
         addRow(sb, "max", str(original.getMax()), str(incoming.getMax()), str(merged.getMax()));
         addRow(sb, "selectable", str(original.getSelectable()), str(incoming.getSelectable()),
            str(merged.getSelectable()));
         addRow(sb, "values", str(original.getValues()), str(incoming.getValues()), str(merged.getValues()));
         addRow(sb, "conditions", str(original.getConditions()), str(incoming.getConditions()),
            str(merged.getConditions()));
         addRow(sb, "fgLabelColor", str(original.getFgLabelColor()), str(incoming.getFgLabelColor()),
            str(merged.getFgLabelColor()));
         addRow(sb, "dateFormat", str(original.getDateFormat()), str(incoming.getDateFormat()),
            str(merged.getDateFormat()));
         addRow(sb, "diagDescUrl", str(original.getDiagDescUrl()), str(incoming.getDiagDescUrl()),
            str(merged.getDiagDescUrl()));
         addRow(sb, "id", str(original.getId()), str(incoming.getId()), str(merged.getId()));
         addRow(sb, "enumeratedArt", str(original.getEnumeratedArt()), str(incoming.getEnumeratedArt()),
            str(merged.getEnumeratedArt()));
         //         addRow(sb, "branchQuery", String.valueOf(original.getBranchQuery()), String.valueOf(incoming.getBranchQuery()),
         //            String.valueOf(merged.getBranchQuery()));
         addRow(sb, "computedCharacteristic", str(original.getComputedCharacteristic()),
            str(incoming.getComputedCharacteristic()), str(merged.getComputedCharacteristic()));
         addRow(sb, "valueProvider", String.valueOf(original.getValueProvider()),
            String.valueOf(incoming.getValueProvider()), String.valueOf(merged.getValueProvider()));
         addRow(sb, "parameters", str(original.getParameters()), str(incoming.getParameters()),
            str(merged.getParameters()));
         addRow(sb, "providerFor", str(original.getProviderFor()), str(incoming.getProviderFor()),
            str(merged.getProviderFor()));
         addRow(sb, "branch", str(original.getBranch()), str(incoming.getBranch()), str(merged.getBranch()));
         addRow(sb, "validator", objOrNull(original.getValidator()), objOrNull(incoming.getValidator()),
            objOrNull(merged.getValidator()));
         addRow(sb, "swtValueListeners", str(original.getSwtValueListeners()), str(incoming.getSwtValueListeners()),
            str(merged.getSwtValueListeners()));
         addRow(sb, "viewerComparator", String.valueOf(original.getViewerComparator()),
            String.valueOf(incoming.getViewerComparator()), String.valueOf(merged.getViewerComparator()));
         addRow(sb, "xModListeners", str(original.getxModListeners()), str(incoming.getxModListeners()),
            str(merged.getxModListeners()));
         addRow(sb, "labelProvider", String.valueOf(original.getLabelProvider()),
            String.valueOf(incoming.getLabelProvider()), String.valueOf(merged.getLabelProvider()));
         addRow(sb, "object", String.valueOf(original.getObject()), String.valueOf(incoming.getObject()),
            String.valueOf(merged.getObject()));
         addRow(sb, "formToolkit", objOrNull(original.getFormToolkit()), objOrNull(incoming.getFormToolkit()),
            objOrNull(merged.getFormToolkit()));
         addRow(sb, "managedForm", objOrNull(original.getManagedForm()), objOrNull(incoming.getManagedForm()),
            objOrNull(merged.getManagedForm()));
         addRow(sb, "xWidget", objOrNull(original.getxWidget()), objOrNull(incoming.getxWidget()),
            objOrNull(merged.getxWidget()));
         addRow(sb, "widgetDef", String.valueOf(original.getWidgetDef()), String.valueOf(incoming.getWidgetDef()),
            String.valueOf(merged.getWidgetDef()));

         sb.append("</table></body></html>");
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.WARNING, "Failed to write XWidgetData debug HTML", ex);
         sb.append(Lib.exceptionToString(ex));
      }
      return sb;
   }

   private static String objOrNull(Object obj) {
      return obj == null ? "" : obj.getClass().getSimpleName();
   }

   private static void addRow(StringBuilder sb, String field, String orig, String incoming, String merged) {
      boolean differs = !orig.equals(incoming) || !incoming.equals(merged);
      String cls = differs ? " class='diff'" : "";
      sb.append("<tr").append(cls).append(">");
      sb.append("<td><b>").append(escHtml(field)).append("</b></td>");
      sb.append("<td>").append(escHtml(orig)).append("</td>");
      sb.append("<td>").append(escHtml(incoming)).append("</td>");
      sb.append("<td>").append(escHtml(merged)).append("</td>");
      sb.append("</tr>");
   }

   private static String str(Object obj) {
      return obj == null ? "" : String.valueOf(obj);
   }

   private static String escHtml(String s) {
      if (s == null || s.isEmpty()) {
         return "";
      }
      return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
   }

}
