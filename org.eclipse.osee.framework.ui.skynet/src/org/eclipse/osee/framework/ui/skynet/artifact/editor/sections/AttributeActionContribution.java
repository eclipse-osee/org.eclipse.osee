/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.artifact.editor.sections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.IActionContributor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.implementations.NewArtifactEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

/**
 * @author Roberto E. Escobar
 */
public class AttributeActionContribution implements IActionContributor {

   private final NewArtifactEditor editor;

   public AttributeActionContribution(NewArtifactEditor editor) {
      this.editor = editor;
   }

   public void contributeToToolBar(IToolBarManager manager) {
      manager.add(new OpenAddAttributeTypeDialogAction());
      manager.add(new OpenDeleteAttributeTypeDialogAction());
   }

   private CheckedTreeSelectionDialog createDialog(String title, Image image, String message) {
      CheckedTreeSelectionDialog dialog =
            new CheckedTreeSelectionDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                  new LabelProvider(), new ArrayTreeContentProvider());
      dialog.setTitle(title);
      dialog.setImage(image);
      dialog.setMessage(message);
      dialog.setValidator(new ISelectionStatusValidator() {

         @Override
         public IStatus validate(Object[] selection) {
            if (selection.length == 0) {
               return new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID,
                     "Select at least one item or click cancel to exit.");
            }
            return Status.OK_STATUS;
         }
      });
      return dialog;
   }

   private void handleAttributeTypeEdits(Artifact artifact, boolean isAdd, String title, Image image) throws OseeCoreException {
      String operation = isAdd ? "add" : "delete";
      AttributeType[] types =
            isAdd ? AttributeTypeUtil.getEmptyTypes(artifact) : AttributeTypeUtil.getTypesWithData(artifact);
      List<AttributeType> input = new ArrayList<AttributeType>(Arrays.asList(types));
      if (!isAdd) {
         for (AttributeType type : types) {
            if (type.getMinOccurrences() > 0 && artifact.getAttributeTypes().contains(type)) {
               input.remove(type);
            }
         }
      }
      if (input.isEmpty()) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(), title, String.format(
               "No attribute types available to %s.", operation));
      } else {
         CheckedTreeSelectionDialog dialog =
               createDialog(title, image, String.format("Select items to %s.", operation));
         dialog.setInput(input);
         int result = dialog.open();
         if (result == Window.OK) {
            Object[] objects = dialog.getResult();
            if (objects.length > 0) {
               for (Object object : objects) {
                  String attributeTypeName = ((AttributeType) object).getName();
                  if (isAdd) {
                     artifact.addAttributeFromString(attributeTypeName, "");
                  } else {
                     artifact.deleteAttributes(attributeTypeName);
                  }
               }
            }
         }
      }
   }

   private final class OpenAddAttributeTypeDialogAction extends Action {
      public OpenAddAttributeTypeDialogAction() {
         super();
         ImageDescriptor addImage = SkynetGuiPlugin.getInstance().getImageDescriptor("add.gif");
         setImageDescriptor(addImage);
         setToolTipText("Opens a dialog to select which attribute type instances to create on the artifact");
      }

      public void run() {
         try {
            Artifact artifact = editor.getEditorInput().getArtifact();
            Image image = SkynetGuiPlugin.getInstance().getImage("add.gif");
            handleAttributeTypeEdits(artifact, true, "Add Attribute Types", image);
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private final class OpenDeleteAttributeTypeDialogAction extends Action {
      public OpenDeleteAttributeTypeDialogAction() {
         super();
         ImageDescriptor deleteImage = SkynetGuiPlugin.getInstance().getImageDescriptor("delete.gif");
         setImageDescriptor(deleteImage);
         setToolTipText("Opens a dialog to select which attribute type instances to remove from the artifact");
      }

      public void run() {
         try {
            Artifact artifact = editor.getEditorInput().getArtifact();
            Image image = SkynetGuiPlugin.getInstance().getImage("delete.gif");
            handleAttributeTypeEdits(artifact, false, "Delete Attribute Types", image);
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }
}