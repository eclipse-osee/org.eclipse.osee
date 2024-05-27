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

import { Component, model } from '@angular/core';

import { ElementDialog } from '@osee/messaging/shared/types';

import { ElementFormComponent } from './element-form.component';

@Component({
	selector: 'osee-element-form',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockElementFormComponent implements Partial<ElementFormComponent> {
	data = model.required<ElementDialog>();
}
