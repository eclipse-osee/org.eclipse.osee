/*
 * Created on Oct 8, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.util;

import java.util.Collection;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class NotSaveable implements ISaveable {

   @Override
   public Result isEditable() {
      return new Result("Not Editable");
   }

   @Override
   public Result save() {
      return new Result("Not Saveable");
   }

   @Override
   public Result save(Collection<ICoverage> coverages) throws OseeCoreException {
      return new Result("Not Saveable");
   }

}
