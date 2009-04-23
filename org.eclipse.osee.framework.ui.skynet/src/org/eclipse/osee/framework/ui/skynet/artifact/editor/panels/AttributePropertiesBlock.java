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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.BaseArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.implementations.NewArtifactEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 */
public class AttributePropertiesBlock {

   private SashForm sashForm;
   private final NewArtifactEditor editor;

   public AttributePropertiesBlock(NewArtifactEditor editor) {
      super();
      this.editor = editor;
   }

   public Composite createContent(IManagedForm managedForm, Composite parent) {
      final FormToolkit toolkit = managedForm.getToolkit();
      sashForm = new InternalSashForm(parent, SWT.NULL);
      sashForm.setData("form", managedForm);
      toolkit.adapt(sashForm, false, false);
      sashForm.setMenu(parent.getMenu());
      sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      createMasterPart(managedForm, sashForm);
      createDataPart(managedForm, sashForm);
      hookResizeListener();
      sashForm.setWeights(new int[] {4, 16});
      return sashForm;
   }

   private void hookResizeListener() {
      Listener listener = ((InternalSashForm) sashForm).listener;
      Control[] children = sashForm.getChildren();
      for (int i = 0; i < children.length; i++) {
         if (children[i] instanceof Sash) continue;
         children[i].addListener(SWT.Resize, listener);
      }
   }

   private void createMasterPart(final IManagedForm managedForm, Composite parent) {
      final FormToolkit toolkit = managedForm.getToolkit();

      Section section = toolkit.createSection(parent, Section.DESCRIPTION);
      section.setText("Controls");
      section.setDescription("List of attribute types to add");
      section.marginWidth = 0;
      section.marginHeight = 0;

      toolkit.createCompositeSeparator(section);

      Composite composite = toolkit.createComposite(section, SWT.WRAP);
      GridLayout layout = new GridLayout(2, false);
      layout.marginWidth = 2;
      layout.marginHeight = 2;
      composite.setLayout(layout);

      Table table = toolkit.createTable(composite, SWT.NULL);
      table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
      toolkit.paintBordersFor(composite);

      Button b = toolkit.createButton(composite, "Add...", SWT.PUSH);
      b.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
      section.setClient(composite);
      final SectionPart spart = new SectionPart(section);
      managedForm.addPart(spart);
      TableViewer viewer = new TableViewer(table);
      viewer.addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            managedForm.fireSelectionChanged(spart, event.getSelection());
         }
      });
      viewer.setContentProvider(new InternalContentProvider());
      viewer.setLabelProvider(new InternalLabelProvider());
      viewer.setInput(editor.getEditorInput());
   }

   private void createDataPart(final IManagedForm mform, Composite parent) {
      AttributeDataPage detailsPage = new AttributeDataPage(editor);
      mform.addPart(detailsPage);
      detailsPage.createContents(parent);
   }

   private void onSashPaint(Event e) {
      Sash sash = (Sash) e.widget;
      IManagedForm form = (IManagedForm) sash.getParent().getData("form"); //$NON-NLS-1$
      FormColors colors = form.getToolkit().getColors();
      boolean vertical = (sash.getStyle() & SWT.VERTICAL) != 0;
      GC gc = e.gc;
      Boolean hover = (Boolean) sash.getData("hover"); //$NON-NLS-1$
      gc.setBackground(colors.getColor(IFormColors.TB_BG));
      gc.setForeground(colors.getColor(IFormColors.TB_BORDER));
      Point size = sash.getSize();
      if (vertical) {
         if (hover != null) gc.fillRectangle(0, 0, size.x, size.y);
         //else
         //gc.drawLine(1, 0, 1, size.y-1);
      } else {
         if (hover != null) gc.fillRectangle(0, 0, size.x, size.y);
         //else
         //gc.drawLine(0, 1, size.x-1, 1);            
      }
   }

   private final class InternalContentProvider implements IStructuredContentProvider {
      public Object[] getElements(Object inputElement) {
         if (inputElement instanceof BaseArtifactEditorInput) {
            Artifact artifact = editor.getEditorInput().getArtifact();
            return getEmptyTypes(artifact);
         }
         return new Object[0];
      }

      public void dispose() {
      }

      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      }

      protected AttributeType[] getEmptyTypes(Artifact artifact) {
         List<AttributeType> items = new ArrayList<AttributeType>();
         try {
            for (AttributeType type : artifact.getAttributeTypes()) {
               if (!type.getName().equals("Name") && artifact.getAttributes(type.getName()).isEmpty()) {
                  items.add(type);
               }
            }
            Collections.sort(items);
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
         return items.toArray(new AttributeType[items.size()]);
      }
   }

   private final class InternalLabelProvider extends LabelProvider implements ITableLabelProvider {
      public String getColumnText(Object obj, int index) {
         return obj.toString();
      }

      public Image getColumnImage(Object obj, int index) {
         if (obj instanceof AttributeType) {
            //            AttributeType type = (AttributeType) obj;
            //            Class<?> clazz = type.getBaseAttributeClass();
            //            ISharedImages.
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
         }
         return null;
      }
   }

   private final class InternalSashForm extends SashForm {
      List<Sash> sashes = new ArrayList<Sash>();
      Listener listener = new Listener() {
         public void handleEvent(Event e) {
            switch (e.type) {
               case SWT.MouseEnter:
                  e.widget.setData("hover", Boolean.TRUE);
                  ((Control) e.widget).redraw();
                  break;
               case SWT.MouseExit:
                  e.widget.setData("hover", null);
                  ((Control) e.widget).redraw();
                  break;
               case SWT.Paint:
                  onSashPaint(e);
                  break;
               case SWT.Resize:
                  hookSashListeners();
                  break;
            }
         }
      };

      public InternalSashForm(Composite parent, int style) {
         super(parent, style);
      }

      public void layout(boolean changed) {
         super.layout(changed);
         hookSashListeners();
      }

      public void layout(Control[] children) {
         super.layout(children);
         hookSashListeners();
      }

      private void hookSashListeners() {
         purgeSashes();
         Control[] children = getChildren();
         for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof Sash) {
               Sash sash = (Sash) children[i];
               if (sashes.contains(sash)) continue;
               sash.addListener(SWT.Paint, listener);
               sash.addListener(SWT.MouseEnter, listener);
               sash.addListener(SWT.MouseExit, listener);
               sashes.add(sash);
            }
         }
      }

      private void purgeSashes() {
         for (Iterator<Sash> iter = sashes.iterator(); iter.hasNext();) {
            Sash sash = iter.next();
            if (sash.isDisposed()) iter.remove();
         }
      }
   }

}
