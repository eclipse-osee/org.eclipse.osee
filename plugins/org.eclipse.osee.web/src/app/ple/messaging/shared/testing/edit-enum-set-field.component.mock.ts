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

import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Subject } from 'rxjs';
import { enumerationSet, PlatformType } from '@osee/messaging/shared/types';
import { EditEnumSetFieldComponent } from '@osee/messaging/shared/forms';

@Component({
	selector: 'osee-edit-enum-set-field',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockEditEnumSetFieldComponent
	implements Partial<EditEnumSetFieldComponent>
{
	@Input() editable: boolean = false;

	@Input() platformTypeId: string | undefined;

	//type enumset loading case 2: by type
	@Input() platformType: PlatformType | undefined;

	@Output() enumUpdated = new EventEmitter<enumerationSet | undefined>();

	@Output('unique') unique = new Subject<boolean>();
}
