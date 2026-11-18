/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { By } from '@angular/platform-browser';
import { MatTooltip } from '@angular/material/tooltip';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { BehaviorSubject, of } from 'rxjs';
import { CiDashboardUiService } from '../../../services/ci-dashboard-ui.service';
import { DashboardService } from '../../../services/dashboard.service';
import { OverflowMenuComponent } from './overflow-menu.component';

describe('OverflowMenuComponent', () => {
	let component: OverflowMenuComponent;
	let fixture: ComponentFixture<OverflowMenuComponent>;
	let branchId: BehaviorSubject<string>;
	let ciSetId: BehaviorSubject<`${number}`>;
	let exportBranchData: ReturnType<typeof vi.fn>;
	let exportSetData: ReturnType<typeof vi.fn>;

	beforeEach(() => {
		branchId = new BehaviorSubject('1');
		ciSetId = new BehaviorSubject<`${number}`>('2');
		exportBranchData = vi.fn(() => of(new Blob(['branch'])));
		exportSetData = vi.fn(() => of(new Blob(['set'])));

		TestBed.configureTestingModule({
			imports: [OverflowMenuComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: CiDashboardUiService,
					useValue: { branchId, ciSetId },
				},
				{
					provide: DashboardService,
					useValue: { exportBranchData, exportSetData },
				},
			],
		});
		fixture = TestBed.createComponent(OverflowMenuComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	afterEach(() => {
		vi.restoreAllMocks();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should expose a recognizable accessible dashboard CSV download action', () => {
		const trigger = fixture.debugElement.query(
			By.css('button[aria-label="Download Dashboard CSV"]')
		);
		const tooltip = trigger.injector.get(MatTooltip);

		expect(trigger.nativeElement.textContent.trim()).toBe('download');
		expect(tooltip.message).toBe('Download Dashboard CSV');
	});

	it('should preserve both CSV download choices', () => {
		const trigger = fixture.nativeElement.querySelector(
			'button[aria-label="Download Dashboard CSV"]'
		) as HTMLButtonElement;
		trigger.click();
		fixture.detectChanges();

		const menuText = Array.from(
			document.body.querySelectorAll('[role="menuitem"]')
		).map((item) => item.textContent?.trim());
		expect(menuText[0]).toContain('Download CSV for Branch');
		expect(menuText[1]).toContain('Download CSV for Set');
	});

	it('should disable downloads when the branch is invalid', () => {
		branchId.next('-1');
		fixture.detectChanges();

		const trigger = fixture.nativeElement.querySelector(
			'button[aria-label="Download Dashboard CSV"]'
		) as HTMLButtonElement;
		const explanation = trigger.parentElement;

		expect(trigger.disabled).toBe(true);
		expect(explanation?.tabIndex).toBe(0);
		expect(explanation?.getAttribute('aria-label')).toBe(
			'Select a valid branch to enable dashboard export.'
		);
	});

	it('should disable set export and expose its explanation without a set', () => {
		ciSetId.next('-1');
		fixture.detectChanges();
		const trigger = fixture.nativeElement.querySelector(
			'button[aria-label="Download Dashboard CSV"]'
		) as HTMLButtonElement;
		trigger.click();
		fixture.detectChanges();

		const menuItems = Array.from(
			document.body.querySelectorAll<HTMLButtonElement>(
				'[role="menuitem"]'
			)
		);
		expect(menuItems[0].disabled).toBe(false);
		expect(menuItems[1].disabled).toBe(true);
		expect(menuItems[1].parentElement?.tabIndex).toBe(0);
		expect(menuItems[1].parentElement?.getAttribute('aria-label')).toBe(
			'Select a valid CI Set to enable set export.'
		);
	});

	it('should revoke each CSV URL when replaced or destroyed', () => {
		const createObjectURL = vi
			.spyOn(URL, 'createObjectURL')
			.mockReturnValueOnce('blob:branch')
			.mockReturnValueOnce('blob:set');
		const revokeObjectURL = vi.spyOn(URL, 'revokeObjectURL');
		const click = vi
			.spyOn(HTMLAnchorElement.prototype, 'click')
			.mockImplementation(() => undefined);

		component.triggerBranchExport();
		fixture.detectChanges();

		expect(exportBranchData).toHaveBeenCalledOnce();
		expect(createObjectURL).toHaveBeenCalledOnce();
		expect(click).toHaveBeenCalledOnce();
		expect(revokeObjectURL).not.toHaveBeenCalled();

		component.triggerSetExport();
		fixture.detectChanges();

		expect(exportSetData).toHaveBeenCalledOnce();
		expect(createObjectURL).toHaveBeenCalledTimes(2);
		expect(click).toHaveBeenCalledTimes(2);
		expect(revokeObjectURL).toHaveBeenCalledWith('blob:branch');

		fixture.destroy();
		expect(revokeObjectURL).toHaveBeenCalledWith('blob:set');
	});

	it('should not create a URL for an empty export', () => {
		exportSetData.mockReturnValue(of(new Blob([])));
		const createObjectURL = vi.spyOn(URL, 'createObjectURL');

		component.triggerSetExport();
		fixture.detectChanges();

		expect(createObjectURL).not.toHaveBeenCalled();
	});
});
