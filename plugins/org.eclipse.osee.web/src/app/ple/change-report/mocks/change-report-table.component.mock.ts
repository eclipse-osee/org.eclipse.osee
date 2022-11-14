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
import { ChangeReportTableComponent } from '../components/change-report-table/change-report-table.component';

@Component({
	selector: 'osee-change-report-table',
	template: '<p>Dummy</p>',
})
export class MockChangeReportTableComponent
	implements Partial<ChangeReportTableComponent>
{
	@Input() branchId: string = '10';
	@Input() tx1: string = '1';
	@Input() tx2: string = '2';

	constructor() {}
}
