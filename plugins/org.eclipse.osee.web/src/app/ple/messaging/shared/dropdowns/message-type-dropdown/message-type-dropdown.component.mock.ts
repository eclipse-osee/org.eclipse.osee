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

import { Component, Input, Output } from '@angular/core';
import {
	ErrorStateMatcher,
	ShowOnDirtyErrorStateMatcher,
} from '@angular/material/core';
import { Subject } from 'rxjs';
import { MessageTypeDropdownComponent } from './message-type-dropdown.component';

@Component({
	selector: 'osee-message-type-dropdown',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockMessageTypeDropdownComponent
	implements Partial<MessageTypeDropdownComponent>
{
	@Input() required: boolean = false;
	@Input() disabled: boolean = false;

	@Input() hintHidden: boolean = false;
	@Input() messageType: string = '';

	@Output() messageTypeChange = new Subject<string>();

	@Input() errorMatcher: ErrorStateMatcher =
		new ShowOnDirtyErrorStateMatcher();
}
