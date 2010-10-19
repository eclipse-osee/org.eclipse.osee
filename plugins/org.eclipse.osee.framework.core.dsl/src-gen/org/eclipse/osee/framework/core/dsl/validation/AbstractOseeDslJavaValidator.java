/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.validation;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.ComposedChecks;

@ComposedChecks(validators = {
   org.eclipse.xtext.validation.ImportUriValidator.class,
   org.eclipse.xtext.validation.NamesAreUniqueValidator.class})
public class AbstractOseeDslJavaValidator extends AbstractDeclarativeValidator {

   @Override
   protected List<EPackage> getEPackages() {
      List<EPackage> result = new ArrayList<EPackage>();
      result.add(org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage.eINSTANCE);
      return result;
   }

}
