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
import { Component, input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArtifactTabGroupComponent } from './artifact-tab-group.component';
import { CurrentBranchInfoService } from '@osee/shared/services';
import {
	EditorDirtyService,
	leaveState,
} from '@osee/shared/conflict-resolution';
import { testBranchInfo } from '@osee/shared/testing';
import { of } from 'rxjs';
import { ArtifactExplorerTabService } from '../../services/artifact-explorer-tab.service';
import { tab } from '../../types/artifact-explorer';
import { ArtifactEditorComponent } from '../editor/artifact-editor/artifact-editor.component';

/** Stub for the heavy per-tab editor so the tab strip renders without bootstrapping it. */
@Component({
	selector: 'osee-artifact-editor',
	template: '',
})
class ArtifactEditorStubComponent {
	tab = input<tab>();
}

/** Minimal artifact tab fixture with a controllable id. */
function makeTab(tabId: string, artifactId: `${number}`): tab {
	return {
		tabId,
		tabType: 'Artifact',
		tabTitle: `Tab ${tabId}`,
		branchId: '12345',
		branchName: 'Test branch',
		viewId: '-1',
		artifact: {
			id: artifactId,
			gammaId: '-1',
			name: `Art ${artifactId}`,
			typeId: '1',
			typeName: '',
			icon: {
				icon: '',
				color: 'primary',
				lightShade: '',
				darkShade: '',
				variant: '',
			},
			attributes: [],
			relations: [],
			editable: true,
			operationTypes: [],
			applicability: { id: '-1', name: 'Base' },
		},
	};
}

describe('ArtifactTabGroupComponent', () => {
	let component: ArtifactTabGroupComponent;
	let fixture: ComponentFixture<ArtifactTabGroupComponent>;
	let tabService: ArtifactExplorerTabService;
	let dirty: EditorDirtyService;

	beforeEach(() => {
		// Swap the heavy per-tab editor for a stub (same selector) so the tab strip
		// renders without bootstrapping the real editor's provider graph.
		TestBed.overrideComponent(ArtifactTabGroupComponent, {
			remove: { imports: [ArtifactEditorComponent] },
			add: { imports: [ArtifactEditorStubComponent] },
		});
		TestBed.configureTestingModule({
			imports: [ArtifactTabGroupComponent],
			providers: [
				{
					provide: CurrentBranchInfoService,
					useValue: { currentBranch: of(testBranchInfo) },
				},
			],
		});
		tabService = TestBed.inject(ArtifactExplorerTabService);
		dirty = TestBed.inject(EditorDirtyService);
		fixture = TestBed.createComponent(ArtifactTabGroupComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	/** `isClosing` is protected on the component; read it through the instance for assertions. */
	const isClosing = (tabId: string): boolean =>
		(
			component as unknown as { isClosing: (id: string) => boolean }
		).isClosing(tabId);

	describe('closing-state (spinner) while a save settles', () => {
		/** Forces closeTab down the `saving` branch and holds awaitSettled pending. */
		function stubSaving(resolveWith?: 'saved' | 'timeout') {
			vi.spyOn(dirty, 'leaveStateFor').mockReturnValue(
				'saving' as leaveState
			);
			let release: ((r: 'saved' | 'timeout') => void) | undefined;
			const pending = new Promise<'saved' | 'timeout'>((res) => {
				release = res;
			});
			vi.spyOn(dirty, 'awaitSettled').mockReturnValue(
				resolveWith !== undefined
					? Promise.resolve(resolveWith)
					: pending
			);
			return { release };
		}

		it('marks the tab closing and shows a spinner in place of the ×', async () => {
			const t = makeTab('tab-1', '100');
			tabService.Tabs.set([t]);
			fixture.detectChanges();

			stubSaving(); // awaitSettled never resolves -> stays in the closing window

			// Kick off the close (don't await — it's parked on awaitSettled).
			void component.closeTab(0);

			expect(isClosing('tab-1')).toBe(true);
			fixture.detectChanges();

			const spinner = fixture.nativeElement.querySelector(
				'mat-progress-spinner'
			);
			expect(spinner).toBeTruthy();
			// The × button is replaced by the spinner while closing.
			const closeBtn = fixture.nativeElement.querySelector(
				'button[aria-label="Close tab"]'
			);
			expect(closeBtn).toBeNull();
		});

		it('ignores a repeat closeTab while the tab is already closing', async () => {
			const t = makeTab('tab-1', '100');
			tabService.Tabs.set([t]);
			fixture.detectChanges();

			stubSaving();
			const removeSpy = vi.spyOn(tabService, 'removeTabByTabId');

			void component.closeTab(0);
			expect(isClosing('tab-1')).toBe(true);

			// A second click while closing must be a no-op (no extra prompt / removal).
			await component.closeTab(0);
			expect(removeSpy).not.toHaveBeenCalled();
		});

		it('closes the tab and clears closing-state once the save settles', async () => {
			const t = makeTab('tab-1', '100');
			tabService.Tabs.set([t]);
			fixture.detectChanges();

			stubSaving('saved'); // awaitSettled resolves immediately as saved
			const removeSpy = vi.spyOn(tabService, 'removeTabByTabId');

			await component.closeTab(0);

			expect(removeSpy).toHaveBeenCalledWith('tab-1');
			expect(isClosing('tab-1')).toBe(false);
		});
	});

	describe('clean / conflicted close (no settle wait)', () => {
		it('closes a clean tab immediately by tabId', async () => {
			const t = makeTab('tab-1', '100');
			tabService.Tabs.set([t]);
			vi.spyOn(dirty, 'leaveStateFor').mockReturnValue('clean');
			const removeSpy = vi.spyOn(tabService, 'removeTabByTabId');

			await component.closeTab(0);

			expect(removeSpy).toHaveBeenCalledWith('tab-1');
			expect(isClosing('tab-1')).toBe(false);
		});

		it('discarding a conflicted tab clears its dirty keys (no stale "saving" after close)', async () => {
			const t = makeTab('tab-1', '100');
			tabService.Tabs.set([t]);
			// Real dirty state: the artifact has an unsaved, conflicted edit. (A
			// conflicted field editor's ngOnDestroy skips markClean, so the close site
			// must clear these or they leak.)
			dirty.markDirty('100-1');
			dirty.setConflicted('100', true);
			expect(dirty.globalLeaveState()).toBe('conflicted');

			// User confirms the discard.
			vi.spyOn(window, 'confirm').mockReturnValue(true);

			await component.closeTab(0);

			// The decisive assertion: nothing left dirty/conflicted for this artifact,
			// so a later unload sees a clean editor (not a phantom "saving").
			expect(dirty.globalLeaveState()).toBe('clean');
			expect(dirty.hasDirtyEditorsForEntity('100')).toBe(false);
		});

		it('keeping a conflicted tab (cancel) leaves its state intact', async () => {
			const t = makeTab('tab-1', '100');
			tabService.Tabs.set([t]);
			dirty.markDirty('100-1');
			dirty.setConflicted('100', true);

			vi.spyOn(window, 'confirm').mockReturnValue(false);
			const removeSpy = vi.spyOn(tabService, 'removeTabByTabId');

			await component.closeTab(0);

			// Cancel: tab stays, state preserved for the user to resolve.
			expect(removeSpy).not.toHaveBeenCalled();
			expect(dirty.globalLeaveState()).toBe('conflicted');
		});
	});
});
