/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.mvp.view;

import java.util.Locale;
import org.eclipse.osee.display.mvp.presenter.Presenter;

/**
 * @author Roberto E. Escobar
 */
public interface ViewFactory {

   boolean canCreate(Class<? extends View> viewType);

   <T extends View> T createView(Presenter<?, ?> presenter, Class<T> viewType, Locale locale) throws CreateViewException;

}
