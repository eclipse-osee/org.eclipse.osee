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
package org.eclipse.osee.framework.ui.skynet.widgets.xx.util;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;

/**
 * Click-to-edit behavior for a read-only XX widget {@link StyledText} value (OseeInfo.useClickEdit()). Clicking the value
 * makes it editable in place with Save/Cancel buttons; Enter (single-line) or Ctrl+Enter (multi-line) saves, Esc
 * cancels, and focus leaving the widget saves. The owning widget supplies validation and persistence so the normal XX
 * storage path (setSelected, handleSelectedPersist, refresh) is unchanged.
 * <p>
 * Must be created before the widget hides the caret, so the original caret can be restored while editing.
 *
 * @author Donald G. Dunne
 */
public class XXClickEditor {

   private static final int MAX_EDIT_HEIGHT = 500;

   private final Composite comp;
   private final StyledText sText;
   private final boolean multiLine;
   private final BooleanSupplier canEdit;
   private final Supplier<String> storedText;
   private final Function<String, String> validator;
   private final Consumer<String> onSave;
   private final Caret caret;
   private Composite actionBar;
   private boolean editing = false;
   private boolean hovered = false;
   private String errorMsg;
   private String savedToolTip;
   private Color readOnlyBackground;
   private Color readOnlyForeground;
   private int origHeightHint = SWT.DEFAULT;
   private int minEditHeight;
   private int lastEditHeight;

   /**
    * @param canEdit evaluated on each click so enable/disable and read-only changes are honored
    * @param storedText the persisted display value; restored on cancel
    * @param validator returns an error message, or null when the text can be saved
    * @param onSave persists the validated text; only called when the text changed
    */
   public XXClickEditor(Composite comp, StyledText sText, FormToolkit toolkit, boolean multiLine, BooleanSupplier canEdit, Supplier<String> storedText, Function<String, String> validator, Consumer<String> onSave) {
      this.comp = comp;
      this.sText = sText;
      this.multiLine = multiLine;
      this.canEdit = canEdit;
      this.storedText = storedText;
      this.validator = validator;
      this.onSave = onSave;
      this.caret = sText.getCaret();
      createActionBar(toolkit);
      addListeners();
   }

   private void createActionBar(FormToolkit toolkit) {
      actionBar = toolkit != null ? toolkit.createComposite(comp, SWT.NONE) : new Composite(comp, SWT.NONE);
      RowLayout layout = new RowLayout(SWT.HORIZONTAL);
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      layout.spacing = 4;
      actionBar.setLayout(layout);
      GridData gd = new GridData(SWT.END, SWT.CENTER, true, false);
      // Own row across all of the widget's columns; excluded until editing
      if (comp.getLayout() instanceof GridLayout gridLayout) {
         gd.horizontalSpan = gridLayout.numColumns;
      }
      gd.exclude = true;
      actionBar.setLayoutData(gd);
      actionBar.setVisible(false);

      Button saveButton = createButton(toolkit, "Save");
      saveButton.setToolTipText(multiLine ? "Save (Ctrl+Enter)" : "Save (Enter)");
      saveButton.addListener(SWT.Selection, e -> save());

      Button cancelButton = createButton(toolkit, "Cancel");
      cancelButton.setToolTipText("Discard changes (Esc)");
      cancelButton.addListener(SWT.Selection, e -> cancel());
   }

   private Button createButton(FormToolkit toolkit, String text) {
      if (toolkit != null) {
         return toolkit.createButton(actionBar, text, SWT.PUSH);
      }
      Button button = new Button(actionBar, SWT.PUSH);
      button.setText(text);
      return button;
   }

