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
import { MatTableModule } from '@angular/material/table';

import { DiffReportRoutingModule } from './diff-report-routing.module';
import { DiffReportComponent } from './diff-report.component';
import { DiffReportTableComponent } from './diff-report-table/diff-report-table.component';
import { NodeDiffsComponent } from './node-diffs/node-diffs.component';
import { ConnectionDiffsComponent } from './connection-diffs/connection-diffs.component';
import { MessageDiffsComponent } from './message-diffs/message-diffs.component';
import { MatButtonModule } from '@angular/material/button';
import { SubmessageDiffsComponent } from './submessage-diffs/submessage-diffs.component';
import { StructureDiffsComponent } from './structure-diffs/structure-diffs.component';
import { MatIconModule } from '@angular/material/icon';
import { GenericButtonsModule } from 'src/app/ple/generic-buttons/generic-buttons.module';

@NgModule({
	declarations: [
		DiffReportComponent,
		DiffReportTableComponent,
		NodeDiffsComponent,
		ConnectionDiffsComponent,
		MessageDiffsComponent,
		SubmessageDiffsComponent,
		StructureDiffsComponent,
	],
	imports: [
		CommonModule,
		DiffReportRoutingModule,
		GenericButtonsModule,
		MatButtonModule,
		MatIconModule,
		MatTableModule,
	],
})
export class DiffReportModule {}
