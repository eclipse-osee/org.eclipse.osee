/*********************************************************************
 * Copyright (c) 2023 Boeing
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

@Component({
	selector: 'osee-messaging-edit-sub-message-field',
	template: '<button>Ok</button>',
	standalone: true,
	imports: [],
})
export class MockEditMessageFieldComponent<T = unknown, R = unknown> {
	@Input() messageId!: string;
	@Input() subMessageId!: string;
	@Input() header: R = {} as R;
	@Input() value: T = {} as T;
}
