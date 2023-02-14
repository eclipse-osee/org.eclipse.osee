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
import { ScrollingModule } from '@angular/cdk/scrolling';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { Component, AfterViewInit, ViewChild } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectHarness } from '@angular/material/select/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';

import { MatOptionLoadingComponent } from './mat-option-loading.component';

@Component({
	selector: 'osee-outer-component',
	template:
		'<mat-form-field><mat-label>OuterComponent</mat-label><mat-select><osee-mat-option-loading [data]="observable$"><ng-template let-option><mat-option>{{option}}</mat-option></ng-template></osee-mat-option-loading></mat-select></mat-form-field>',
	standalone: true,
	imports: [
		FormsModule,
		MatFormFieldModule,
		MatProgressSpinnerModule,
		MatSelectModule,
		MatAutocompleteModule,
		MatListModule,
		MatButtonModule,
		ScrollingModule,
		MatOptionLoadingComponent,
	],
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

describe('MatOptionLoadingComponent', async () => {
	let component: OuterComponent;
	let fixture: ComponentFixture<OuterComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				FormsModule,
				MatFormFieldModule,
				MatProgressSpinnerModule,
				MatSelectModule,
				MatAutocompleteModule,
				MatListModule,
				MatButtonModule,
				ScrollingModule,
				NoopAnimationsModule,
				MatOptionLoadingComponent,
				OuterComponent,
			],
		}).compileComponents();

		fixture = TestBed.createComponent(OuterComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
		await fixture.whenStable();
	});

	it('should create', async () => {
		expect(component).toBeTruthy();
		const select = await loader.getHarness(MatSelectHarness);
		await select.open();
		await select.isOpen();
		await fixture.whenStable();
		const options = await select.getOptions();
		expect(options.length).toBe(3);
	});
});
