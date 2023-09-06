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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import {
	ComponentFixture,
	fakeAsync,
	TestBed,
	tick,
} from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatAutocompleteHarness } from '@angular/material/autocomplete/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectHarness } from '@angular/material/select/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { EditElementFieldComponent } from './edit-element-field.component';
import { A11yModule } from '@angular/cdk/a11y';
import { NgIf, NgFor, AsyncPipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import {
	MockApplicabilitySelectorComponent,
	MockMatOptionLoadingComponent,
} from '@osee/shared/components/testing';
import {
	enumsServiceMock,
	warningDialogServiceMock,
	CurrentStateServiceMock,
	unitsMock,
	platformTypesMock,
} from '@osee/messaging/shared/testing';
import {
	CurrentStructureService,
	EnumsService,
	WarningDialogService,
} from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { MockUnitDropdownComponent } from '@osee/messaging/shared/dropdowns/testing';
import { of } from 'rxjs';

describe('EditElementFieldComponent', () => {
	let component: EditElementFieldComponent<any>;
	let fixture: ComponentFixture<EditElementFieldComponent<any>>; //@todo Luciano fix these types later when the types are smarter
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [NoopAnimationsModule],
			providers: [
				{
					provide: STRUCTURE_SERVICE_TOKEN,
					useValue: CurrentStateServiceMock,
				},
			],
		})
			.overrideComponent(EditElementFieldComponent, {
				set: {
					imports: [
						MatFormFieldModule,
						FormsModule,
						MatSelectModule,
						MatOptionModule,
						MatInputModule,
						MatAutocompleteModule,
						MockMatOptionLoadingComponent,
						MatButtonModule,
						MatIconModule,
						AsyncPipe,
						A11yModule,
						RouterLink,
						NgIf,
						NgFor,
						MockApplicabilitySelectorComponent,
						MockUnitDropdownComponent,
					],
					providers: [
						{ provide: EnumsService, useValue: enumsServiceMock },
						{ provide: ActivatedRoute, useValue: {} },
						{
							provide: WarningDialogService,
							useValue: warningDialogServiceMock,
						},
						{
							provide: STRUCTURE_SERVICE_TOKEN,
							useValue: CurrentStateServiceMock,
						},
					],
				},
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(EditElementFieldComponent);
		component = fixture.componentInstance;
		component.structureId = '10';
		component.elementId = '15';
		component.platformType = platformTypesMock[0];
		//@todo luciano backport gamma id reactivity
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	describe('Platform Type Editing', () => {
		beforeEach(() => {
			component.header = 'platformType';
			component.value = platformTypesMock[0];
			fixture.detectChanges();
			loader = TestbedHarnessEnvironment.loader(fixture);
		});

		it('should update the platform type', fakeAsync(async () => {
			component.focusChanged('mouse');
			component.focusChanged('mouse');
			component.focusChanged(null);
			let select = await loader.getHarness(MatAutocompleteHarness);
			await select.focus();
			await select.isOpen();
			await select.enterText('2');
			const val = await select.getValue();
			tick(500);
			expect(val).toBe('2');
		}));

		it('should emit an event to parent component', () => {
			let mEvent = document.createEvent('MouseEvent');
			let spy = spyOn(component.contextMenu, 'emit');
			component.openMenu(mEvent, '');
			expect(spy).toHaveBeenCalledWith(mEvent);
		});
	});
	describe('Units editing', () => {
		beforeEach(() => {
			component.header = 'units';
			component.value = unitsMock[0];
			fixture.detectChanges();
			loader = TestbedHarnessEnvironment.loader(fixture);
		});
		it('should update the units', fakeAsync(async () => {
			let serviceSpy = spyOn(
				TestBed.inject(STRUCTURE_SERVICE_TOKEN),
				'updatePlatformTypeValue'
			).and.callFake(() => of());
			component.updateUnits('');
			tick(500);
			component.updateUnits('radians');
			tick(500);
			expect(serviceSpy).toHaveBeenCalledWith({
				id: '1',
				interfacePlatformTypeUnits: 'radians',
			});
		}));
	});
});
