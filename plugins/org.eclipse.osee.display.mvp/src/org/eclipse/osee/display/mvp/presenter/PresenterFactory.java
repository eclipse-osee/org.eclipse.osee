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
package org.eclipse.osee.display.mvp.presenter;

import java.util.Locale;
import org.eclipse.osee.display.mvp.event.EventBus;
import org.eclipse.osee.display.mvp.view.View;

/**
 * @author Roberto E. Escobar
 */
public interface PresenterFactory {

   boolean canCreate(Class<? extends Presenter<? extends View, ? extends EventBus>> presenterType);

   <T extends Presenter<? extends View, ? extends EventBus>> T createPresenter(Class<? extends Presenter<? extends View, ? extends EventBus>> presenterType, Locale locale) throws CreatePresenterException;

}
