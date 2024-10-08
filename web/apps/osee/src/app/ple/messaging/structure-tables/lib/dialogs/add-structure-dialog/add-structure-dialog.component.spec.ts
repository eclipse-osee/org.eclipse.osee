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
import {} from '@angular/common/http/testing';
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
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { AddStructureDialog } from './add-structure-dialog';

import { AddStructureDialogComponent } from './add-structure-dialog.component';

import type { structure } from '@osee/messaging/shared/types';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';
import { CurrentStateServiceMock } from '@osee/messaging/shared/testing';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { applicabilitySentinel } from '@osee/applicability/types';
import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';
import { MockApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown/testing';
import { StructureCategoryDropdownComponent } from '@osee/messaging/structure-category/structure-category-dropdown';
import { MockStructureCategoryDropdownComponent } from '@osee/messaging/structure-category/structure-category-dropdown/testing';

describe('AddStructureDialogComponent', () => {
	let component: AddStructureDialogComponent;
	let fixture: ComponentFixture<AddStructureDialogComponent>;
	const dialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
	const dialogData: AddStructureDialog = {
		id: '123456',
		name: 'submessage',
		structure: {
			id: '-1',
			gammaId: '-1',
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: '',
			},
			nameAbbrev: {
				id: '-1',
				typeId: '8355308043647703563',
				gammaId: '-1',
				value: '',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
			interfaceMaxSimultaneity: {
				id: '-1',
				typeId: '2455059983007225756',
				gammaId: '-1',
				value: '',
			},
			interfaceMinSimultaneity: {
				id: '-1',
				typeId: '2455059983007225755',
				gammaId: '-1',
				value: '',
			},
			interfaceTaskFileType: {
				id: '-1',
				typeId: '2455059983007225760',
				gammaId: '-1',
				value: 0,
			},
			interfaceStructureCategory: {
				id: '-1',
				typeId: '2455059983007225764',
				gammaId: '-1',
				value: '',
			},
			applicability: applicabilitySentinel,
			elements: [],
		},
	};
	const dummyStructure: structure = {
		id: '10',
		gammaId: '-1',
		name: {
			id: '-1',
			typeId: '1152921504606847088',
			gammaId: '-1',
			value: '',
		},
		nameAbbrev: {
			id: '-1',
			typeId: '8355308043647703563',
			gammaId: '-1',
			value: '',
		},
		description: {
			id: '-1',
			typeId: '1152921504606847090',
			gammaId: '-1',
			value: '',
		},
		interfaceMaxSimultaneity: {
			id: '-1',
			typeId: '2455059983007225756',
			gammaId: '-1',
			value: '',
		},
		interfaceMinSimultaneity: {
			id: '-1',
			typeId: '2455059983007225755',
			gammaId: '-1',
			value: '',
		},
		interfaceTaskFileType: {
			id: '-1',
			typeId: '2455059983007225760',
			gammaId: '-1',
			value: 0,
		},
		interfaceStructureCategory: {
			id: '-1',
			typeId: '2455059983007225764',
			gammaId: '-1',
			value: '',
		},
		applicability: applicabilitySentinel,
		elements: [],
	};

	beforeEach(async () => {
		await TestBed.overrideComponent(AddStructureDialogComponent, {
			add: {
				imports: [
					MockMatOptionLoadingComponent,
					MockApplicabilityDropdownComponent,
					MockStructureCategoryDropdownComponent,
				],
				providers: [
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: CurrentStateServiceMock,
					},
				],
			},
			remove: {
				imports: [
					MatOptionLoadingComponent,
					ApplicabilityDropdownComponent,
					StructureCategoryDropdownComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [
					MatStepperModule,
					MatDialogModule,
					MatButtonModule,
					FormsModule,
					MatFormFieldModule,
					MatSelectModule,
					MatInputModule,
					MatSlideToggleModule,
				],
				declarations: [],
				providers: [
					provideNoopAnimations(),
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
		const stepper = jasmine.createSpyObj(
			'stepper',
			{},
			{ selectedIndex: 0 }
		);
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
