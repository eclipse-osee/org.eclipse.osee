/*
 * Created on May 9, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Provider to validate XWidget/IAttributeWidget entry against Artifact store model without use of XWidget UI
 * 
 * @author Donald G. Dunne
 */
public interface IOseeXWidgetValidator {

   public boolean isProvider(String xWidgetName);

   public IStatus validate(Artifact artifact, String xWidgetName, String name);

}
