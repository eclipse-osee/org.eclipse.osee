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
import { ConnectionDropdownComponent } from './connection-dropdown.component';
import { connection, connectionSentinel } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-connection-dropdown',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockConnectionDropdownComponent
	implements Partial<ConnectionDropdownComponent>
{
	@Input() required: boolean = false;
	@Input() disabled: boolean = false;

	@Input() showNoneOption: boolean = false;
	@Input() connection: connection = connectionSentinel;

	@Output() connectionChange = new Subject<connection>();

	@Input() errorMatcher: ErrorStateMatcher =
		new ShowOnDirtyErrorStateMatcher();
}
