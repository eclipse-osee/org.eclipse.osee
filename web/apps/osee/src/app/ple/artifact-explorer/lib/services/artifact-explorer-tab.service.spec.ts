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
import { TestBed } from '@angular/core/testing';

import { ArtifactExplorerTabService } from './artifact-explorer-tab.service';
import {
	BranchCommitEventService,
	CurrentBranchInfoService,
} from '@osee/shared/services';
import { of } from 'rxjs';
import { testBranchInfo } from '@osee/shared/testing';
import { tab } from '../types/artifact-explorer';

describe('ArtifactExplorerTabService', () => {
	let service: ArtifactExplorerTabService;
	let eventService: BranchCommitEventService;
	const tab1: tab = {
		tabId: '1',
		tabTitle: 'Test',
		branchId: '12345',
		branchName: 'Test branch',
		viewId: '-1',
		tabType: 'Artifact',
		artifact: {
			id: '1',
			name: '',
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
			editable: false,
			operationTypes: [],
			applicability: {
				id: '-1',
				name: 'Base',
			},
		},
	};
	const tab2: tab = {
		tabId: '2',
		tabTitle: 'Test',
		branchId: '6789',
		branchName: 'Test branch',
		viewId: '-1',
		tabType: 'Artifact',
		artifact: {
			id: '1',
			name: '',
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
			editable: false,
			operationTypes: [],
			applicability: {
				id: '-1',
				name: 'Base',
			},
		},
	};

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: CurrentBranchInfoService,
					useValue: { currentBranch: of(testBranchInfo) },
				},
			],
		});
		service = TestBed.inject(ArtifactExplorerTabService);
		eventService = TestBed.inject(BranchCommitEventService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('tab initialization', () => {
		expect(service.Tabs()).toEqual([]);
	});

	describe('Event Tests', () => {
		it('Empty tabs and no branch committed', () => {
			service.Tabs.set([]);
			expect(service.Tabs()).toEqual([]);
		});
		it('Empty tabs and branch committed', () => {
			service.Tabs.set([]);
			expect(service.Tabs()).toEqual([]);
			eventService.sendEvent('12345');
			expect(service.Tabs()).toEqual([]);
		});
		it('Existing tabs and branch committed that is not in tab list', () => {
			service.Tabs.set([tab1, tab2]);
			expect(service.Tabs()).toEqual([tab1, tab2]);
			eventService.sendEvent('234');
			expect(service.Tabs()).toEqual([tab1, tab2]);
		});
		it('Existing tabs and branch committed that is in tab list(1st)', () => {
			service.Tabs.set([tab1, tab2]);
			expect(service.Tabs()).toEqual([tab1, tab2]);
			eventService.sendEvent('12345');
			expect(service.Tabs()).toEqual([tab2]);
		});
		it('Existing tabs and branch committed that is in tab list(2nd)', () => {
			service.Tabs.set([tab1, tab2]);
			expect(service.Tabs()).toEqual([tab1, tab2]);
			eventService.sendEvent('6789');
			expect(service.Tabs()).toEqual([tab1]);
		});
		it('Existing tabs and no branch committed', () => {
			service.Tabs.set([tab1, tab2]);
			expect(service.Tabs()).toEqual([tab1, tab2]);
		});
	});
});
