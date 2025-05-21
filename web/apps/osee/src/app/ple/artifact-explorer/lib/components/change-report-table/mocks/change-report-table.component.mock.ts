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

import { Component, input } from '@angular/core';
import { ChangeReportTableComponent } from '../change-report-table.component';

@Component({
	selector: 'osee-change-report-table',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockChangeReportTableComponent
	implements Partial<ChangeReportTableComponent>
{
	branchId = input<string>('10');
	tx1 = input<string>('1');
	tx2 = input<string>('2');

	constructor() {}
}
