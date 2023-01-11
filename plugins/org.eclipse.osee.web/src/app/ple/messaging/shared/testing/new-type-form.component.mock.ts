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
import { of } from 'rxjs';
import { PlatformType } from '../types/platformType';
import { NewTypeFormComponent } from '../forms/new-type-form/new-type-form.component';

@Component({
	selector: 'osee-new-type-form',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockNewTypeFormComponent implements Partial<NewTypeFormComponent> {
	@Input() preFillData?: PlatformType[];
	@Output() typeFormState = of({
		platformType: {},
		createEnum: false,
		enumSetId: '',
		enumSetName: '',
		enumSetDescription: '',
		enumSetApplicability: { id: '1', name: 'Base' },
		enums: [],
	});
}
