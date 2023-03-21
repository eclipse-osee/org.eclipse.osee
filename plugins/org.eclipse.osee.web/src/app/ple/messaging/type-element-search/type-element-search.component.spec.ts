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
import { ActivatedRoute, Params } from '@angular/router';
import { Subject } from 'rxjs';
import { RouterStateService } from './lib/services/router-state.service';
import { MockElementTableComponent } from './lib/testing/element-table.component.mock';
import { MockElementTableSearchComponent } from './lib/testing/element-table-search.component.mock';

import { TypeElementSearchComponent } from './type-element-search.component';
import {
	ActionDropdownStub,
	BranchPickerStub,
} from '@osee/shared/components/testing';
import { MessagingControlsMockComponent } from '@osee/messaging/shared/testing';

describe('TypeElementSearchComponent', () => {
	let component: TypeElementSearchComponent;
	let fixture: ComponentFixture<TypeElementSearchComponent>;
	let params: Subject<Params>;
	let uiService: RouterStateService;

	beforeEach(async () => {
		params = new Subject<Params>();
		await TestBed.overrideComponent(TypeElementSearchComponent, {
			set: {
				imports: [
					MessagingControlsMockComponent,
					MockElementTableComponent,
					MockElementTableSearchComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [
					MessagingControlsMockComponent,
					TypeElementSearchComponent,
					MockElementTableComponent,
					MockElementTableSearchComponent,
				],
				providers: [
					{ provide: ActivatedRoute, useValue: { params: params } },
				],
				declarations: [],
			})
			.compileComponents();
	});

	beforeEach(() => {
		uiService = TestBed.inject(RouterStateService);
		fixture = TestBed.createComponent(TypeElementSearchComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should create with branchId 8 and type product line', () => {
		params.next({ branchId: '8', branchType: 'baseline' });
		expect(component).toBeTruthy();
		expect(uiService.id).toEqual('8');
		expect(uiService.type).toEqual('baseline');
	});

	it('should create with branchId 8 and type working', () => {
		params.next({ branchId: '8', branchType: 'working' });
		expect(component).toBeTruthy();
		expect(uiService.id).toEqual('8');
		expect(uiService.type).toEqual('working');
	});

	it('should create with branchId(when set to -1) "" and type working', () => {
		params.next({ branchId: '-1', branchType: 'working' });
		expect(component).toBeTruthy();
		expect(uiService.id).toEqual('0');
		expect(uiService.type).toEqual('working');
	});

	it('should create with branchId(when set to asdf) "0" and type working', () => {
		params.next({ branchId: 'asdf', branchType: 'working' });
		expect(component).toBeTruthy();
		expect(uiService.id).toEqual('0');
		expect(uiService.type).toEqual('working');
	});

	it('should create with branchId 8 and type "" (when set to asdf)', () => {
		params.next({ branchId: '8', branchType: 'asdf' });
		expect(component).toBeTruthy();
		expect(uiService.id).toEqual('8');
		expect(uiService.type).toEqual('');
	});
});
