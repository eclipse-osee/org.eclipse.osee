/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { Component } from '@angular/core';
import { MatSidenavModule } from '@angular/material/sidenav';
import MessagingHelpContentComponent from 'src/app/ple/messaging/messaging-help/lib/messaging-help-content/messaging-help-content.component';
import { MessagingHelpNavigationComponent } from 'src/app/ple/messaging/messaging-help/lib/messaging-help-navigation/messaging-help-navigation.component';

@Component({
	selector: 'osee-messaging-help',
	templateUrl: './messaging-help.component.html',
	styleUrls: ['./messaging-help.component.sass'],
	standalone: true,
	imports: [
		MatSidenavModule,
		MessagingHelpNavigationComponent,
		MessagingHelpContentComponent,
	],
})
export class MessagingHelpComponent {
	constructor() {}
}
export default MessagingHelpComponent;
