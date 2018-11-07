/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.dsl.ui.labeling;

import com.google.inject.Inject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

/**
 * Provides labels for a EObjects. see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
/**
 * @author Donald G. Dunne
 */
public class AtsDslLabelProvider extends DefaultEObjectLabelProvider {

   @Inject
   public AtsDslLabelProvider(AdapterFactoryLabelProvider delegate) {
      super(delegate);
   }

   /*
    * //Labels and icons can be computed like this: String text(MyModel ele) { return "my "+ele.getName(); } String
    * image(MyModel ele) { return "MyModel.gif"; }
    */
}
