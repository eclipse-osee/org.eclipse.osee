/*
 * Created on May 9, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class OseeXWidgetValidateManager {
   private static final String EXTENSION_ELEMENT = "OseeXWidgetValidator";
   private static final String EXTENSION_ID = Activator.PLUGIN_ID + "." + EXTENSION_ELEMENT;
   private static final String CLASS_NAME_ATTRIBUTE = "classname";
   public static OseeXWidgetValidateManager instance = new OseeXWidgetValidateManager();
   private static ExtensionDefinedObjects<IOseeXWidgetValidator> validators;

   private OseeXWidgetValidateManager() {
      validators =
         new ExtensionDefinedObjects<IOseeXWidgetValidator>(EXTENSION_ID, EXTENSION_ELEMENT, CLASS_NAME_ATTRIBUTE);
   }

   public Collection<IStatus> validate(Artifact artifact, String xWidgetName, String name) {
      List<IStatus> statuses = new ArrayList<IStatus>();
      for (IOseeXWidgetValidator validator : validators.getObjects()) {
         if (validator.isProvider(xWidgetName)) {
            statuses.add(validator.validate(artifact, xWidgetName, name));
         }
      }
      return statuses;
   }
}
