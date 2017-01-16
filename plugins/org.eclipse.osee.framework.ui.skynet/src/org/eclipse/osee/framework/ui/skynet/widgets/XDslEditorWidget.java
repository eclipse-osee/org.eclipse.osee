/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.DslGrammar;
import org.eclipse.osee.framework.ui.skynet.internal.DslGrammarManager;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.XtextSourceViewer;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditor;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditorFactory;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditorFactory.Builder;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditorModelAccess;
import org.eclipse.xtext.ui.editor.embedded.IEditedResourceProvider;
import org.eclipse.xtext.ui.editor.outline.impl.OutlinePage;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("restriction")
public class XDslEditorWidget extends XText {

   public static final String WIDGET_ID = XDslEditorWidget.class.getSimpleName();

   private DslGrammar grammar;
   private EmbeddedEditorModelAccess model;
   private EmbeddedEditor editor;
   private URI uri;

   private String extension;

   public XDslEditorWidget(String displayLabel) {
      super(displayLabel);
   }

   public void setExtension(String extension) {
      this.extension = extension;
   }

   protected String getExtension() {
      return extension;
   }

   protected DslGrammar getGrammar() {
      if (grammar == null && Strings.isValid(extension)) {
         DslGrammar grammar = DslGrammarManager.getDslByExtension(extension);
         setGrammar(grammar);
      }
      return grammar;
   }

   protected void setGrammar(DslGrammar grammar) {
      this.grammar = grammar;
   }

   protected URI getUri() {
      if (uri == null && Strings.isValid(extension)) {
         String uriString = String.format("dslEditor.%s.%s", Lib.generateUuid(), getExtension());
         uri = URI.createURI(uriString);
      }
      return uri;
   }

   protected void setUri(URI uri) {
      this.uri = uri;
   }

   protected EmbeddedEditorModelAccess getModel() {
      return model;
   }

   protected EmbeddedEditor getEditor() {
      return editor;
   }

   @Override
   protected void createControls(final Composite parent, int horizontalSpan) {
      setNotificationsAllowed(false);
      try {
         if (!verticalLabel && horizontalSpan < 2) {
            horizontalSpan = 2;
         }

         // Create Text Widgets
         if (isDisplayLabel() && Strings.isValid(getLabel())) {
            labelWidget = new Label(parent, SWT.NONE);
            labelWidget.setText(getLabel() + ":");
            if (getToolTip() != null) {
               labelWidget.setToolTipText(getToolTip());
            }
         }

         Composite composite = new Composite(parent, SWT.BORDER);

         if (fillVertically) {
            GridLayout layout = ALayout.getZeroMarginLayout(1, false);
            layout.verticalSpacing = 4;
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
         } else {
            GridLayout layout = ALayout.getZeroMarginLayout(horizontalSpan, false);
            layout.verticalSpacing = 4;
            composite.setLayout(layout);
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
            gd.horizontalSpan = horizontalSpan;
            composite.setLayoutData(gd);
         }
         createEditor(composite, horizontalSpan);

         addToolTip(composite, getToolTip());
      } finally {
         setNotificationsAllowed(true);
      }
      refresh();
   }

   private void createEditor(Composite composite, int horizontalSpan) {
      final DslGrammar grammar = getGrammar();
      IEditedResourceProvider resourceProvider = new IEditedResourceProvider() {

         @Override
         public XtextResource createResource() {
            try {
               ResourceSet resourceSet = new XtextResourceSet();
               Resource resource = resourceSet.createResource(getUri(), grammar.getExtension());
               return (XtextResource) resource;
            } catch (Exception e) {
               return null;
            }
         }
      };
      EmbeddedEditorFactory factory = grammar.getObject(EmbeddedEditorFactory.class);
      Builder builder = factory.newEditor(resourceProvider).showErrorAndWarningAnnotations();
      if (!isEditable()) {
         builder.readOnly();
      }
      editor = builder.withParent(composite);
      try {
         model = editor.createPartialEditor();
      } catch (Exception ex) {
         ex.printStackTrace();
      }

      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 200;

      editor.getViewer().getControl().setLayoutData(gd);
      editor.getViewer().addTextListener(new ITextListener() {

         @Override
         public void textChanged(TextEvent event) {
            XtextSourceViewer viewer = editor.getViewer();
            if (viewer != null) {
               if (Widgets.isAccessible(viewer.getTextWidget())) {
                  Displays.ensureInDisplayThread(new Runnable() {
                     @Override
                     public void run() {
                        notifyXModifiedListeners();
                     }
                  });
               }
            }
         }
      });
   }

   private void addToolTip(Control control, String toolTipText) {
      if (Strings.isValid(toolTipText)) {
         control.setToolTipText(toolTipText);
         if (control instanceof Composite) {
            for (Control child : ((Composite) control).getChildren()) {
               child.setToolTipText(toolTipText);
            }
         }
      }
   }

   @Override
   public Control getControl() {
      Control control = null;
      if (editor != null) {
         control = editor.getViewer().getControl();
      }
      return control;
   }

   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (editor != null) {
         editor.getViewer().setEditable(editable);
      }
   }

   @Override
   public void setText(String text) {
      if (model != null) {
         model.updateModel("", text, "");
      }
   }

   @Override
   public String getText() {
      return get();
   }

   @Override
   public void setFocus() {
      Control control = getControl();
      if (control != null) {
         control.setFocus();
      }
   }

   @Override
   protected void updateTextWidget() {
      EmbeddedEditor editor = getEditor();
      if (editor != null) {
         XtextSourceViewer viewer = editor.getViewer();
         if (viewer != null) {
            if (Widgets.isAccessible(viewer.getTextWidget())) {
               validate();
            }
         }
      }
   }

   @Override
   public String get() {
      return model != null ? model.getSerializedModel() : "";
   }

   @Override
   public Object getData() {
      return get();
   }

   @Override
   public void dispose() {
      if (editor != null) {
         XtextSourceViewer viewer = editor.getViewer();
         if (viewer != null) {
            Control control = editor.getViewer().getControl();
            if (control != null && !control.isDisposed()) {
               control.dispose();
            }
         }
      }
      super.dispose();
   }

   public ContentOutlinePage getOutlinePage() {
      OutlinePage page = null;
      if (editor != null) {
         DslGrammar grammar = getGrammar();
         if (grammar != null) {
            page = grammar.getObject(OutlinePage.class);
            page.setSourceViewer(editor.getViewer());
         }
      }
      return page;
   }

}