   private void addListeners() {
      sText.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseDown(MouseEvent e) {
            boolean plainClick = e.button == 1 && (e.stateMask & SWT.MODIFIER_MASK) == 0;
            if (!editing && plainClick && !isOnUrl(e) && canEdit.getAsBoolean()) {
               enterEditMode();
            }
         }
      });
      sText.addMouseTrackListener(new MouseTrackAdapter() {
         @Override
         public void mouseEnter(MouseEvent e) {
            setHovered(canEdit.getAsBoolean());
         }

         @Override
         public void mouseExit(MouseEvent e) {
            setHovered(false);
         }
      });
      // VerifyKey runs before StyledText's own key handling, so doit=false keeps Enter from inserting a newline
      sText.addVerifyKeyListener(e -> {
         if (!editing) {
            return;
         }
         if (e.keyCode == SWT.ESC) {
            e.doit = false;
            cancel();
         } else if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
            if (!multiLine || (e.stateMask & SWT.MOD1) != 0) {
               e.doit = false;
               save();
            }
         }
      });
      sText.addModifyListener(e -> {
         if (editing) {
            validate();
            autoExpand();
         }
      });
      // Deferred so focus has settled; moving to Save/Cancel stays in the widget and must not auto-save
      sText.addListener(SWT.FocusOut, e -> sText.getDisplay().asyncExec(() -> {
         if (editing && Widgets.isAccessible(comp) && !isInWidget(sText.getDisplay().getFocusControl())) {
            save();
         }
      }));
      comp.addPaintListener(e -> paintBorder(e.gc));
   }

   public boolean isEditing() {
      return editing;
   }

   /**
    * Ends an in-progress edit before another edit path (e.g. the label's editor dialog) takes over: saves if valid,
    * otherwise discards.
    */
   public void stopEditing() {
      if (editing && !save()) {
         cancel();
      }
   }

   private void enterEditMode() {
      editing = true;
      errorMsg = null;
      savedToolTip = sText.getToolTipText();
      readOnlyBackground = sText.getBackground();
      readOnlyForeground = sText.getForeground();
      updateCursor();
      if (multiLine && sText.getLayoutData() instanceof GridData gd) {
         origHeightHint = gd.heightHint;
         minEditHeight = sText.getSize().y;
         lastEditHeight = minEditHeight;
      }
      sText.setEditable(true);
      sText.setCaret(caret);
      sText.setFocus();
      setActionBarVisible(true);
      autoExpand();
      comp.redraw();
   }

   /**
    * Multi-line only, while editing: grows the value to fit its content so the user sees everything being typed. Never
    * shrinks below the height it had when editing started and caps at MAX_EDIT_HEIGHT, after which it scrolls.
    */
   private void autoExpand() {
      if (!multiLine || !editing || !(sText.getLayoutData() instanceof GridData gd)) {
         return;
      }
      int width = sText.getSize().x;
      if (width <= 0) {
         return;
      }
      int contentHeight = sText.computeSize(width, SWT.DEFAULT, true).y;
      int height = Math.min(Math.max(contentHeight, minEditHeight), Math.max(minEditHeight, MAX_EDIT_HEIGHT));
      // Change-aware: no layout/reflow while typing within the current height
      if (height == lastEditHeight) {
         return;
      }
      lastEditHeight = height;
      gd.heightHint = height;
      relayout(sText);
   }

   private void restoreHeight() {
      if (multiLine && sText.getLayoutData() instanceof GridData gd && gd.heightHint != origHeightHint) {
         gd.heightHint = origHeightHint;
         relayout(sText);
      }
   }

   private boolean save() {
      if (!editing) {
         return true;
      }
      String text = sText.getText();
      if (!validate()) {
         return false;
      }
      boolean changed = !text.equals(storedText.get());
      exitEditMode();
      if (changed) {
         onSave.accept(text);
      }
      return true;
   }

   private void cancel() {
      if (!editing) {
         return;
      }
      sText.setText(storedText.get());
      exitEditMode();
   }

   private void exitEditMode() {
      editing = false;
      errorMsg = null;
      sText.setToolTipText(savedToolTip);
      sText.setEditable(false);
      sText.setSelection(0);
      sText.setCaret(null);
      restoreColors();
      updateCursor();
      restoreHeight();
      setActionBarVisible(false);
      comp.redraw();
   }

   /**
    * The theme (notably Dark Mode CSS) can restyle the control while it is editable; put back the read-only colors
    * the widget had before editing so it matches its neighbors again.
    */
   private void restoreColors() {
      if (readOnlyBackground != null && !readOnlyBackground.isDisposed()) {
         sText.setBackground(readOnlyBackground);
      }
      if (readOnlyForeground != null && !readOnlyForeground.isDisposed()) {
         sText.setForeground(readOnlyForeground);
      }
   }

   private boolean validate() {
      String error = validator.apply(sText.getText());
      String newError = Strings.isValid(error) ? error : null;
      if (newError == null ? errorMsg != null : !newError.equals(errorMsg)) {
         errorMsg = newError;
         sText.setToolTipText(newError == null ? savedToolTip : newError);
         comp.redraw();
      }
      return newError == null;
   }

   private void setHovered(boolean hovered) {
      if (this.hovered != hovered) {
         this.hovered = hovered;
         updateCursor();
         comp.redraw();
      }
   }

   /**
    * Hand while hovering an editable read-only value signals it is clickable; null restores StyledText's I-beam.
    */
   private void updateCursor() {
      sText.setCursor(hovered && !editing ? sText.getDisplay().getSystemCursor(SWT.CURSOR_HAND) : null);
   }

   private void setActionBarVisible(boolean visible) {
      GridData gd = (GridData) actionBar.getLayoutData();
      if (gd.exclude == !visible) {
         return;
      }
      gd.exclude = !visible;
      actionBar.setVisible(visible);
      relayout(actionBar);
   }

   /**
    * Targeted layout flushes cached sizes up the chain; then update the scrolled form extent.
    */
   private void relayout(Control changed) {
      comp.getParent().layout(new Control[] {changed});
      for (Composite parent = comp.getParent(); parent != null; parent = parent.getParent()) {
         if (parent instanceof SharedScrolledComposite) {
            ((SharedScrolledComposite) parent).reflow(true);
            break;
         }
      }
   }

   private void paintBorder(GC gc) {
      if (!editing && !hovered || !Widgets.isAccessible(sText)) {
         return;
      }
      Rectangle bounds = sText.getBounds();
      if (errorMsg != null) {
         gc.setForeground(comp.getDisplay().getSystemColor(SWT.COLOR_RED));
      } else if (editing) {
         gc.setForeground(comp.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
      } else {
         gc.setForeground(comp.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
         gc.setLineStyle(SWT.LINE_DOT);
      }
      gc.drawRectangle(bounds.x - 1, bounds.y - 1, bounds.width + 1, bounds.height + 1);
   }

   private boolean isOnUrl(MouseEvent e) {
      int offset = sText.getOffsetAtPoint(new Point(e.x, e.y));
      if (offset < 0) {
         return false;
      }
      StyleRange range = sText.getStyleRangeAtOffset(offset);
      return range != null && range.underline && range.underlineStyle == SWT.UNDERLINE_LINK;
   }

   private boolean isInWidget(Control control) {
      for (Control c = control; c != null; c = c.getParent()) {
         if (c == comp) {
            return true;
         }
      }
      return false;
   }

}
