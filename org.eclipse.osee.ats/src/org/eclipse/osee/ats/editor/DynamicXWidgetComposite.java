/*
 * Created on Nov 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.swt.widgets.Composite;
import org.xml.sax.SAXException;

/**
 * @author Donald G. Dunne
 */
public class DynamicXWidgetComposite extends Composite {

   private final String xWidgetsXml;
   private List<DynamicXWidgetLayoutData> layoutDatas;
   private final DynamicXWidgetLayout dynamicXWidgetLayout;
   private final XWidgetParser xWidgetParser;

   public DynamicXWidgetComposite(String xWidgetsXml, Composite parent, int style) {
      super(parent, style);
      this.xWidgetsXml = xWidgetsXml;
      this.xWidgetParser = new XWidgetParser();
      this.dynamicXWidgetLayout = new DynamicXWidgetLayout();
      this.layoutDatas = new LinkedList<DynamicXWidgetLayoutData>();

   }

   public List<DynamicXWidgetLayoutData> getLayoutDatas() throws IllegalArgumentException, OseeCoreException, ParserConfigurationException, SAXException, IOException, CoreException {
      if (layoutDatas.isEmpty()) {
         layoutDatas = XWidgetParser.extractWorkAttributes(dynamicXWidgetLayout, xWidgetsXml);
      }
      return layoutDatas;
   }

}
