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
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';
import {
	enumsServiceMock,
	warningDialogServiceMock,
	CurrentStateServiceMock,
	unitsMock,
} from '@osee/messaging/shared/testing';
import { EnumsService } from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { WarningDialogService } from 'src/app/ple/messaging/shared/services/warnings';

describe('EditElementFieldComponent', () => {
	let component: EditElementFieldComponent;
	let fixture: ComponentFixture<EditElementFieldComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [NoopAnimationsModule],
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
		component.platformTypeId = '20';
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	describe('Applicability Editing', () => {
		beforeEach(() => {
			component.header = 'applicability';
			component.value = { id: '1', name: 'Base' };
			fixture.detectChanges();
			loader = TestbedHarnessEnvironment.loader(fixture);
		});

		it('should update the applicability', async () => {
			let spy = spyOn(component, 'updateImmediately').and.callThrough();
			let select = await loader.getHarness(MatSelectHarness);
			await select.open();
			if (await select.isOpen()) {
				await select.clickOptions({ text: 'Second' });
				component.focusChanged(null);
				expect(spy).toHaveBeenCalled();
			} else {
				expect(spy).not.toHaveBeenCalled();
			}
		});

		it('should update value', fakeAsync(() => {
			let spy = spyOn(component, 'updateElement').and.callThrough();
			component.focusChanged('mouse');
			component.focusChanged('mouse');
			component.focusChanged(null);
			component.updateElement('description', 'v2');
			tick(500);
			expect(spy).toHaveBeenCalled();
		}));
	});

	describe('Platform Type Editing', () => {
		beforeEach(() => {
			component.header = 'platformTypeName2';
			component.value = 'First';
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
			expect(val).toBe('First2');
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
		});
		it('should update the units', fakeAsync(async () => {
			component.focusChanged('mouse');
			component.focusChanged('mouse');
			component.focusChanged(null);
			let select = await loader.getHarness(
				MatSelectHarness.with({ selector: '.unit-selector' })
			);
			await select.focus();
			await select.open();
			await select.isOpen();
			await select.clickOptions({ text: unitsMock[5] });
			tick(500);
			expect(await select.getValueText()).toBe(unitsMock[5]);
		}));
	});
});
