/*
 * Created on Sep 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test.import1;

import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.CoveragePlugin;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class NavigationButton1 extends Button {

   public Image image;

   /**
    * @param parent
    * @param style
    */
   public NavigationButton1(Composite parent, int style, Image image) {
      super(parent, style);
   }

   @Override
   public String getText() {
      try {
         if (getStyle() == 4) { // 2, 1, y
            return "Navigate Here"; // 2, 2, y
         } else
            return "Navigate There"; // 2, 3, y
      } catch (Exception ex) {
         OseeLog.log(CoveragePlugin.class, Level.SEVERE, ex); // 2, 4, n
      }
      return "Navigate";
   }

   @Override
   public void setImage(Image image) {
      this.image = image; // 3, 1, y
   }

   @Override
   public void setText(String string) {
      super.setText(string); // 4, 1, n
   }

   @Override
   public Image getImage() {
      try {
         if (getStyle() == 4) { // 5, 1, y
            return this.image; // 5, 2, n
         } else
            return super.getImage(); // 5, 3, y
      } catch (IllegalArgumentException ex) {
         OseeLog.log(CoveragePlugin.class, Level.SEVERE, ex); // 5, 4, n

      } catch (Exception ex) {
         OseeLog.log(CoveragePlugin.class, Level.SEVERE, ex); // 5, 5, n
      }
      return null; // 5, 6, n
   }

}
