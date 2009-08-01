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
package org.eclipse.osee.framework.ui.skynet.widgets.hex;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.osee.framework.ui.skynet.FontManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

/**
 * A simple TableViewer to demonstrate usage of an ILazyContentProvider. You can compare this snippet to the
 * Snippet029VirtualTableViewer to see the small but needed difference.
 * 
 * @author Tom Schindl <tom.schindl@bestsolution.at>
 */
public abstract class HexTable extends Composite {

   private final TableViewer v;
   private final int bytesPerRow;

   public HexTable(Composite parent, int style, byte[] array, int bytesPerRow) {
      super(parent, SWT.NONE);
      this.bytesPerRow = bytesPerRow;
      v = new TableViewer(this, SWT.VIRTUAL | SWT.FULL_SELECTION | style);
      v.setContentProvider(new HexTableContentProvider(v, bytesPerRow));
      v.setUseHashlookup(true);
      TableColumnLayout layout = new TableColumnLayout();
      setLayout(layout);
      createAndConfigureColumns(v, layout, bytesPerRow);
      TableViewerFocusCellManager focusCellManager =
            new TableViewerFocusCellManager(v, new FocusCellOwnerDrawHighlighter(v));
      ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(v) {
         @Override
         protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
            return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR) || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
         }
      };

      TableViewerEditor.create(
            v,
            focusCellManager,
            actSupport,
            ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);

      v.setInput(array);
      v.setItemCount((array.length + bytesPerRow - 1) / bytesPerRow);
      //  v.getTable().setLinesVisible(true);
      v.getTable().setHeaderVisible(false);

   }

   protected ByteColumnLabelProvider createByteColumnLabelProvider(int column) {
      return new ByteColumnLabelProvider(FontManager.getCourierNew8(), column);
   }

   protected HexEditingSupport createHexEditingSupport(int column) {
      return new HexEditingSupport(FontManager.getCourierNew8(), v, column);
   }

   public int getBytesPerRow() {
      return bytesPerRow;
   }

   abstract protected void createAndConfigureColumns(TableViewer viewer, TableColumnLayout layout, int bytesPerRow);

   public IHexTblHighlighter createHighlighter(final int index, final int length, Color color) {
      return new Highlighter((HexTableContentProvider) v.getContentProvider(), index, length, color);
   }

   @Override
   public void dispose() {
      super.dispose();
   }

}