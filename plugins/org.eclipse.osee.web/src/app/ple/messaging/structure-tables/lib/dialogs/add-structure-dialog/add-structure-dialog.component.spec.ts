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
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatStepperModule } from '@angular/material/stepper';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { AddStructureDialog } from './add-structure-dialog';

import { AddStructureDialogComponent } from './add-structure-dialog.component';

import type { structure } from '@osee/messaging/shared/types';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';
import { CurrentStateServiceMock } from '@osee/messaging/shared/testing';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';

describe('AddStructureDialogComponent', () => {
	let component: AddStructureDialogComponent;
	let fixture: ComponentFixture<AddStructureDialogComponent>;
	let dialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
	let dialogData: AddStructureDialog = {
		id: '123456',
		name: 'submessage',
		structure: {
			id: '',
			name: '',
			nameAbbrev: '',
			description: '',
			elements: [],
			interfaceMaxSimultaneity: '1',
			interfaceMinSimultaneity: '0',
			interfaceStructureCategory: '',
			interfaceTaskFileType: 0,
		},
	};
	let dummyStructure: structure = {
		id: '10',
		name: '',
		nameAbbrev: '',
		description: '',
		elements: [],
		interfaceMaxSimultaneity: '1',
		interfaceMinSimultaneity: '0',
		interfaceStructureCategory: '',
		interfaceTaskFileType: 0,
	};

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [NoopAnimationsModule],
		})
			.configureTestingModule({
				imports: [
					HttpClientTestingModule,
					MatStepperModule,
					MatDialogModule,
					MatButtonModule,
					FormsModule,
					MatFormFieldModule,
					MatSelectModule,
					MatInputModule,
					MatSlideToggleModule,
					MockMatOptionLoadingComponent,
				],
				declarations: [],
				providers: [
					{ provide: MAT_DIALOG_DATA, useValue: dialogData },
					{ provide: MatDialogRef, useValue: dialogRef },
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: CurrentStateServiceMock,
					},
				],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(AddStructureDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should movetoStep', () => {
		let stepper = jasmine.createSpyObj('stepper', {}, { selectedIndex: 0 });
		spyOn(stepper, 'selectedIndex').and.callThrough();
		component.moveToStep(3, stepper);
		expect(stepper.selectedIndex).toEqual(0);
	});

	it('should create new  by setting id to -1', () => {
		component.createNew();
		expect(component.data.structure.id).toEqual('-1');
	});

	it('should store the selected structure', () => {
		component.selectExistingStructure(dummyStructure);
		expect(component.selectedStructure).toEqual(dummyStructure);
	});

	it('should movetoStep 3', () => {
		let stepper = jasmine.createSpyObj('stepper', {}, { selectedIndex: 0 });
		spyOn(stepper, 'selectedIndex').and.callThrough();
		let spy = spyOn(component, 'moveToStep').and.stub();
		component.moveToReview(stepper);
		expect(spy).toHaveBeenCalled();
		expect(spy).toHaveBeenCalledWith(3, stepper);
	});
});
