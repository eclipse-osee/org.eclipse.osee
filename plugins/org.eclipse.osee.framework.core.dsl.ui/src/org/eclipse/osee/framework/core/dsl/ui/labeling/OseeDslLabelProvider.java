/*********************************************************************
 * Copyright (c) 2018 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.dsl.ui.labeling;

import com.google.inject.Inject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

/**
 * Provides labels for a EObjects. see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 * 
 * @author Roberto E. Escobar
 */
public class OseeDslLabelProvider extends DefaultEObjectLabelProvider {

   @Inject
   public OseeDslLabelProvider(AdapterFactoryLabelProvider labelProvider) {
      super(labelProvider);
   }
}
