/*********************************************************************
 * Copyright (c) 2022 Boeing
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
	selector: 'osee-action-dropdown',
	template: '<div>Dummy</div>',
})
// eslint-disable-next-line @angular-eslint/component-class-suffix
export class ActionDropdownStub {
	@Input() category: string = '';
	@Input() workType: string = '';
}
