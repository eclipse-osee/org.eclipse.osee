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
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { PlatformTypeQuery } from '@osee/messaging/shared/query';
import {
	MimPreferencesServiceMock,
	QueryServiceMock,
	elementServiceMock,
	elementsMock,
	messageServiceMock,
	platformTypes1,
	structureRepeatingWithChanges,
	structureServiceRandomMock,
	structuresMock2,
	structuresMock3,
	structuresMockWithChangesMulti,
	structuresPreChanges,
	typesServiceMock,
	warningDialogServiceMock,
} from '@osee/messaging/shared/testing';
import type {
	structure,
	structureWithChanges,
} from '@osee/messaging/shared/types';
import { BranchInfoService } from '@osee/shared/services';
import { BranchInfoServiceMock, changeReportMock } from '@osee/shared/testing';
import { CurrentTransactionService } from '@osee/transactions/services';
import { currentTransactionServiceMock } from '@osee/transactions/services/testing';
import { transactionResultMock } from '@osee/transactions/testing';
import { of } from 'rxjs';
import { tap } from 'rxjs/operators';
import { TestScheduler } from 'rxjs/testing';
import { ElementService } from '../http/element.service';
import { MessagesService } from '../http/messages.service';
import { MimPreferencesService } from '../http/mim-preferences.service';
import { QueryService } from '../http/query.service';
import { StructuresService } from '../http/structures.service';
import { TypesService } from '../http/types.service';
import { CurrentStructureMultiService } from './current-structure-multi.service';
import { CurrentStructureSingleService } from './current-structure-single.service';
import { CurrentStructureService } from './current-structure.service';
import { StructuresUiService } from './structures-ui.service';
import { WarningDialogService } from './warning-dialog.service';

