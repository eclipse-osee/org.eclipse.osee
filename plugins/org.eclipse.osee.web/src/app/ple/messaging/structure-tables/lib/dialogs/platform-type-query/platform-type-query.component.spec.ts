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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectHarness } from '@angular/material/select/testing';
import { MatSliderHarness } from '@angular/material/slider/testing';
import { MatSliderModule } from '@angular/material/slider';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { PlatformTypeQueryComponent } from './platform-type-query.component';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import {
	ApplicabilityListService,
	EnumerationSetService,
	EnumsService,
	MimPreferencesService,
	TypesService,
} from '@osee/messaging/shared';
import { UserDataAccountService, userDataAccountServiceMock } from '@osee/auth';
import { TransactionBuilderService } from '@osee/shared/transactions';
import { transactionBuilderMock } from '@osee/shared/transactions/testing';
import {
	MimPreferencesServiceMock,
	typesServiceMock,
	enumsServiceMock,
	enumerationSetServiceMock,
	applicabilityListServiceMock,
	platformTypesMock,
} from '@osee/messaging/shared/testing';

describe('PlatformTypeQueryComponent', () => {
	let component: PlatformTypeQueryComponent;
	let fixture: ComponentFixture<PlatformTypeQueryComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatSelectModule,
				MatFormFieldModule,
				FormsModule,
				MatDividerModule,
				MatButtonModule,
				MatIconModule,
				MatSliderModule,
				MatDividerModule,
				MatInputModule,
				MatAutocompleteModule,
				NoopAnimationsModule,
			],
			providers: [
				{
					provide: TransactionBuilderService,
					useValue: transactionBuilderMock,
				},
				{
					provide: MimPreferencesService,
					useValue: MimPreferencesServiceMock,
				},
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
				{ provide: TypesService, useValue: typesServiceMock },
				{ provide: EnumsService, useValue: enumsServiceMock },
				{
					provide: EnumerationSetService,
					useValue: enumerationSetServiceMock,
				},
				{
					provide: ApplicabilityListService,
					useValue: applicabilityListServiceMock,
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(PlatformTypeQueryComponent);
		component = fixture.componentInstance;
		loader = TestbedHarnessEnvironment.loader(fixture);
		component.platformTypes = platformTypesMock;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
	async function testSelect(
		name: string,
		value: string,
		length: number,
		equalityCheck?: '>' | '<' | '='
	) {
		const select = await loader.getHarness(
			MatSelectHarness.with({ selector: `.${name}-select` })
		);
		await select.open();
		expect(await select.isOpen()).toBeTruthy();
		if (equalityCheck === '>' || equalityCheck === undefined) {
			expect(await (await select.getOptions()).length).toBeGreaterThan(
				length
			);
		} else if (equalityCheck === '<') {
			expect(await (await select.getOptions()).length).toBeLessThan(
				length
			);
		} else {
			expect(await (await select.getOptions()).length).toEqual(length);
		}
		await select.clickOptions({ text: value });
	}

	it('should create a query', async () => {
		await testSelect('unit', 'Feet^2', 3);
		await testSelect('logical-type', 'boolean', 0);
		await testSelect('min-val', '4', 2, '=');
		await testSelect('max-val', '8', 1, '=');
		await testSelect('msb-val', '6', 1, '=');
		await testSelect('default-val', 'false', 1, '=');
		const input = await loader.getHarness(MatInputHarness);
		await input.setValue('8');
		const slider = await loader.getHarness(MatSliderHarness);
		component.name = 'abcd'; // no enumerations are in the mock currently
		const queryButton = await loader.getHarness(
			MatButtonHarness.with({ selector: '.query-button' })
		);
		await queryButton.click();
	});
});
