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
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CiDashboardControlsComponent } from './ci-dashboard-controls.component';
import { BranchPickerStub } from '@osee/shared/components/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { CiDashboardUiService } from '../../services/ci-dashboard-ui.service';
import { MockOverflowMenuComponent } from './overflow-menu/overflow-menu.component.mock';

@Component({
	imports: [CiDashboardControlsComponent],
	template: `<osee-ci-dashboard-controls>
		<span data-testid="projected-filter">Page filter</span>
		<button
			ciDashboardAction
			data-testid="projected-action">
			Page action
		</button>
	</osee-ci-dashboard-controls>`,
})
class TestHostComponent {}

describe('CiDashboardControlsComponent', () => {
	let component: CiDashboardControlsComponent;
	let fixture: ComponentFixture<CiDashboardControlsComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(CiDashboardControlsComponent, {
			set: {
				imports: [BranchPickerStub, MockOverflowMenuComponent],
			},
		}).configureTestingModule({
			imports: [
				CiDashboardControlsComponent,
				BranchPickerStub,
				RouterTestingModule,
				TestHostComponent,
			],
		});
		fixture = TestBed.createComponent(CiDashboardControlsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should name the Branch and CI Set groups without visible headings', () => {
		const region = fixture.nativeElement.querySelector(
			'[aria-label="Zenith dashboard controls"]'
		) as HTMLElement;
		expect(region.tagName).toBe('SECTION');
		expect(region.classList).toContain('tw-flex-wrap');
		for (const name of ['Branch', 'CI Set']) {
			const group = region.querySelector(
				`[aria-label="${name}"]`
			) as HTMLElement;
			expect(group.getAttribute('role')).toBe('group');
		}
	});

	it('should explain the empty CI Set section when no branch is selected', () => {
		const setGroup = fixture.nativeElement.querySelector(
			'[aria-label="CI Set"]'
		) as HTMLElement;
		expect(setGroup.textContent).toContain(
			'Select a branch to choose a CI Set.'
		);
		expect(setGroup.querySelector('osee-set-dropdown')).toBeNull();
	});

	it('should hide the CI Set section when showSetSection is false', () => {
		fixture.componentRef.setInput('showSetSection', false);
		fixture.detectChanges();
		expect(
			fixture.nativeElement.querySelector('[aria-label="CI Set"]')
		).toBeNull();
		expect(
			fixture.nativeElement.querySelector('[aria-label="Branch"]')
		).toBeTruthy();
	});

	it('should describe each section for assistive tech', () => {
		for (const [name, description] of [
			['Branch', 'Lists only branches tagged with the CI category.'],
			[
				'CI Set',
				'CI Sets belong to a branch. The list shows the Sets of the selected branch.',
			],
		]) {
			const group = fixture.nativeElement.querySelector(
				`[aria-label="${name}"]`
			) as HTMLElement;
			const describedBy = fixture.nativeElement.querySelector(
				`#${group.getAttribute('aria-describedby')}`
			) as HTMLElement;
			expect(describedBy.textContent?.replace(/\s+/g, ' ')).toContain(
				description
			);
		}
	});

	it('should sync the routed set id without rendering the Set dropdown', async () => {
		const router = TestBed.inject(Router);
		const ui = TestBed.inject(CiDashboardUiService);

		await router.navigateByUrl('/?set=123');
		TestBed.tick();

		expect(
			fixture.nativeElement.querySelector('osee-set-dropdown')
		).toBeNull();
		expect(ui.ciSetId.value).toBe('123');
	});

	it('should project page filters and actions into separate groups', () => {
		const hostFixture = TestBed.createComponent(TestHostComponent);
		hostFixture.detectChanges();
		const projectedFilter = hostFixture.nativeElement.querySelector(
			'[data-testid="projected-filter"]'
		) as HTMLElement;
		const projectedAction = hostFixture.nativeElement.querySelector(
			'[data-testid="projected-action"]'
		) as HTMLElement;

		expect(projectedFilter.closest('[aria-label="CI Set"]')).toBeTruthy();
		expect(
			projectedAction.closest('[aria-label="Dashboard actions"]')
		).toBeTruthy();
	});

	it('should keep download actions distinct and trailing', () => {
		const actions = fixture.nativeElement.querySelector(
			'[aria-label="Dashboard actions"]'
		) as HTMLElement;
		expect(actions.getAttribute('role')).toBe('group');
		expect(actions.classList).toContain('tw-ml-auto');
		expect(actions.querySelector('osee-overflow-menu')).toBeTruthy();
	});
});
