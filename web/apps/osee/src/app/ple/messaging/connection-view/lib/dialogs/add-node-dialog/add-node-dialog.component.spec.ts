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
import { AsyncPipe } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import {
	MatStep,
	MatStepper,
	MatStepperNext,
	MatStepperPrevious,
} from '@angular/material/stepper';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { MockNewNodeFormComponent } from '@osee/messaging/connection-view/testing';
import { NodeReviewComponent } from '@osee/messaging/nodes/review';
import { MockNodeSearchComponent } from '@osee/messaging/nodes/search/testing';
import { connectionMock, dialogRef } from '@osee/messaging/shared/testing';
import { DefaultAddNodeDialog } from '../../dialogs/add-node-dialog/add-node-dialog.default';
import { AddNodeDialogComponent } from './add-node-dialog.component';

describe('AddNodeDialogComponent', () => {
	let component: AddNodeDialogComponent;
	let fixture: ComponentFixture<AddNodeDialogComponent>;
	const dialogData = new DefaultAddNodeDialog(connectionMock);

	beforeEach(async () => {
		await TestBed.overrideComponent(AddNodeDialogComponent, {
			set: {
				imports: [
					AsyncPipe,
					FormsModule,
					MatDialogActions,
					MatDialogClose,
					MatDialogContent,
					MatDialogTitle,
					MatStepper,
					MatStep,
					MatStepperPrevious,
					MatStepperNext,
					MatButton,
					MockNodeSearchComponent,
					MockNewNodeFormComponent,
					NodeReviewComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [AddNodeDialogComponent],
				providers: [
					provideNoopAnimations(),
					{ provide: MatDialogRef, useValue: dialogRef },
					{ provide: MAT_DIALOG_DATA, useValue: dialogData },
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
