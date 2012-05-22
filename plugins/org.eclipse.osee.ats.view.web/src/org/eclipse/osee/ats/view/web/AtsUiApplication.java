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
package org.eclipse.osee.ats.view.web;

import java.util.Locale;
import org.eclipse.osee.ats.ui.api.data.AtsSearchParameters;
import org.eclipse.osee.ats.ui.api.search.AtsSearchPresenter;
import org.eclipse.osee.ats.ui.api.view.AtsSearchHeaderComponent;
import org.eclipse.osee.display.view.web.HasLogger;
import org.eclipse.osee.display.view.web.HasNavigator;
import org.eclipse.osee.display.view.web.HasPresenter;
import org.eclipse.osee.display.view.web.HasUrl;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.vaadin.AbstractApplication;
import org.eclipse.osee.vaadin.widgets.HasViews;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import com.vaadin.ui.Window;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class AtsUiApplication<T extends AtsSearchHeaderComponent, K extends AtsSearchParameters> extends AbstractApplication implements HasUrl, HasNavigator, HasPresenter, HasLogger {

   private final AtsSearchPresenter<T, K> atsSearchPresenter;
   private String url = "";
   private final Log logger;

   public AtsUiApplication(AtsSearchPresenter<T, K> searchPresenter, Log logger) {
      super();
      this.atsSearchPresenter = searchPresenter;
      this.logger = logger;
      setTheme("osee");
   }

   @Override
   protected Window createApplicationWindow(Locale locale) {
      HasViews viewProvider = new AtsUiViews();
      AtsWindow window = new AtsWindow(viewProvider, new AtsNavigator());
      return window;
   }

   @Override
   protected String getApplicationWindowName() {
      return "AtsUiApplication";
   }

   @Override
   public AtsNavigator getNavigator() {
      AtsWindow atsWindow = (AtsWindow) getCurrentWindow();
      return atsWindow.getNavigator();
   }

   @Override
   public AtsSearchPresenter<T, K> getPresenter() {
      return atsSearchPresenter;
   }

   @Override
   public String getUrl() {
      return url;
   }

   @Override
   public void setUrl(String url) {
      this.url = url;
   }

   @Override
   public String getVersion() {
      Bundle bundle = FrameworkUtil.getBundle(this.getClass());
      return bundle.getVersion().toString();
   }

   @Override
   public void logError(String format, Object... args) {
      logger.error(format, args);
   }

   @Override
   public void logWarn(String format, Object... args) {
      logger.warn(format, args);
   }

   @Override
   public void logInfo(String format, Object... args) {
      logger.info(format, args);
   }

   @Override
   public void logDebug(String format, Object... args) {
      logger.debug(format, args);
   }

}
