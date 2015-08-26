/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.ats.api.util.ColorColumns;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class AtsConfigurations {

   private final List<AtsConfiguration> configs = new ArrayList<AtsConfiguration>();
   private AtsViews views = new AtsViews();
   private ColorColumns colorColumns = new ColorColumns();

   public List<AtsConfiguration> getConfigs() {
      return configs;
   }

   public AtsViews getViews() {
      return views;
   }

   public void setViews(AtsViews views) {
      this.views = views;
   }

   public ColorColumns getColorColumns() {
      return colorColumns;
   }

   public void setColorColumns(ColorColumns colorColumns) {
      this.colorColumns = colorColumns;
   }

}
