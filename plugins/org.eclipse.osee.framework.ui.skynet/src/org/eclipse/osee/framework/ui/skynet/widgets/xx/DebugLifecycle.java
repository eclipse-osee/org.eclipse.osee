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

package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * Opt-in lifecycle, SWT-event, and performance diagnostics for {@link XXAbstractBaseXWidget}. This class keeps the
 * instrumentation mechanics and usage guidance out of the widget implementation.
 * <p>
 * Diagnostic workflow:
 * <ol>
 * <li>Choose modes in {@link XWidget}; {@link XWidget#debugXWidgets} is the master switch.</li>
 * <li>For performance measurement, enable {@link XWidget#debugXWidgetPerformance} only. Leave lifecycle, stacks, and
 * SWT events disabled because console output, stack construction, and listeners distort timing.</li>
 * <li>For a lifecycle bug, enable {@link XWidget#debugXWidgetLifecycle}; add {@link XWidget#debugXWidgetStacks} to
 * identify callers and {@link XWidget#debugXWidgetSwtEvents} only for paint/resize/show/hide diagnosis.</li>
 * <li>Clear the console after the editor settles, perform exactly one controlled action, and capture the output.</li>
 * <li>For performance analysis, filter to {@code XXWIDGET PERF} and {@code WFE REFRESH PERF}. For lifecycle analysis,
 * filter to {@code XXWIDGET LIFECYCLE} and include the action entry through the final SWT events.</li>
 * <li>Paste the filtered results to an AI for sequence/call-stack/timing analysis. Repeat performance actions at least
 * three times and compare the median; the first request may include connection/cache warm-up.</li>
 * </ol>
 * <p>
 * Lifecycle lines contain a process-wide sequence, wall-clock epoch milliseconds, monotonic elapsed milliseconds,
 * widget/control identity, selected/displayed values, and thread. Performance lines contain monotonic operation
 * durations without expensive stacks/events.
 * <p>
 * Detailed examples, field interpretation, the resolved same-state {@code Section.setExpanded(...)} flicker case,
 * and VPN measurements are maintained in
 * <code>.kiro/steering/conditional/docs/ide_swt_refresh_problem.md</code>, which references
 * <code>docs/ai/ide/ide_swt_refresh_problem.md</code> as the durable source.
 * <p>
 * Keep all diagnostics behind the mode switches. The master switch and diagnostic modes must not be enabled by
 * default in merged production code.
 *
 * @author Donald G. Dunne
 */
public final class DebugLifecycle {

   private static final AtomicLong SEQUENCE = new AtomicLong();
   private static final long START_NANOS = System.nanoTime();

   private DebugLifecycle() {
      // Utility class
   }

   static void log(XXAbstractBaseXWidget<?> widget, Collection<?> selected, String operation, String detail,
      Object... data) {
      if (!XWidget.debugXWidgets.isLogToSysErr() || !XWidget.debugXWidgetLifecycle) {
         return;
      }
      String valueText = Widgets.isAccessible(widget.valueWidget) ? widget.valueWidget.getText()
         : Widgets.isAccessible(widget.valueSText) ? widget.valueSText.getText() : "<no-value-control>";
      String formattedDetail = data.length == 0 ? detail : String.format(detail, data);
      long nowNanos = System.nanoTime();
      long sequence = SEQUENCE.incrementAndGet();
      double elapsedMs = (nowNanos - START_NANOS) / 1_000_000.0;
      StringBuilder message = new StringBuilder(512);
      message.append("XXWIDGET LIFECYCLE #").append(sequence).append(" epochMs=").append(System.currentTimeMillis())
         .append(" elapsedMs=").append(String.format("%.3f", elapsedMs)).append(' ').append(operation).append(" [")
         .append(widget.getLabel()).append("] ").append(widget.getWidgetId().getName()).append(" instance=")
         .append(System.identityHashCode(widget)).append(" control=")
         .append(Widgets.isAccessible(widget.getControl()) ? System.identityHashCode(widget.getControl()) : "NA")
         .append(" selected=").append(selected).append(" text=[").append(valueText).append("] thread=")
         .append(Thread.currentThread().getName());
      if (Strings.isValid(formattedDetail)) {
         message.append(" detail=").append(formattedDetail);
      }
      message.append('\n');

      if (XWidget.debugXWidgetStacks) {
         int frameCount = 0;
         for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
            if (frame.getClassName().equals(Thread.class.getName()) ||
               frame.getClassName().equals(DebugLifecycle.class.getName())) {
               continue;
            }
            message.append("    at ").append(frame).append('\n');
            if (++frameCount == 12) {
               break;
            }
         }
      }
      XWidget.debugXWidgets.syserr("%s", message);
   }

   static void logDuration(XXAbstractBaseXWidget<?> widget, long startNanos, String operation, String detail,
      Object... data) {
      if (!XWidget.debugXWidgets.isLogToSysErr() || !XWidget.debugXWidgetPerformance) {
         return;
      }
      long nowNanos = System.nanoTime();
      long sequence = SEQUENCE.incrementAndGet();
      String formattedDetail = data.length == 0 ? detail : String.format(detail, data);
      double elapsedMs = (nowNanos - START_NANOS) / 1_000_000.0;
      double durationMs = (nowNanos - startNanos) / 1_000_000.0;
      XWidget.debugXWidgets.syserr(
         "XXWIDGET PERF #%s epochMs=%s elapsedMs=%.3f durationMs=%.3f operation=%s label=[%s] widgetId=%s%s\n",
         sequence, System.currentTimeMillis(), elapsedMs, durationMs, operation, widget.getLabel(),
         widget.getWidgetId().getName(), Strings.isValid(formattedDetail) ? " detail=" + formattedDetail : "");
   }

   static void addControlEvents(XXAbstractBaseXWidget<?> widget, Collection<?> selected, Control control, String role) {
      if (!XWidget.debugXWidgets.isLogToSysErr() || !XWidget.debugXWidgetLifecycle ||
         !XWidget.debugXWidgetSwtEvents || !Widgets.isAccessible(control)) {
         return;
      }
      int[] eventTypes = {SWT.Paint, SWT.Resize, SWT.Move, SWT.Show, SWT.Hide, SWT.Dispose};
      for (int eventType : eventTypes) {
         control.addListener(eventType,
            event -> log(widget, selected, "SWT." + eventName(event.type), "%s bounds=%s visible=%s enabled=%s", role,
               control.getBounds(), control.getVisible(), control.getEnabled()));
      }
      if (control instanceof Text || control instanceof StyledText) {
         control.addListener(SWT.Modify,
            event -> log(widget, selected, "SWT.Modify", "%s bounds=%s visible=%s enabled=%s", role,
               control.getBounds(), control.getVisible(), control.getEnabled()));
      }
   }

   private static String eventName(int eventType) {
      switch (eventType) {
         case SWT.Paint:
            return "Paint";
         case SWT.Resize:
            return "Resize";
         case SWT.Move:
            return "Move";
         case SWT.Show:
            return "Show";
         case SWT.Hide:
            return "Hide";
         case SWT.Dispose:
            return "Dispose";
         default:
            return String.valueOf(eventType);
      }
   }
}
