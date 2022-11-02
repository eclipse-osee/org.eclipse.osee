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
import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelect, MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatSelectHarness } from '@angular/material/select/testing';
import { of } from 'rxjs';

import { MatOptionLoadingComponent } from './mat-option-loading.component';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';

@Component({
	selector: 'osee-outer-component',
	template:
		'<mat-form-field><mat-label>OuterComponent</mat-label><mat-select><osee-mat-option-loading [options]="observable$"><mat-option *ngFor="let option of (observable$ | async)"></mat-option></osee-mat-option-loading></mat-select></mat-form-field>',
})
class OuterComponent implements AfterViewInit {
	observable$ = of(['1', '2', '3']);
	@ViewChild('osee-mat-option-loading')
	public loading!: MatOptionLoadingComponent<string>;
	constructor() {}
	ngAfterViewInit(): void {
		const variable = 0;
	}
}

describe('MatOptionLoadingComponent', () => {
	let component: OuterComponent;
	let fixture: ComponentFixture<OuterComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatSelectModule,
				NoopAnimationsModule,
				MatProgressSpinnerModule,
			],
			declarations: [MatOptionLoadingComponent, OuterComponent],
		}).compileComponents();

		fixture = TestBed.createComponent(OuterComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', async () => {
		const select = await loader.getHarness(MatSelectHarness);
		await select.open();
		await select.isOpen();
		const options = await select.getOptions();
		expect(options.length).toBe(3);
	});
});
