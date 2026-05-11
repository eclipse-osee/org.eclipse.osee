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
import { Component, input, output } from '@angular/core';
import type { subMessage, message } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-messaging-sub-message-table',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockSubMessageTableComponent {
	data = input.required<subMessage[]>();
	filter = input.required<string>();

	message = input.required<message>();
	editMode = input.required<boolean>();
	expandRow = output<message>();
}
