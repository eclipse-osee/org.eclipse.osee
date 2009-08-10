/*
 * Created on Aug 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer.util.internal;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class XViewerMenuDetectListener implements Listener {
   private final XViewer xViewer;

   public XViewerMenuDetectListener(XViewer xViewer) {
      this.xViewer = xViewer;
   }

   public void handleEvent(Event event) {
      Point point = Display.getCurrent().map(null, xViewer.getTree(), new Point(event.x, event.y));
      xViewer.processRightClickMouseEvent(point);
   }

}
