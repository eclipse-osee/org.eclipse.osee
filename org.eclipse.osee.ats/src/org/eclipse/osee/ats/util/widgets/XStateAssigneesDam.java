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
package org.eclipse.osee.ats.util.widgets;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;

/**
 * @author Donald G. Dunne
 */
public abstract class XStateAssigneesDam extends XTextDam {

   protected final String attributeName;
   protected final StateMachineArtifact sma;

   public XStateAssigneesDam(StateMachineArtifact sma, String attributeName) {
      super(attributeName);
      this.sma = sma;
      this.attributeName = attributeName;
      try {
         setArtifact(sma, attributeName);
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   @Override
   public String getUdatStringValue() throws SQLException {
      return "";
   }

   private Collection<Attribute<String>> getAttributes() throws SQLException {
      return sma.getAttributes(attributeName);
   }

   public SMAState getState(String stateName, boolean create) {
      try {
         for (Attribute<String> attr : getAttributes()) {
            if (attr.getValue().startsWith(stateName + ";")) {
               SMAState state = new SMAState();
               state.setFromXml((String) attr.getValue());
               return state;
            }
         }
         if (create) return new SMAState(stateName);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Error parsing state data for " + sma.getHumanReadableId(), ex, false);
      }
      return null;
   }

   public Set<SMAState> getStates() {
      Set<SMAState> states = new HashSet<SMAState>();
      try {
         for (Attribute<String> attr : getAttributes()) {
            SMAState state = new SMAState();
            state.setFromXml(attr.getValue());
            states.add(state);
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Error parsing state data for " + sma.getHumanReadableId(), ex, false);
      }
      return states;
   }

   public void setState(SMAState state) throws Exception {
      // Update attribute if it already exists
      try {
         for (Attribute<String> attr : getAttributes()) {
            SMAState storedState = new SMAState();
            storedState.setFromXml(attr.getValue());
            if (state.getName().equals(storedState.getName())) {
               attr.setValue(state.toXml());
               return;
            }
         }
         // Else, doesn't exist yet, create
         Attribute<String> newAttribute = sma.getAttributeManager(this.attributeName).getNewAttribute();
         newAttribute.setValue(state.toXml());
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Error setting state data for " + sma.getHumanReadableId(), ex, false);
      }
   }
}