const servicesUnderTest: {
	service: typeof CurrentStructureService;
	name: string;
	noDiffValue: structure[];
	pushStructure: boolean;
	values: Record<string, Partial<structure | structureWithChanges>[]>;
	diffEmission: string;
}[] = [
	{
		service: CurrentStructureMultiService,
		name: 'Multiple Structures',
		noDiffValue: structuresMock3,
		pushStructure: false,
		values: {
			a: [
				structuresMockWithChangesMulti[0],
				structuresMockWithChangesMulti[1],
				structuresMockWithChangesMulti[2],
			],
			b: [
				structuresMockWithChangesMulti[0],
				structuresMockWithChangesMulti[1],
				structuresMockWithChangesMulti[2],
				structuresMockWithChangesMulti[3],
			],
			c: structuresMockWithChangesMulti,
		},
		diffEmission:
			'500ms (aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbbcccc)',
	},
	{
		service: CurrentStructureSingleService,
		name: 'Single Structure',
		noDiffValue: [structuresMock3[0]],
		pushStructure: true,
		values: {
			a: [structureRepeatingWithChanges],
		},
		diffEmission: '500ms (a)',
	},
];
servicesUnderTest.forEach((testCase) => {
	describe(`Structure Service - PLE Aware - ${testCase.name}`, () => {
		let service: CurrentStructureService;
		let ui: StructuresUiService;
		let scheduler: TestScheduler;

		beforeEach(() => {
			TestBed.configureTestingModule({
				imports: [],
				providers: [
					{ provide: ElementService, useValue: elementServiceMock },
					{
						provide: StructuresService,
						useValue: structureServiceRandomMock,
					},
					{
						provide: MessagesService,
						useValue: messageServiceMock,
					},
					{ provide: TypesService, useValue: typesServiceMock },
					{
						provide: MimPreferencesService,
						useValue: MimPreferencesServiceMock,
					},
					{ provide: StructuresUiService },
					{
						provide: BranchInfoService,
						useValue: BranchInfoServiceMock,
					},
					{ provide: QueryService, useValue: QueryServiceMock },
					{
						provide: WarningDialogService,
						useValue: warningDialogServiceMock,
					},
					{
						provide: CurrentTransactionService,
						useValue: currentTransactionServiceMock,
					},
					provideHttpClient(withInterceptorsFromDi()),
					provideHttpClientTesting(),
				],
			});
			service = TestBed.inject(testCase.service);
			ui = TestBed.inject(StructuresUiService);
			ui.DiffMode = false;
			ui.difference = [];
			if (testCase.pushStructure) {
				ui.singleStructureIdValue = '10';
			}
		});

		beforeEach(
			() =>
				(scheduler = new TestScheduler((actual, expected) => {
					expect(actual).toEqual(expected);
				}))
		);

		it('should be created', () => {
			expect(service).toBeTruthy();
		});

		//TODO: test doesn't work with signals...
		xit('should get filtered structures', () => {
			scheduler.run(() => {
				service.branchId = '10';
				service.structureFilter.set('0');
				service.messageId = '1';
				service.subMessageId = '2';
				service.connection = '3';
				const expectedObservable = { a: testCase.noDiffValue };
				const expectedMarble = '500ms a';
				scheduler
					.expectObservable(service.structures)
					.toBe(expectedMarble, expectedObservable);
			});
		});

		it('should change an element and get a transactionResultMock back', () => {
			scheduler.run(() => {
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(
						service.partialUpdateElement(
							structuresMock3[0].elements[0],
							structuresMock2[0].elements[0]
						)
					)
					.toBe(expectedMarble, expectedObservable);
			});
		});

		it('should create an element and get a transactionResultMock back', () => {
			scheduler.run(() => {
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(
						service.createNewElement(
							structuresMock3[0].elements[0],
							'10',
							'10'
						)
					)
					.toBe(expectedMarble, expectedObservable);
			});
		});

		it('should create a structure and get a transactionResultMock back', () => {
			scheduler.run(() => {
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(
						service.createStructure(structuresMock3[0])
					)
					.toBe(expectedMarble, expectedObservable);
			});
		});

		it('should change a structure and get a transactionResultMock back', () => {
			scheduler.run(() => {
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(
						service.partialUpdateStructure(
							structuresMock3[0],
							structuresMock2[0]
						)
					)
					.toBe(expectedMarble, expectedObservable);
			});
		});

		it('should relate an element', () => {
			scheduler.run(() => {
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(service.relateElement('10', '20'))
					.toBe(expectedMarble, expectedObservable);
			});
		});
		//TODO: test doesn't work with signals...
		xit('should relate a structure', () => {
			scheduler.run(() => {
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(service.relateStructure('10'))
					.toBe(expectedMarble, expectedObservable);
			});
		});

		it('should perform a mutation for deleting a submessage relation', () => {
			scheduler.run(({ expectObservable }) => {
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				expectObservable(
					service.removeStructureFromSubmessage('10', '20')
				).toBe(expectedMarble, expectedObservable);
			});
		});

		it('should perform a mutation for deleting a submessage relation', () => {
			scheduler.run(({ expectObservable }) => {
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				expectObservable(
					service.removeElementsFromStructure(
						[elementsMock[0]],
						structuresMock3[0]
					)
				).toBe(expectedMarble, expectedObservable);
			});
		});

		it('should perform a mutation for deleting a submessage relation', () => {
			scheduler.run(({ expectObservable }) => {
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				expectObservable(
					service.deleteElements([elementsMock[0]])
				).toBe(expectedMarble, expectedObservable);
			});
		});

		it('should perform a mutation for deleting a structure', () => {
			scheduler.run(({ expectObservable }) => {
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				expectObservable(
					service.deleteStructure(structuresMock3[0].id)
				).toBe(expectedMarble, expectedObservable);
			});
		});

		it('should update user preferences', () => {
			scheduler.run(() => {
				service.branchId = '10';
				const expectedObservable = {
					a: [
						transactionResultMock,
						transactionResultMock,
						transactionResultMock,
						transactionResultMock,
					],
				};
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(
						service.updatePreferences({
							branchId: '10',
							allowedHeaders1: ['name', 'description'],
							allowedHeaders2: ['name', 'description'],
							allHeaders1: [
								'name',
								'description',
								'applicability',
							],
							allHeaders2: [
								'name',
								'description',
								'applicability',
							],
							editable: true,
							headers1Label: '',
							headers2Label: '',
							headersTableActive: false,
							wordWrap: false,
						})
					)
					.toBe(expectedMarble, expectedObservable);
			});
		});

		it('should get types', () => {
			scheduler.run(() => {
				service.branchId = '0';
				const expectedObservable = { a: platformTypes1 };
				const expectedMarble = 'a';
				scheduler
					.expectObservable(service.types)
					.toBe(expectedMarble, expectedObservable);
			});
		});
		it('should get available structures', () => {
			scheduler.run(() => {
				const expectedObservable = {
					a: structuresMock3,
					b: [structuresMock3[0], structuresMock2[0]],
					c: [
						structuresMock3[0],
						structuresMock3[0],
						structuresMock3[0],
					],
					d: [
						structuresMock3[0],
						structuresMock3[0],
						structuresMock3[0],
						structuresMock3[0],
					],
					e: [
						structuresMock3[0],
						structuresMock3[0],
						structuresMock3[0],
						structuresMock3[0],
						structuresMock3[0],
					],
				};
				const expectedMarble = '(a)';
				scheduler
					.expectObservable(service.availableStructures)
					.toBe(expectedMarble, expectedObservable);
			});
		});

		it('should get available elements', () => {
			scheduler.run(() => {
				const expectedObservable = { a: elementsMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(service.availableElements)
					.toBe(expectedMarble, expectedObservable);
			});
		});

		it('should get and set branch type', () => {
			expect(service.BranchType).toEqual('');
			scheduler.run(({ expectObservable, cold }) => {
				const expectedObservable = {
					a: 'baseline',
					b: 'working',
					c: '',
				};
				const expectedMarble = 'c---a----b';
				const makeemissions = cold('----a----(b|)', {
					a: 'baseline' as const,
					b: 'working' as const,
				}).pipe(tap((t) => (service.BranchType = t)));
				expectObservable(makeemissions).toBe('----a----(b|)', {
					a: 'baseline',
					b: 'working',
				});
				expectObservable(service.branchType).toBe(
					expectedMarble,
					expectedObservable
				);
			});
		});

		it('should get a connection path', () => {
			scheduler.run(({ expectObservable }) => {
				service.branchId = '10';
				service.BranchType = 'working';
				expectObservable(service.connectionsRoute).toBe('a', {
					a: '/ple/messaging/connections/working/10',
				});
			});
		});

		it('done should complete', () => {
			scheduler.run(({ expectObservable, cold }) => {
				const expectedFilterValues = {
					a: true,
					b: undefined,
					c: false,
				};
				const expectedMarble = '-(a|)';
				const delayMarble = '-a';
				cold(delayMarble).subscribe(() => (service.toggleDone = true));
				expectObservable(service.done).toBe(
					expectedMarble,
					expectedFilterValues
				);
			});
		});

		it('should return an update', () => {
			scheduler.run(({ cold, expectObservable }) => {
				const expectedObservable = { a: true };
				const expectedMarble = '101ms a';
				const delayMarble = '-a';
				cold(delayMarble).subscribe(() => (service.update = true));
				expectObservable(service.updated).toBe(
					expectedMarble,
					expectedObservable
				);
			});
		});

		it('should perform a query', () => {
			scheduler.run(({ expectObservable }) => {
				const expectedObservable = { a: ['Hello'] };
				const expectedMarble = 'a';
				expectObservable(service.query(new PlatformTypeQuery())).toBe(
					expectedMarble,
					expectedObservable
				);
			});
		});
	});

	describe(`Structure Service - PLE Aware - ${testCase.name} - Diffs`, () => {
		let service: CurrentStructureService;
		let ui: StructuresUiService;
		let scheduler: TestScheduler;

		beforeEach(() => {
			TestBed.configureTestingModule({
				imports: [],
				providers: [
					{ provide: ElementService, useValue: elementServiceMock },
					{
						provide: StructuresService,
						useValue: {
							getFilteredStructures(
								_v1: string,
								_v2: string,
								_v3: string,
								_v4: string,
								_v5: string
							) {
								return of(structuresPreChanges);
							},
							getStructure(
								_branchId: string,
								_messageId: string,
								_subMessageId: string,
								_structureId: string,
								_connectionId: string
							) {
								return of(structuresPreChanges[0]);
							},
						},
					},
					{
						provide: MessagesService,
						useValue: messageServiceMock,
					},
					{ provide: TypesService, useValue: typesServiceMock },
					{
						provide: MimPreferencesService,
						useValue: MimPreferencesServiceMock,
					},
					{ provide: StructuresUiService },
					{
						provide: BranchInfoService,
						useValue: BranchInfoServiceMock,
					},
					CurrentStructureService,
					provideHttpClient(withInterceptorsFromDi()),
					provideHttpClientTesting(),
				],
			});
			service = TestBed.inject(testCase.service);
			ui = TestBed.inject(StructuresUiService);
			ui.DiffMode = false;
			ui.BranchIdString = '50';
			ui.messageIdString = '10';
			ui.subMessageIdString = '20';
			ui.connectionIdString = '5';
			ui.subMessageBreadCrumbsString = '10>20';
			ui.difference = [];
			if (testCase.pushStructure) {
				ui.singleStructureIdValue = '10';
			}
		});

		beforeEach(
			() =>
				(scheduler = new TestScheduler((actual, expected) => {
					expect(actual).toEqual(expected);
				}))
		);

		it('should be created', () => {
			expect(service).toBeTruthy();
		});

		/**
		 * @todo Luciano fix or disable?
		 */
		xit('should fetch structures array with diff', () => {
			service.DiffMode = true;
			service.difference = changeReportMock;
			service.subMessageId = '201301';
			scheduler.run(({ expectObservable }) => {
				service.branchId = '10';
				expectObservable(service.structures).toBe(
					testCase.diffEmission,
					testCase.values
				);
			});
		});

		afterEach(() => {
			ui.DiffMode = false;
			ui.difference = [];
		});
	});
});
