/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.workdef.model;

import org.eclipse.osee.ats.api.workdef.WidgetOption;

/**
 * @author Donald G. Dunne
 */
public class MeetingAttendeeWidgetDefinition extends WidgetDefinition {

   public MeetingAttendeeWidgetDefinition() {
      super("Meeting Attendee(s)", "XHyperlinkMeetingAttendeesSelectionDam");
   }

   public LayoutItem andRequired() {
      set(WidgetOption.RFT);
      return this;
   }

   public LayoutItem andRequiredForFormal() {
      set(WidgetOption.REQUIRED_FOR_FORMAL_REVIEW);
      return this;
   }

}
