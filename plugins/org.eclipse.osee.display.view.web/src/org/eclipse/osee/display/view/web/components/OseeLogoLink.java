/*
 * Created on Oct 3, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.display.view.web.components;

import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Link;

@SuppressWarnings("serial")
public class OseeLogoLink extends Link {

   public OseeLogoLink(Navigator navigator, String styleName, Class<?> viewClass) {
      //super("", new ExternalResource(String.format("ats#%s", navigator.getUri(viewClass))));
      super("", new ExternalResource("ats"));
      Resource logoIconRes = new ThemeResource("../osee/osee_large.png");
      setIcon(logoIconRes);
      setStyleName(styleName);

   }
}
