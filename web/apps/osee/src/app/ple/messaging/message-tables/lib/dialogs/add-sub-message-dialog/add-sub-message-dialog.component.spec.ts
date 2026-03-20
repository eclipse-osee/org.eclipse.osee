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
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
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
	const dialogRef = {
		close: vi.fn().mockName('MatDialogRef.close'),
	};
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
		const stepper: Partial<MatStepper> = {
			selectedIndex: 0,
		};
		component.moveToStep(3, stepper as MatStepper);
		expect(stepper.selectedIndex).toEqual(2);
	});

	it('should movetoStep 3', () => {
		const stepper: Partial<MatStepper> = {
			selectedIndex: 0,
		};
		vi.spyOn(stepper, 'selectedIndex', 'set').mockImplementation(() => {
			return;
		});
		const spy = vi.spyOn(component, 'moveToStep').mockImplementation(() => {
			return;
		});
		component.moveToReview(stepper as MatStepper);
		expect(spy).toHaveBeenCalled();
		expect(spy).toHaveBeenCalledWith(3, stepper);
	});
});
