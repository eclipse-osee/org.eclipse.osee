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
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Ryan D. Brooks
 */
public class AttributeMenuSelectionListener extends SelectionAdapter {
   private AttributesComposite attrsComp;
   private TableViewer tableViewer;
   private IDirtiableEditor editor;

   public AttributeMenuSelectionListener(AttributesComposite attrsComp, TableViewer tableViewer, IDirtiableEditor editor) {
      this.attrsComp = attrsComp;
      this.tableViewer = tableViewer;
      this.editor = editor;
   }

   /*
    * (non-Javadoc) @@see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclips
    * e.swt.events.SelectionEvent)
    */
   @Override
   public void widgetSelected(SelectionEvent ev) {
      AttributeType attributeType = (AttributeType) ((MenuItem) ev.getSource()).getData();
      attrsComp.getArtifact().createAttribute(attributeType, true);

      tableViewer.refresh();
      attrsComp.layout();
      attrsComp.getParent().layout();
      editor.onDirtied();
      attrsComp.notifyModifyAttribuesListeners();
   }
}