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
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
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
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.xtext.ui.editor.XtextSourceViewer;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditor;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditorModelAccess;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("restriction")
public class XDslEditorWidgetDam extends XDslEditorWidget implements IAttributeWidget {

   public static final String WIDGET_ID = XDslEditorWidgetDam.class.getSimpleName();

   private Artifact artifact;
   private AttributeTypeToken attributeType;

   public XDslEditorWidgetDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public void saveToArtifact()  {
      String value = get();
      if (!Strings.isValid(value)) {
         getArtifact().deleteSoleAttribute(getAttributeType());
      } else if (!value.equals(getArtifact().getSoleAttributeValue(getAttributeType(), ""))) {
         getArtifact().setSoleAttributeValue(getAttributeType(), value);
      }
   }

   @Override
   public Result isDirty()  {
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

   @Override
   public String get() {
      EmbeddedEditorModelAccess model = getModel();
      return model != null ? getStorageAdapter().postProcess(getArtifact(), model.getSerializedModel()) : "";
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType)  {
      this.artifact = artifact;
      this.attributeType = attributeType;
      DslGrammar grammar = DslGrammarManager.getGrammar(attributeType);
      if (grammar == null) {
         OseeLog.log(getClass(), Level.SEVERE, "Could not find a grammar for attribute type " + attributeType);
      } else {
         setGrammar(grammar);
         setExtension(grammar.getExtension());
         URI uri = createURI(artifact, getExtension());
         setUri(uri);
      }
      updateTextWidget();
   }

   private URI createURI(Artifact artifact, String extension)  {
      String encodedName = "";
      try {
         encodedName = URLEncoder.encode(artifact.getSafeName(), "UTF-8");
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      String uriString = String.format("branch/%s/artifact/%s/%s.%s", artifact.getBranch().getIdString(),
         artifact.getGuid(), encodedName, extension);
      return URI.createURI(uriString);
   }

   @Override
   public void refresh() {
      updateTextWidget();
      super.refresh();
   }

   @Override
   protected void updateTextWidget() {
      EmbeddedEditor editor = getEditor();
      if (editor != null) {
         XtextSourceViewer viewer = editor.getViewer();
         if (viewer != null) {
            if (Widgets.isAccessible(viewer.getTextWidget())) {
               EmbeddedEditorModelAccess model = getModel();
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
      DslGrammar grammar = getGrammar();
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
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public void revert()  {
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

}
