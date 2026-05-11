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
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSelectHarness } from '@angular/material/select/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

import { PlatformTypeQueryComponent } from './platform-type-query.component';
import { platformTypesMock } from '@osee/messaging/shared/testing';
import { MockUnitDropdownComponent } from '@osee/messaging/shared/dropdowns/testing';
import { UnitDropdownComponent } from '@osee/messaging/units/dropdown';

// TODO: get this test working if we decide to re-activate this functionality
describe.skip('PlatformTypeQueryComponent', () => {
	let component: PlatformTypeQueryComponent;
	let fixture: ComponentFixture<PlatformTypeQueryComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.overrideComponent(PlatformTypeQueryComponent, {
			remove: {
				imports: [UnitDropdownComponent],
			},
			add: {
				imports: [MockUnitDropdownComponent],
			},
		})
			.configureTestingModule({
				providers: [provideNoopAnimations()],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(PlatformTypeQueryComponent);
		component = fixture.componentInstance;
		loader = TestbedHarnessEnvironment.loader(fixture);
		fixture.componentRef.setInput('platformTypes', platformTypesMock);
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
		await testSelect('logical-type', 'boolean', 0);
		await testSelect('min-val', '4', 2, '=');
		await testSelect('max-val', '8', 1, '=');
		await testSelect('msb-val', '6', 1, '=');
		await testSelect('default-val', 'false', 1, '=');
		const input = await loader.getHarness(MatInputHarness);
		await input.setValue('8');
		component.name.set('abcd'); // no enumerations are in the mock currently
		const queryButton = await loader.getHarness(
			MatButtonHarness.with({ selector: '.query-button' })
		);
		await queryButton.click();
	});
});
