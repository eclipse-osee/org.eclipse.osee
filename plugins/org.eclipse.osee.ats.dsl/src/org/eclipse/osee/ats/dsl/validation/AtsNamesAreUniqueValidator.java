/*
 * Created on Feb 2, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.dsl.validation;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osee.ats.dsl.atsDsl.UserRef;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.NamesAreUniqueValidator;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;

public class AtsNamesAreUniqueValidator extends NamesAreUniqueValidator {
   AtsNamesAreUniqueValidationHelper atsHelper;

   public AtsNamesAreUniqueValidator() {
      super();
      atsHelper = new AtsNamesAreUniqueValidationHelper();
   }

   public class AtsNamesAreUniqueValidationHelper extends org.eclipse.xtext.validation.NamesAreUniqueValidationHelper {

      @Override
      public void checkUniqueNames(Iterable<IEObjectDescription> descriptions, ValidationMessageAcceptor acceptor) {
         super.checkUniqueNames(descriptions, acceptor);
      }

      @Override
      public void checkUniqueNames(Iterable<IEObjectDescription> descriptions, CancelIndicator cancelIndicator, ValidationMessageAcceptor acceptor) {
         List<IEObjectDescription> validDescriptions = new ArrayList<IEObjectDescription>();
         for (IEObjectDescription description : descriptions) {
            if (!(description.getEObjectOrProxy() instanceof UserRef)) {
               validDescriptions.add(description);
            }
         }
         super.checkUniqueNames(validDescriptions, cancelIndicator, acceptor);
      }

   }

   @Override
   public void checkUniqueNamesInResourceOf(EObject eObject) {
      if (!(getHelper() instanceof AtsNamesAreUniqueValidationHelper)) {
         setHelper(atsHelper);
      }
      super.checkUniqueNamesInResourceOf(eObject);
   }

}
