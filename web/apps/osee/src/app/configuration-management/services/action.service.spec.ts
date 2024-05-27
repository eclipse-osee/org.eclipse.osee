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
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from '@osee/environments';

import { NamedId } from '@osee/shared/types';
import {
	transitionAction,
	PRIORITIES,
	targetedVersionSentinel,
} from '@osee/shared/types/configuration-management';
import { ActionService } from './action.service';
import {
	MockUserResponse,
	testARB,
	testWorkFlow,
	testBranchActions,
	testDataTransitionResponse,
	testDataVersion,
	MockXResultData,
} from '@osee/shared/testing';
import { CreateNewActionInterface } from '@osee/configuration-management/types';
import { testnewActionResponse } from '@osee/configuration-management/testing';

const testNewActionData: CreateNewActionInterface = {
	title: 'title',
	description: 'string',
	aiIds: [],
	asUserId: '0',
	createdByUserId: '0',
	versionId: targetedVersionSentinel,
	priority: PRIORITIES.LowestPriority,
	changeType: { id: '-1', name: '', description: '' },
	originator: 'Joe Smith',
	assignees: '123,456',
	points: '1',
	unplanned: false,
	workPackage: '12345',
	featureGroup: '4',
	sprint: '10',
	attrValues: { '999': 'value' },
	parentAction: '',
};
const testUsers = [{ testDataUser: MockUserResponse }];
describe('ActionService', () => {
	let service: ActionService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		});
		service = TestBed.inject(ActionService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should get users', () => {
		service.users.subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/ats/user?active=Active'
		);
		expect(req.request.method).toEqual('GET');
		req.flush(testUsers);
		httpTestingController.verify();
	});

	it('should get ARB actionable item', () => {
		service.getActionableItems('ARB').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/ats/ai/all?orderByName=true&workType=ARB'
		);
		expect(req.request.method).toEqual('GET');
		req.flush(testARB);
		httpTestingController.verify();
	});

	it('should get workflow', () => {
		service.getWorkFlow(0).subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/ats/teamwf/' + 0
		);
		expect(req.request.method).toEqual('GET');
		req.flush(testWorkFlow);
		httpTestingController.verify();
	});

	it('should get action', () => {
		service.getAction('0').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/ats/action/' + 0
		);
		expect(req.request.method).toEqual('GET');
		req.flush(testBranchActions);
		httpTestingController.verify();
	});

	it('should get validateTransitionAction response', () => {
		const transitionData = new transitionAction(
			'Review',
			'Transition To Review',
			testBranchActions,
			MockUserResponse
		);
		service.validateTransitionAction(transitionData).subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/ats/action/transitionValidate'
		);
		expect(req.request.method).toEqual('POST');
		req.flush(testDataTransitionResponse);
		httpTestingController.verify();
	});

	it('should transitionAction', () => {
		const transitionData = new transitionAction(
			'Review',
			'Transition To Review',
			testBranchActions,
			MockUserResponse
		);
		service.transitionAction(transitionData).subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/ats/action/transition'
		);
		expect(req.request.method).toEqual('POST');
		req.flush(testDataTransitionResponse);
		httpTestingController.verify();
	});

	it('should get versions', () => {
		service.getVersions('0').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/ats/teamwf/' + 0 + '/version?sort=true'
		);
		expect(req.request.method).toEqual('GET');
		req.flush(testDataVersion);
		httpTestingController.verify();
	});

	it('should create branch', () => {
		service.createActionAndWorkingBranch(testNewActionData).subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/ats/action/branch'
		);
		expect(req.request.method).toEqual('POST');
		req.flush(testnewActionResponse);
		httpTestingController.verify();
	});

	it('should commit branch', () => {
		service.commitBranch('0', '0').subscribe();
		const req = httpTestingController.expectOne(
			apiURL +
				'/ats/action/branch/commit?teamWfId=' +
				0 +
				'&branchId=' +
				0
		);
		expect(req.request.method).toEqual('PUT');
		req.flush(MockXResultData);
		httpTestingController.verify();
	});

	it('should approve branch', () => {
		service.approveBranch('0').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/ats/action/' + 0 + '/approval'
		);
		expect(req.request.method).toEqual('POST');
		req.flush(MockXResultData);
		httpTestingController.verify();
	});

	it('should get team leads', () => {
		const testData: NamedId[] = [
			{
				id: '0',
				name: 'name',
			},
		];
		service.getTeamLeads('0').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/ats/config/teamdef/' + 0 + '/leads'
		);
		expect(req.request.method).toEqual('GET');
		req.flush(testData);
		httpTestingController.verify();
	});

	it('should get branch approval', () => {
		service.getBranchApproved('0').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/ats/action/' + 0 + '/approval'
		);
		expect(req.request.method).toEqual('GET');
		req.flush(MockXResultData);
		httpTestingController.verify();
	});
});
