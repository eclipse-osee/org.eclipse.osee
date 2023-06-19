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
import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatStepperModule } from '@angular/material/stepper';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import {
	graphServiceMock,
	MockNewNodeFormComponent,
} from '@osee/messaging/connection-view/testing';
import { connectionMock, dialogRef } from '@osee/messaging/shared/testing';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';
import { DefaultAddNodeDialog } from '../../dialogs/add-node-dialog/add-node-dialog.default';
import { CurrentGraphService } from '../../services/current-graph.service';
import { AddNodeDialogComponent } from './add-node-dialog.component';

describe('AddNodeDialogComponent', () => {
	let component: AddNodeDialogComponent;
	let fixture: ComponentFixture<AddNodeDialogComponent>;
	let dialogData = new DefaultAddNodeDialog(connectionMock);

	beforeEach(async () => {
		await TestBed.overrideComponent(AddNodeDialogComponent, {
			set: {
				imports: [
					CommonModule,
					MatDialogModule,
					MatStepperModule,
					MatFormFieldModule,
					MatAutocompleteModule,
					MatButtonModule,
					MatInputModule,
					MatTooltipModule,
					MockMatOptionLoadingComponent,
					MockNewNodeFormComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [
					AddNodeDialogComponent,
					MatDialogModule,
					MatStepperModule,
					MatFormFieldModule,
					MatAutocompleteModule,
					MatButtonModule,
					MatInputModule,
					MatTooltipModule,
					NoopAnimationsModule,
					MockMatOptionLoadingComponent,
					MockNewNodeFormComponent,
				],
				providers: [
					{ provide: MatDialogRef, useValue: dialogRef },
					{ provide: MAT_DIALOG_DATA, useValue: dialogData },
					{
						provide: CurrentGraphService,
						useValue: graphServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(AddNodeDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
