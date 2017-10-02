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
package org.eclipse.osee.ote.ui.define.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.ui.define.OteDefineImage;
import org.eclipse.osee.ote.ui.define.panels.CommentComposite;
import org.eclipse.osee.ote.ui.define.panels.IOverrideHandler;
import org.eclipse.osee.ote.ui.define.panels.SelectionComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Roberto E. Escobar
 */
public class CommitDialog extends TitleAreaDialog {

   private static final Image MESSAGE_IMAGE = ImageManager.getImage(OteDefineImage.COMMIT_WIZ);
   private static final Image TITLE_BAR_IMAGE = ImageManager.getImage(OteDefineImage.COMMIT);

   private static final String MESSAGE_TITLE = "Enter a commit comment";
   private static final String TITLE_BAR_TEXT = "OSEE Commit";
   private static final String COMMENT_GROUP_TEXT = "Comment";
   private static final String MESSAGE =
      "You can specify a comment describing the changes in the area below. " + "Empty comments are allowed, but adding a comment would help others understand your changes. \n";
   private static final String CONFLICT_MESSAGE =
      "NOTE: Exclamation marks denote items that already exist in the repository or are missing revision infromation. These items will not be committed. \n";

   private SelectionComposite selectionComposite;
   private CommentComposite commentComposite;
   private final ITableLabelProvider tableLabelProvider;
   private Object[] selectable;
   private Object[] unselectable;
   private Object[] selected;
   private final String[] columnNames;
   private IOverrideHandler overrideHandler;

   public CommitDialog(Shell parent, String[] columnNames, ITableLabelProvider tableLabelProvider) {
      super(parent);
      setShellStyle(SWT.SHELL_TRIM);
      this.columnNames = columnNames;
      this.tableLabelProvider = tableLabelProvider;
      this.selectable = this.selected = new Object[0];
      this.overrideHandler = null;
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
      separator.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
      return super.createButtonBar(parent);
   }

   /*
    * @see Dialog#createDialogArea(Composite)
    */
   @Override
   protected Control createDialogArea(Composite parent) {
      Composite content = (Composite) super.createDialogArea(parent);

      Composite composite = new Composite(content, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      SashForm sash = new SashForm(composite, SWT.VERTICAL);
      sash.setLayout(new GridLayout());
      sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createCommentArea(sash);
      createCommitArea(sash);
      sash.setWeights(new int[] {1, 2});

      setTitle(MESSAGE_TITLE);
      setTitleImage(MESSAGE_IMAGE);
      setMessage(MESSAGE + (unselectable != null && unselectable.length > 0 ? CONFLICT_MESSAGE : ""));
      getShell().setText(TITLE_BAR_TEXT);
      getShell().setImage(TITLE_BAR_IMAGE);
      return sash;
   }

   private void createCommentArea(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginHeight = 0;
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true));

      Group group = new Group(composite, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      group.setText(COMMENT_GROUP_TEXT);

      commentComposite = new CommentComposite(group, SWT.NONE);
      commentComposite.setLayout(new GridLayout());
      commentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
      separator.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
   }

   private void createCommitArea(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.verticalSpacing = 0;
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      selectionComposite = new SelectionComposite(composite, SWT.BORDER, columnNames, tableLabelProvider, selectable,
         true, selected, unselectable, overrideHandler);
      selectionComposite.setLayout(new GridLayout());
      selectionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
   }

   public void setInput(Object[] input) {
      this.selectable = input;
   }

   public void setSelected(Object[] selected) {
      this.selected = selected;
   }

   public void setUnSelectable(Object[] unselectable) {
      this.unselectable = unselectable;
   }

   public void setOverrideHandler(IOverrideHandler overrideHandler) {
      this.overrideHandler = overrideHandler;
   }

   public String getComments() {
      return commentComposite.getMessage();
   }

   public Object[] getSelectedResources() {
      return selectionComposite.getSelectedResources();
   }
}
