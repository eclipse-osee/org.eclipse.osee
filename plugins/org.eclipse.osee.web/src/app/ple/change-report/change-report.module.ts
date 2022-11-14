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
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ChangeReportRoutingModule } from './change-report-routing.module';
import { BranchPickerModule } from 'src/app/shared-components/components/branch-picker/branch-picker.module';
import { ChangeReportTableComponent } from './components/change-report-table/change-report-table.component';
import { ChangeReportComponent } from './change-report.component';
import { MatTableModule } from '@angular/material/table';

@NgModule({
	declarations: [ChangeReportComponent, ChangeReportTableComponent],
	imports: [
		BranchPickerModule,
		CommonModule,
		ChangeReportRoutingModule,
		MatTableModule,
	],
})
export class ChangeReportModule {}
