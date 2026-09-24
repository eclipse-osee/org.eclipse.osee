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
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { Observable, of } from 'rxjs';

import { CreateWorkingBranchFromWorkflowButtonComponent } from './create-working-branch-from-workflow-button';
import { ActionService } from '@osee/configuration-management/services';
import { actionServiceMock } from '@osee/configuration-management/testing';
import { actionBranchData } from '@osee/configuration-management/types';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { BranchRoutedUIService, UiService } from '@osee/shared/services';
import { MutationService } from '@osee/shared/services/network';
import {
	branchRoutedUiServiceMock,
	teamWorkflowDetailsMock,
} from '@osee/shared/testing';
import { XResultData } from '@osee/shared/types';

/**
 * Pass-through MutationService: runs the http call and invokes describeChange (so we can
 * assert whether a local notification WOULD have been emitted) without the SSE readiness
 * gate. Records the last descriptor produced.
 */
class MutationServiceStub {
	lastDescriptor: unknown = 'unset';
	mutateAndNotify<T, D>(
		httpCall: Observable<T>,
		describeChange: (r: T) => D | null
	): Observable<T> {
		return new Observable<T>((subscriber) => {
			return httpCall.subscribe({
				next: (r) => {
					this.lastDescriptor = describeChange(r);
					subscriber.next(r);
				},
				error: (e) => subscriber.error(e),
				complete: () => subscriber.complete(),
			});
		});
	}
}

function xResult(overrides: Partial<XResultData>): XResultData {
	return {
		empty: false,
		errorCount: 0,
		errors: false,
		failed: false,
		ids: [],
		infoCount: 0,
		numErrors: 0,
		numErrorsViaSearch: 0,
		numWarnings: 0,
		numWarningsViaSearch: 0,
		results: [],
		success: true,
		tables: [],
		title: null,
		txId: '',
		warningCount: 0,
		...overrides,
	};
}

function branchResponse(results: XResultData): actionBranchData {
	return {
		branchName: 'WF - test',
		parent: '-1',
		applyAccess: false,
		validate: true,
		branchType: 0,
		associatedArt: { id: '9932353', name: '' },
		author: { id: '1', name: 'me' },
		creationComment: 'Creating working branch',
		results,
	};
}

describe('CreateWorkingBranchFromWorkflowButtonComponent', () => {
	let component: CreateWorkingBranchFromWorkflowButtonComponent;
	let fixture: ComponentFixture<CreateWorkingBranchFromWorkflowButtonComponent>;
	let mutation: MutationServiceStub;
	let uiService: UiService;
	let createResponse: actionBranchData;
	let openSpy: ReturnType<typeof vi.fn>;

	beforeEach(async () => {
		mutation = new MutationServiceStub();
		openSpy = vi.fn();
		vi.stubGlobal('open', openSpy);

		await TestBed.configureTestingModule({
			imports: [CreateWorkingBranchFromWorkflowButtonComponent],
			providers: [
				{
					provide: ActionService,
					useValue: {
						...actionServiceMock,
						createWorkingBranchForAction: () => of(createResponse),
					},
				},
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
				{
					provide: BranchRoutedUIService,
					useValue: branchRoutedUiServiceMock,
				},
				{ provide: MutationService, useValue: mutation },
				{
					provide: Router,
					useValue: {
						// Minimal stand-in: createUrlTree passes the args through, serializeUrl
						// renders them into a query string so the open(url) assertions still work.
						createUrlTree: (
							commands: unknown[],
							extras: { queryParams: Record<string, string> }
						) => ({ commands, queryParams: extras.queryParams }),
						serializeUrl: (tree: {
							commands: unknown[];
							queryParams: Record<string, string>;
						}) => {
							const path = (tree.commands as string[]).join('/');
							const query = new URLSearchParams(
								tree.queryParams
							).toString();
							return `${path}?${query}`;
						},
					},
				},
				{
					// Stand-in for Location: mimics prepareExternalUrl by prepending a
					// non-root base href, so tests verify the base href is applied to the
					// window.open URL (the production "/osee/" case that plain serializeUrl
					// missed).
					provide: Location,
					useValue: {
						prepareExternalUrl: (url: string) =>
							`/osee/${url.replace(/^\//, '')}`,
					},
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(
			CreateWorkingBranchFromWorkflowButtonComponent
		);
		component = fixture.componentInstance;
		uiService = TestBed.inject(UiService);
		fixture.componentRef.setInput('teamWorkflow', teamWorkflowDetailsMock);
		fixture.detectChanges();
	});

	afterEach(() => {
		vi.unstubAllGlobals();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('opens the artifact explorer and emits a branch-created notification on success', () => {
		createResponse = branchResponse(
			xResult({ success: true, ids: ['200'] })
		);

		component.createWorkingBranch();

		expect(openSpy).toHaveBeenCalledTimes(1);
		const openedUrl = openSpy.mock.calls[0][0] as string;
		expect(openedUrl).toContain('branchId=200');
		// The URL must carry the app's base href (production serves under "/osee/"); a bare
		// serializeUrl path would 404 there. It must also open in a new tab with noopener.
		expect(openedUrl).toContain('/osee/');
		expect(openedUrl).toContain('ple/artifact/explorer');
		expect(openSpy.mock.calls[0][1]).toBe('_blank');
		expect(openSpy.mock.calls[0][2]).toBe('noopener');
		expect(mutation.lastDescriptor).toMatchObject({
			type: 'branch',
			branchId: '200',
			changeType: 'created',
		});
	});

	it('surfaces the server failure reason and does NOT open the explorer on a 200-with-failure', () => {
		createResponse = branchResponse(
			xResult({
				success: false,
				failed: true,
				errors: true,
				ids: [],
				results: ['Error: Invalid Parent Branch -1'],
			})
		);
		const errorSpy = vi.spyOn(uiService, 'ErrorText', 'set');

		component.createWorkingBranch();

		expect(openSpy).not.toHaveBeenCalled();
		expect(errorSpy).toHaveBeenCalledWith(
			'Error: Invalid Parent Branch -1'
		);
		// No local branch-created notification for a failed create.
		expect(mutation.lastDescriptor).toBeNull();
	});

	it('does not open the explorer when the server returns a sentinel id', () => {
		// 200 with success:true but a sentinel id must not open COMMON.
		createResponse = branchResponse(
			xResult({ success: true, ids: ['-1'] })
		);
		const errorSpy = vi.spyOn(uiService, 'ErrorText', 'set');

		component.createWorkingBranch();

		expect(openSpy).not.toHaveBeenCalled();
		expect(errorSpy).toHaveBeenCalled();
		expect(mutation.lastDescriptor).toBeNull();
	});
});
