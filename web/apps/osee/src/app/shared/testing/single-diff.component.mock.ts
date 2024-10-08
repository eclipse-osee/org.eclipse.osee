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

@Component({
	selector: 'osee-single-diff',
	template: '<div>Dummy</div>',
	standalone: true,
})
export class MockSingleDiffComponent {
	@Input() field: string = '';
	@Input() currentValue: any = '';
	@Input() previousValue: any = '';
	@Input() user: string = '';
	@Input() date: string = '';
}
