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

import java.net.URLEncoder;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.DslGrammar;
import org.eclipse.osee.framework.ui.skynet.DslGrammarStorageAdapter;
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
public class XDslEditorWidget extends XLabel implements IAttributeWidget {

   public static final String WIDGET_ID = XDslEditorWidget.class.getSimpleName();

   private Artifact artifact;
   private IAttributeType attributeType;
   private DslGrammar grammar;
   private EmbeddedEditorModelAccess model;
   private EmbeddedEditor editor;
   private URI uri;

   public XDslEditorWidget(String displayLabel) {
      super(displayLabel);
   }

   @Override
   protected void createControls(final Composite parent, int horizontalSpan) {
      setNotificationsAllowed(false);
      try {
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

         // Create Text Widgets
         if (isDisplayLabel() && Strings.isValid(getLabel())) {
            labelWidget = new Label(composite, SWT.NONE);
            labelWidget.setText(getLabel() + ":");
            if (getToolTip() != null) {
               labelWidget.setToolTipText(getToolTip());
            }
         }

         createEditor(composite);

         addToolTip(composite, getToolTip());
      } finally {
         setNotificationsAllowed(true);
      }
      refresh();
   }

   private void createEditor(Composite composite) {
      IEditedResourceProvider resourceProvider = new IEditedResourceProvider() {

         @Override
         public XtextResource createResource() {
            try {
               ResourceSet resourceSet = new ResourceSetImpl();
               Resource resource = resourceSet.createResource(uri, grammar.getExtension());
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
      model = editor.createPartialEditor();

      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      gridData.heightHint = 500;
      editor.getViewer().getControl().setLayoutData(gridData);
      editor.getViewer().addTextListener(new ITextListener() {

         @Override
         public void textChanged(TextEvent event) {
            if (editor != null) {
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
   public void saveToArtifact() throws OseeCoreException {
      String value = get();
      if (!Strings.isValid(value)) {
         getArtifact().deleteSoleAttribute(getAttributeType());
      } else if (!value.equals(getArtifact().getSoleAttributeValue(getAttributeType(), ""))) {
         getArtifact().setSoleAttributeValue(getAttributeType(), value);
      }
   }

   @Override
   public Result isDirty() throws OseeCoreException {
      Result toReturn = Result.FalseResult;
      if (isEditable()) {
         String enteredValue = get();
         String storedValue = getArtifact().getSoleAttributeValue(getAttributeType(), "");
         if (!enteredValue.equals(storedValue)) {
            return new Result(true, attributeType + " is dirty");
         }
      }
      return toReturn;
   }

   public void setText(String text) {
      if (model != null) {
         model.updateModel("", text, "");
      }
   }

   public String get() {
      return model != null ? getStorageAdapter().postProcess(getArtifact(), model.getSerializedModel()) : "";
   }

   @Override
   public void setAttributeType(Artifact artifact, IAttributeType attributeType) throws OseeCoreException {
      this.artifact = artifact;
      this.attributeType = attributeType;
      this.grammar = DslGrammarManager.getGrammar(attributeType);
      if (grammar == null) {
         OseeLog.log(getClass(), Level.SEVERE, "Could not find a grammar for attribute type " + attributeType);
      } else {
         this.uri = createURI(artifact, grammar.getExtension());
      }

      updateTextWidget();
   }

   private URI createURI(Artifact artifact, String extension) throws OseeCoreException {
      String encodedName = "";
      try {
         encodedName = URLEncoder.encode(artifact.getSafeName(), "UTF-8");
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      String uriString =
         String.format("branch/%s/artifact/%d/%s.%s", artifact.getBranchUuid(), artifact.getGuid(), encodedName,
            extension);
      return URI.createURI(uriString);
   }

   protected void updateTextWidget() {
      if (editor != null) {
         XtextSourceViewer viewer = editor.getViewer();
         if (viewer != null) {
            if (Widgets.isAccessible(viewer.getTextWidget())) {
               String storedValue;
               try {
                  storedValue = getArtifact().getSoleAttributeValue(getAttributeType(), "");
                  model.updateModel("", getStorageAdapter().preProcess(getArtifact(), storedValue), "");
                  // Re-enable Listeners
                  validate();
               } catch (OseeCoreException ex) {
                  //
               }
            }
         }
      }
   }

   private DslGrammarStorageAdapter getStorageAdapter() {
      DslGrammarStorageAdapter storageAdapter = grammar.getStorageAdapter();
      if (storageAdapter == null) {
         storageAdapter = new DslGrammarStorageAdapter() {

            @Override
            public String preProcess(Artifact artifact, String storedValue) {
               return storedValue;
            }

            @Override
            public String postProcess(Artifact artifact, String serializedModel) {
               return serializedModel;
            }
         };
      }
      return storageAdapter;
   }

   @Override
   public void refresh() {
      updateTextWidget();
      super.refresh();
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

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public IAttributeType getAttributeType() {
      return attributeType;
   }

   @Override
   public void revert() throws OseeCoreException {
      setAttributeType(getArtifact(), getAttributeType());
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         status = OseeValidator.getInstance().validate(IOseeValidator.SHORT, getArtifact(), getAttributeType(), get());
      }
      return status;
   }

   public ContentOutlinePage getOutlinePage() {
      OutlinePage page = null;
      if (editor != null) {
         page = grammar.getObject(OutlinePage.class);
         page.setSourceViewer(editor.getViewer());
      }
      return page;
   }
}
