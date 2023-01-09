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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatInputHarness } from '@angular/material/input/testing';

import { TypeGridComponent } from './type-grid.component';
import { LayoutModule } from '@angular/cdk/layout';
import { PlatformType } from '../../../../shared/types/platformType';
import { CurrentTypesService } from '../../services/current-types.service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { PlMessagingTypesUIService } from '../../services/pl-messaging-types-ui.service';
import { settingsDialogData } from '../../../../shared/types/settingsdialog';
import { MatIconModule } from '@angular/material/icon';
import { MockPlatformTypeCardComponent } from '../../../../shared/testing/platform-type-card.component.mock';
import { MatButtonModule } from '@angular/material/button';
import { NgIf, AsyncPipe, NgFor } from '@angular/common';
import { transactionMock } from '../../../../../../transactions/transaction.mock';

let loader: HarnessLoader;

describe('TypeGridComponent', () => {
	let component: TypeGridComponent;
	let fixture: ComponentFixture<TypeGridComponent>;
	let typeData: Observable<PlatformType[]> = of([
		{
			interfaceLogicalType: 'boolean',
			description: '',
			interfacePlatform2sComplement: false,
			interfacePlatformTypeAnalogAccuracy: 'Hello',
			interfacePlatformTypeBitsResolution: '1',
			interfacePlatformTypeBitSize: '8',
			interfacePlatformTypeCompRate: '1',
			interfaceDefaultValue: '1',
			interfacePlatformTypeEnumLiteral: 'Enum Lit.',
			interfacePlatformTypeMaxval: '1',
			interfacePlatformTypeMinval: '0',
			interfacePlatformTypeMsbValue: '1',
			interfacePlatformTypeUnits: 'N/A',
			interfacePlatformTypeValidRangeDescription: 'Description',
			name: 'boolean',
		},
		{
			interfaceLogicalType: 'integer',
			description: '',
			interfacePlatform2sComplement: false,
			interfacePlatformTypeAnalogAccuracy: 'Hello',
			interfacePlatformTypeBitsResolution: '1',
			interfacePlatformTypeBitSize: '8',
			interfacePlatformTypeCompRate: '1',
			interfaceDefaultValue: '1',
			interfacePlatformTypeEnumLiteral: 'Enum Lit.',
			interfacePlatformTypeMaxval: '1',
			interfacePlatformTypeMinval: '0',
			interfacePlatformTypeMsbValue: '1',
			interfacePlatformTypeUnits: 'N/A',
			interfacePlatformTypeValidRangeDescription: 'Description',
			name: 'integer',
		},
	]);

	beforeEach(async () => {
		await TestBed.overrideComponent(TypeGridComponent, {
			set: {
				imports: [
					NgIf,
					AsyncPipe,
					NgFor,
					FormsModule,
					MatButtonModule,
					MatIconModule,
					MatFormFieldModule,
					MatInputModule,
					MockPlatformTypeCardComponent,
				],
				providers: [
					{
						provide: CurrentTypesService,
						useValue: {
							typeData: typeData,
							inEditMode: of(true),
							updatePreferences(preferences: settingsDialogData) {
								return of(transactionMock);
							},
						},
					},
					{
						provide: PlMessagingTypesUIService,
						useValue: {
							filterString: '',
							columnCountNumber: 1,
							columnCount: new BehaviorSubject(1),
							singleLineAdjustment: of(0),
							BranchId: of('10'),
						},
					},
				],
			},
		})
			.configureTestingModule({
				imports: [
					NoopAnimationsModule,
					MatDialogModule,
					TypeGridComponent,
				],
				declarations: [],
				providers: [
					{
						provide: CurrentTypesService,
						useValue: {
							typeData: typeData,
							inEditMode: of(true),
							updatePreferences(preferences: settingsDialogData) {
								return of(transactionMock);
							},
						},
					},
					{
						provide: PlMessagingTypesUIService,
						useValue: {
							filterString: '',
							columnCountNumber: 1,
							columnCount: new BehaviorSubject(1),
							singleLineAdjustment: of(0),
							BranchId: of('10'),
						},
					},
				],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TypeGridComponent);
		component = fixture.componentInstance;
		loader = TestbedHarnessEnvironment.loader(fixture);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should filter', async () => {
		let form = await loader.getHarness(MatFormFieldHarness);
		let input = await form.getControl(MatInputHarness);
		await input?.setValue('boolean');
		expect(component.filterValue).toEqual('boolean');
		//@todo replace with a check to see that the filter value is set
		expect(component).toBeTruthy();
	});
});
