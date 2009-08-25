package org.eclipse.osee.framework.types.bridge.operations;

import java.net.URI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.oseeTypes.Import;
import org.eclipse.osee.framework.oseeTypes.OseeType;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.osee.framework.types.bridge.internal.Activator;

public class TextModelToOseeOperation extends AbstractOperation {
   private final java.net.URI resource;

   public TextModelToOseeOperation(java.net.URI resource) {
      super("OSEE Text Model to OSEE", Activator.PLUGIN_ID);
      this.resource = resource;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      OseeTypeModel model = OseeModelUtil.loadModel(resource);
      for (Import importEntry : model.getImports()) {
         System.out.println("Import: " + importEntry.getImportURI());
         OseeTypeModel importedModel = OseeModelUtil.loadModel(new URI(importEntry.getImportURI()));
      }
      
      for (OseeType type : model.getTypes()) {
         
         
         System.out.println(type.getName());
         for (EObject eObject : type.eContents()) {
            System.out.println(eObject.toString());
         }
      }
   }

}
