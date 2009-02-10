/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.results.chart;

import java.util.logging.Level;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.factory.Generator;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.util.PluginSettings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

public class ChartViewerSWT implements PaintListener

{

   private IDeviceRenderer idr = null;
   private final Chart chart;

   public ChartViewerSWT(Chart chart) {
      this.chart = chart;
      // INITIALIZE THE SWT RENDERING DEVICE
      final PluginSettings ps = PluginSettings.instance();
      try {
         idr = ps.getDevice("dv.SWT");
      } catch (ChartException pex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, pex);
      }
   }

   /**
    * The SWT paint callback
    */

   public void paintControl(PaintEvent pe) {

      idr.setProperty(IDeviceRenderer.GRAPHICS_CONTEXT, pe.gc);
      Composite co = (Composite) pe.getSource();
      Rectangle re = co.getClientArea();
      Bounds bo = BoundsImpl.create(re.x, re.y, re.width, re.height);
      bo.scale(72d / idr.getDisplayServer().getDpiResolution()); // BOUNDS MUST BE SPECIFIED IN POINTS
      // BUILD AND RENDER THE CHART

      Generator gr = Generator.instance();
      try {
         gr.render(idr, gr.build(idr.getDisplayServer(), chart, null, bo, null));
      } catch (ChartException gex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, gex);
      }

   }

}