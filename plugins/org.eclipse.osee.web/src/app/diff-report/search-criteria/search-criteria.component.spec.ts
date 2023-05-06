/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { SearchCriteriaComponent } from './search-criteria.component';

describe('SearchCriteriaComponent', () => {
	let component: SearchCriteriaComponent;
	let fixture: ComponentFixture<SearchCriteriaComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				SearchCriteriaComponent,
				NoopAnimationsModule,
				HttpClientModule,
			],
		}).compileComponents();

		fixture = TestBed.createComponent(SearchCriteriaComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
