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

import { TransportsRoutingModule } from './transports-routing.module';
import { TransportsComponent } from './transports.component';
import { BranchPickerModule } from '../../../shared-components/components/branch-picker/branch-picker.module';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActionStateButtonModule } from '../../../shared-components/components/action-state-button/action-state-button.module';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { SharedMessagingModule } from '../shared/shared-messaging.module';

@NgModule({
	declarations: [TransportsComponent],
	imports: [
		CommonModule,
		BranchPickerModule,
		MatTableModule,
		MatTooltipModule,
		MatButtonModule,
		MatIconModule,
		MatDialogModule,
		SharedMessagingModule,
		ActionStateButtonModule,
		BranchPickerModule,
		TransportsRoutingModule,
	],
})
export class TransportsModule {}
