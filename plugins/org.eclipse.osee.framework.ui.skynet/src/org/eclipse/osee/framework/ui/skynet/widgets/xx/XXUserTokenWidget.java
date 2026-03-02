package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXUserTokenWidget extends XAbstractXXWidget<UserToken> {

   public static final WidgetId ID = WidgetId.XXUserTokenWidget;

   public XXUserTokenWidget() {
      this(ID, "User");
   }

   public XXUserTokenWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
   }

   @Override
   public void setWidData(XWidgetData widData) {
      super.setWidData(widData);
      setSingleSelect(true);
   }

   @Override
   public Collection<UserToken> getSelectable() {
      if (widData.is(XOption.ACTIVE)) {
         return OseeApiService.userSvc().getActiveUsers();
      }
      return OseeApiService.userSvc().getUsers();
   }

   @Override
   protected UserToken getSentinel() {
      return UserToken.SENTINEL;
   }

}
