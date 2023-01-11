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
import { TypeGridComponent } from '../type-grid/type-grid/type-grid.component';

@Component({
	selector: 'osee-messaging-types-type-grid',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockTypeGridComponent implements Partial<TypeGridComponent> {
	@Input() filterValue: string = '';
}
