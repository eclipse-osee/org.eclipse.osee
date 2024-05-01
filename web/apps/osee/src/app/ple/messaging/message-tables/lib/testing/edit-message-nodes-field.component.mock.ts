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
import { Component, Input } from '@angular/core';
import { message } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-edit-message-nodes-field',
	template: '<button>Ok</button>',
	standalone: true,
	imports: [],
})
export class MockEditMessageNodesFieldComponent {
	@Input() message!: message;
	@Input() header!: string;
	@Input() value!: string;
}
