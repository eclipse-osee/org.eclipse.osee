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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.display.mvp.PresenterServiceFactory;
import org.eclipse.osee.display.mvp.event.EventBus;
import org.eclipse.osee.display.mvp.presenter.Presenter;
import org.eclipse.osee.display.mvp.presenter.PresenterAnnotationException;
import org.eclipse.osee.display.mvp.presenter.PresenterFactory;
import org.eclipse.osee.display.mvp.presenter.PresenterNotFoundException;
import org.eclipse.osee.display.mvp.presenter.annotation.IsPresenterFor;
import org.eclipse.osee.display.mvp.view.View;
import org.eclipse.osee.display.mvp.view.ViewFactory;
import org.eclipse.osee.display.mvp.view.ViewNotFoundException;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class PresenterServiceFactoryImpl implements PresenterServiceFactory {

   private final List<ViewFactory> viewFactories = new CopyOnWriteArrayList<ViewFactory>();
   private final List<PresenterFactory> presenterFactories = new CopyOnWriteArrayList<PresenterFactory>();

   private Log logger;
   private EventBusRegistryImpl eventBusAdmin;
   private volatile boolean wasStarted = false;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public Log getLogger() {
      return logger;
   }

   public EventBusRegistryImpl getEventBusAdmin() {
      return eventBusAdmin;
   }

   public void start() {
      eventBusAdmin = new EventBusRegistryImpl(getLogger());
      wasStarted = true;
   }

   public void stop() {
      wasStarted = false;
      eventBusAdmin = null;
   }

   private void checkInitialized() {
      if (!wasStarted) {
         throw new IllegalStateException("start() was not called");
      }
   }

   public void addViewFactory(ViewFactory factory) {
      viewFactories.add(factory);
   }

   public void removeViewFactory(ViewFactory factory) {
      viewFactories.remove(factory);
   }

   public void addPresenterFactory(PresenterFactory factory) {
      presenterFactories.add(factory);
   }

   public void removePresenterFactory(PresenterFactory factory) {
      presenterFactories.remove(factory);
   }

   protected PresenterFactory getPresenterFactory(Class<? extends Presenter<? extends View, ? extends EventBus>> presenterType) throws PresenterNotFoundException {
      PresenterFactory toReturn = null;
      for (PresenterFactory factory : presenterFactories) {
         if (factory.canCreate(presenterType)) {
            toReturn = factory;
            break;
         }
      }
      if (toReturn == null) {
         throw new PresenterNotFoundException("Unable to find presenter factory for [%s]", presenterType);
      }
      return toReturn;
   }

   protected ViewFactory getViewFactory(Class<? extends View> viewType) throws ViewNotFoundException {
      ViewFactory toReturn = null;
      for (ViewFactory factory : viewFactories) {
         if (factory.canCreate(viewType)) {
            toReturn = factory;
            break;
         }
      }
      if (toReturn == null) {
         throw new ViewNotFoundException("Unable to find view factory for [%s]", viewType);
      }
      return toReturn;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends Presenter<? extends View, ? extends EventBus>> T createPresenter(Locale locale, Class<T> presenterType) throws Exception {
      checkInitialized();
      Presenter<View, EventBus> presenter = null;
      try {

         PresenterFactory factory = getPresenterFactory(presenterType);
         presenter = factory.createPresenter(presenterType, locale);

         Class<? extends View> viewClazz = getViewClassFor(presenterType);
         ViewFactory viewFactory = getViewFactory(viewClazz);

         View view = viewFactory.createView(presenter, viewClazz, locale);
         presenter.setView(view);

         EventBus bus = createBus(presenter);
         presenter.setEventBus(bus);

         if (presenter.getLogger() == null) {
            presenter.setLogger(getLogger());
         }

         presenter.bind();
      } catch (Exception ex) {
         getLogger().error(ex, "Error creating presenter");
         throw ex;
      }
      return (T) presenter;
   }

   @SuppressWarnings("unchecked")
   protected EventBus createBus(Presenter<? extends View, ? extends EventBus> presenter) {
      Type type = getType(presenter.getClass());
      EventBus toReturn = null;
      if (EventBus.class.isAssignableFrom((Class<?>) type)) {
         Class<? extends EventBus> clazz = (Class<? extends EventBus>) type;
         getLogger().debug("Class: [%s]", clazz.getName());
         toReturn = getEventBusAdmin().register(clazz, presenter);
      }
      return toReturn;
   }

   protected Type getType(Class<?> presenterClass) {
      Type superClass = presenterClass.getGenericSuperclass();
      ParameterizedType pt = (ParameterizedType) superClass;
      Type[] typeArgs = pt.getActualTypeArguments();
      return typeArgs[1];
   }

   protected Class<? extends View> getViewClassFor(Class<? extends Presenter<? extends View, ? extends EventBus>> presenterClazz) throws PresenterAnnotationException {
      IsPresenterFor presenterBinding = presenterClazz.getAnnotation(IsPresenterFor.class);
      if (presenterBinding == null) {
         throw new PresenterAnnotationException("presenterClazz missing @IsPresenterFor annotation");
      }
      return presenterBinding.value();
   }
}
