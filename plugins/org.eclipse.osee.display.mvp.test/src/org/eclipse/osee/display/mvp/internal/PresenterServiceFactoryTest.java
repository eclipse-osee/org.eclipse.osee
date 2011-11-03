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
package org.eclipse.osee.display.mvp.internal;

import java.util.Locale;
import org.eclipse.osee.display.mvp.event.EventBus;
import org.eclipse.osee.display.mvp.mocks.MockLog;
import org.eclipse.osee.display.mvp.mocks.Presenter1;
import org.eclipse.osee.display.mvp.mocks.Presenter2;
import org.eclipse.osee.display.mvp.mocks.View2;
import org.eclipse.osee.display.mvp.presenter.AbstractPresenterFactory;
import org.eclipse.osee.display.mvp.presenter.Presenter;
import org.eclipse.osee.display.mvp.presenter.PresenterAnnotationException;
import org.eclipse.osee.display.mvp.presenter.PresenterNotFoundException;
import org.eclipse.osee.display.mvp.view.AbstractViewFactory;
import org.eclipse.osee.display.mvp.view.View;
import org.eclipse.osee.display.mvp.view.ViewNotFoundException;
import org.eclipse.osee.logger.Log;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link PresenterServiceFactoryImpl}
 * 
 * @author Roberto E. Escobar
 */
public class PresenterServiceFactoryTest {

   @Test(expected = IllegalStateException.class)
   public void testStart() throws Exception {
      PresenterServiceFactoryImpl factory = new PresenterServiceFactoryImpl();
      factory.setLogger(new MockLog());

      factory.createPresenter(Locale.getDefault(), Presenter1.class);
   }

   @Test(expected = PresenterNotFoundException.class)
   public void testPresenterNotFound() throws Exception {
      PresenterServiceFactoryImpl factory = new PresenterServiceFactoryImpl();
      factory.setLogger(new MockLog());
      factory.start();

      factory.createPresenter(Locale.getDefault(), Presenter1.class);
   }

   @Test(expected = PresenterAnnotationException.class)
   public void testPresenterAnnotation() throws Exception {
      PresenterServiceFactoryImpl factory = new PresenterServiceFactoryImpl();
      factory.setLogger(new MockLog());
      factory.addPresenterFactory(new MockPresenterFactory());
      factory.start();

      factory.createPresenter(Locale.getDefault(), Presenter1.class);
   }

   @Test(expected = ViewNotFoundException.class)
   public void testViewNotFound() throws Exception {
      PresenterServiceFactoryImpl factory = new PresenterServiceFactoryImpl();
      factory.setLogger(new MockLog());
      factory.addPresenterFactory(new MockPresenterFactory());
      factory.start();

      factory.createPresenter(Locale.getDefault(), Presenter2.class);
   }

   @Test
   public void testBinding() throws Exception {
      Log logger = new MockLog();
      PresenterServiceFactoryImpl factory = new PresenterServiceFactoryImpl();
      factory.setLogger(logger);
      factory.addPresenterFactory(new MockPresenterFactory());
      factory.addViewFactory(new MockViewFactory());
      factory.start();

      Presenter<? extends View, ? extends EventBus> presenter =
         factory.createPresenter(Locale.getDefault(), Presenter2.class);
      Assert.assertEquals(Presenter2.class, presenter.getClass());
      Assert.assertEquals(View2.class, presenter.getView().getClass());
      Assert.assertNotNull(presenter.getEventBus());
      Assert.assertEquals(logger, presenter.getLogger());
   }

   private final class MockPresenterFactory extends AbstractPresenterFactory {

      @SuppressWarnings({"unchecked"})
      @Override
      public <T extends Presenter<? extends View, ? extends EventBus>> T createPresenter(Class<? extends Presenter<? extends View, ? extends EventBus>> presenterType, Locale locale) {
         return (T) new Presenter2();
      }
   }

   private final class MockViewFactory extends AbstractViewFactory {

      @SuppressWarnings({"unchecked"})
      @Override
      public <T extends View> T createView(Presenter<?, ?> presenter, Class<T> viewType, Locale locale) {
         return (T) new View2();
      }

   }
}
