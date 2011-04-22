/*
 * Created on Feb 2, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.dsl.validation;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.NamesAreUniqueValidator;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;

/**
 * Osee specific name validator that ignores types names cause they conflict between types.<br>
 * <br>
 * Types instead will be checked via OseeDslJavaValidator
 * 
 * @author Donald G. Dunne
 */
public class OseeNamesAreUniqueValidator extends NamesAreUniqueValidator {
   OseeNamesAreUniqueValidationHelper oseeHelper;

   public OseeNamesAreUniqueValidator() {
      super();
      oseeHelper = new OseeNamesAreUniqueValidationHelper();
   }

   public class OseeNamesAreUniqueValidationHelper extends org.eclipse.xtext.validation.NamesAreUniqueValidationHelper {

      @Override
      public void checkUniqueNames(Iterable<IEObjectDescription> descriptions, ValidationMessageAcceptor acceptor) {
         super.checkUniqueNames(descriptions, acceptor);
      }

      @Override
      public void checkUniqueNames(Iterable<IEObjectDescription> descriptions, CancelIndicator cancelIndicator, ValidationMessageAcceptor acceptor) {
         List<IEObjectDescription> validDescriptions = new ArrayList<IEObjectDescription>();
         for (IEObjectDescription description : descriptions) {
            if (!(description.getEObjectOrProxy() instanceof XArtifactType) && !(description.getEObjectOrProxy() instanceof XAttributeType) && !(description.getEObjectOrProxy() instanceof XRelationType)) {
               validDescriptions.add(description);
            }
         }
         super.checkUniqueNames(validDescriptions, cancelIndicator, acceptor);
      }
   }

   @Override
   public void checkUniqueNamesInResourceOf(EObject eObject) {
      if (!(getHelper() instanceof OseeNamesAreUniqueValidationHelper)) {
         setHelper(oseeHelper);
      }
      super.checkUniqueNamesInResourceOf(eObject);
   }

}
