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
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogModule,
	MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatStepperModule } from '@angular/material/stepper';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';
import { AddSubMessageDialog } from '../../types/AddSubMessageDialog';

import { subMessagesMock } from '@osee/messaging/shared/testing';
import { AddSubMessageDialogComponent } from './add-sub-message-dialog.component';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('AddSubMessageDialogComponent', () => {
	let component: AddSubMessageDialogComponent;
	let fixture: ComponentFixture<AddSubMessageDialogComponent>;
	const dialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
	const dialogData: AddSubMessageDialog = {
		id: '123456',
		name: 'message',
		subMessage: subMessagesMock[0],
	};

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				NoopAnimationsModule,
				MatDialogModule,
				MatStepperModule,
				FormsModule,
				MatFormFieldModule,
				MatInputModule,
				MatButtonModule,
				MatSelectModule,
				MockMatOptionLoadingComponent,
				AddSubMessageDialogComponent,
			],
			providers: [
				{ provide: MatDialogRef, useValue: dialogRef },
				{ provide: MAT_DIALOG_DATA, useValue: dialogData },
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(AddSubMessageDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should movetoStep', () => {
		const stepper = jasmine.createSpyObj(
			'stepper',
			{},
			{ selectedIndex: 0 }
		);
		spyOn(stepper, 'selectedIndex').and.callThrough();
		component.moveToStep(3, stepper);
		expect(stepper.selectedIndex).toEqual(0);
	});

	it('should movetoStep 3', () => {
		const stepper = jasmine.createSpyObj(
			'stepper',
			{},
			{ selectedIndex: 0 }
		);
		spyOn(stepper, 'selectedIndex').and.callThrough();
		const spy = spyOn(component, 'moveToStep').and.stub();
		component.moveToReview(stepper);
		expect(spy).toHaveBeenCalled();
		expect(spy).toHaveBeenCalledWith(3, stepper);
	});
});
