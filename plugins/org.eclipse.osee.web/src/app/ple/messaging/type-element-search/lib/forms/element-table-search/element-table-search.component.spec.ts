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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatInputHarness } from '@angular/material/input/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TestScheduler } from 'rxjs/testing';
import { SearchService } from '../../services/search.service';

import { ElementTableSearchComponent } from './element-table-search.component';

describe('ElementTableSearchComponent', () => {
	let component: ElementTableSearchComponent;
	let fixture: ComponentFixture<ElementTableSearchComponent>;
	let loader: HarnessLoader;
	let service: SearchService;
	let scheduler: TestScheduler;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatInputModule,
				MatFormFieldModule,
				FormsModule,
				NoopAnimationsModule,
				ElementTableSearchComponent,
			],
			teardown: { destroyAfterEach: false },
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ElementTableSearchComponent);
		loader = TestbedHarnessEnvironment.loader(fixture);
		service = TestBed.inject(SearchService);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});
	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should update search terms to Hello World', async () => {
		await (
			await loader.getHarness(MatInputHarness)
		).setValue('Hello World');
		scheduler.run(() => {
			let values = { a: 'Hello World' };
			let marble = 'a';
			scheduler.expectObservable(service.searchTerm).toBe(marble, values);
		});
	});
});
