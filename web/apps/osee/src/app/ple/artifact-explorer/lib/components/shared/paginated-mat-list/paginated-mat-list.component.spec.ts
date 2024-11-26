/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { PaginatedMatListComponent } from './paginated-mat-list.component';
import { Component } from '@angular/core';

@Component({
	selector: 'osee-test-wrapper',
	template: `<osee-paginated-mat-list
		[allItems]="['one', 'two', 'three']"
		[currentPageItems]="['three']"
		[count]="20"
		[pageSize]="1">
		<ng-template let-item>
			<button>Test</button>
		</ng-template>
	</osee-paginated-mat-list>`,
	standalone: false,
})
class WrapperComponent {}

describe('PaginatedMatListComponent', () => {
	let component: PaginatedMatListComponent<string>;
	let fixture: ComponentFixture<WrapperComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [WrapperComponent],
			imports: [PaginatedMatListComponent],
		}).compileComponents();

		fixture = TestBed.createComponent(WrapperComponent);
		component = fixture.debugElement.children[0].componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
		expect(component.allItems()).toEqual(['one', 'two', 'three']);
	});
});
