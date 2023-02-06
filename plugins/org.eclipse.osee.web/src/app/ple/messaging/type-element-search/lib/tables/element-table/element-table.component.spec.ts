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
import { MatMenuModule } from '@angular/material/menu';
import { MatTableModule } from '@angular/material/table';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { CurrentElementSearchService } from '../../services/current-element-search.service';
import { MockElementTableSearchComponent } from '../../testing/element-table-search.component.mock';
import { elementSearch3 } from '../../testing/element-search.response.mock';

import { ElementTableComponent } from './element-table.component';

describe('ElementTableComponent', () => {
	let component: ElementTableComponent;
	let fixture: ComponentFixture<ElementTableComponent>;
	let serviceSpy: jasmine.SpyObj<CurrentElementSearchService>;

	beforeEach(async () => {
		serviceSpy = jasmine.createSpyObj(
			'CurrentElementSearchService',
			{},
			{ elements: of(elementSearch3) }
		);
		await TestBed.configureTestingModule({
			imports: [
				MatTableModule,
				MatMenuModule,
				NoopAnimationsModule,
				ElementTableComponent,
				MockElementTableSearchComponent,
			],
			providers: [
				{ provide: CurrentElementSearchService, useValue: serviceSpy },
			],
			declarations: [],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ElementTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
