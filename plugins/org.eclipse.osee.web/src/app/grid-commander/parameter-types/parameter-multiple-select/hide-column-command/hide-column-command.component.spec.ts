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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { HideColumnCommandComponent } from './hide-column-command.component';

describe('HideColumnCommandComponent', () => {
	let component: HideColumnCommandComponent;
	let fixture: ComponentFixture<HideColumnCommandComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
			declarations: [HideColumnCommandComponent],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(HideColumnCommandComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('unhideCol will remove element from the array when test array contains the parameter passed in', (done: DoneFn) => {
		component.hideColumnsControl.next(['test1', 'test2', 'test3']);
		const testEl = 'test2';

		component.unhideCol(testEl);
		expect(component.hideColumnsControl.value).toEqual(['test1', 'test3']);
		done();
	});
});
