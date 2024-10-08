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
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { TestScheduler } from 'rxjs/testing';
import { BranchInfoService } from '@osee/shared/services';
import { MessageUiService } from './messages-ui.service';
import { ApplicabilityListService } from '../../../../../shared/services/ple_aware/http/applicability-list.service';
import { MessagesService } from '../http/messages.service';
import { MimPreferencesService } from '../http/mim-preferences.service';
import { SubMessagesService } from '../http/sub-messages.service';
import { CurrentMessagesService } from './current-messages.service';
import {
	messageServiceMock,
	subMessageServiceMock,
	MimPreferencesServiceMock,
	messagesMock,
	subMessagesMock,
	MimPreferencesMock,
	warningDialogServiceMock,
} from '@osee/messaging/shared/testing';
import type { message, messageWithChanges } from '@osee/messaging/shared/types';
import { transactionResultMock } from '@osee/transactions/testing';
import {
	applicabilityListServiceMock,
	BranchInfoServiceMock,
	changeReportMock,
} from '@osee/shared/testing';
import { WarningDialogService } from './warning-dialog.service';
import { applicabilitySentinel } from '@osee/applicability/types';
import { CurrentTransactionService } from '@osee/transactions/services';
import { currentTransactionServiceMock } from '@osee/transactions/services/testing';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('CurrentMessagesService', () => {
	let service: CurrentMessagesService;
	let uiService: MessageUiService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [],
			providers: [
				{ provide: MessagesService, useValue: messageServiceMock },
				{
					provide: SubMessagesService,
					useValue: subMessageServiceMock,
				},
				{
					provide: ApplicabilityListService,
					useValue: applicabilityListServiceMock,
				},
				{
					provide: MimPreferencesService,
					useValue: MimPreferencesServiceMock,
				},
				{ provide: BranchInfoService, useValue: BranchInfoServiceMock },
				{
					provide: WarningDialogService,
					useValue: warningDialogServiceMock,
				},
				{
					provide: CurrentTransactionService,
					useValue: currentTransactionServiceMock,
				},
				{ provide: MessageUiService },
				CurrentMessagesService,
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		});
		service = TestBed.inject(CurrentMessagesService);
		uiService = TestBed.inject(MessageUiService);
	});

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);
	describe('no diffs', () => {
		beforeEach(() => {
			uiService.DiffMode = false;
			service.difference = [];
		});
		it('should be created', () => {
			expect(service).toBeTruthy();
		});

		//TODO: test doesn't work with signals
		xit('should fetch filtered messages', () => {
			scheduler.run(() => {
				// service.filter = 'filter';
				service.branch = '10';
				service.connection = '10';
				const expectedObservable = { a: messagesMock };
				const expectedMarble = '500ms a';
				scheduler
					.expectObservable(service.messages)
					.toBe(expectedMarble, expectedObservable);
			});
		});

		//TODO: doesn't work with signals
		xit('should update the list of all messages twice', () => {
			scheduler.run(({ cold }) => {
				service.branch = '10';
				service.connection = '10';
				const expectedObservable = { a: messagesMock };
				const expectedMarble = 'a 100ms a';
				const delayMarble = '-a';
				cold(delayMarble).subscribe(
					() => (uiService.updateMessages = true)
				);
				scheduler
					.expectObservable(service.allMessages)
					.toBe(expectedMarble, expectedObservable);
			});
		});
		it('should partially update a message', () => {
			scheduler.run(() => {
				service.branch = '10';
				service.connection = '10';
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(
						service.partialUpdateMessage(
							messagesMock[0],
							messagesMock[1]
						)
					)
					.toBe(expectedMarble, expectedObservable);
			});
		});
		it('should partially update a sub message', () => {
			scheduler.run(() => {
				service.branch = '10';
				service.connection = '10';
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					//TODO: at some point we should validate they are the same and do nothing
					.expectObservable(
						service.partialUpdateSubMessage(
							subMessagesMock[0],
							subMessagesMock[0]
						)
					)
					.toBe(expectedMarble, expectedObservable);
			});
		});

		it('should relate a sub message', () => {
			scheduler.run(() => {
				service.branch = '10';
				service.connection = '10';
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(service.relateSubMessage('15', '10'))
					.toBe(expectedMarble, expectedObservable);
			});
		});

		it('should create a sub message', () => {
			scheduler.run(() => {
				service.branch = '10';
				service.connection = '10';
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(
						service.createSubMessage(
							messagesMock[0].subMessages[0],
							'10'
						)
					)
					.toBe(expectedMarble, expectedObservable);
			});
		});

		it('should create a message', () => {
			scheduler.run(() => {
				service.branch = '10';
				service.connection = '10';
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(service.createMessage(messagesMock[0]))
					.toBe(expectedMarble, expectedObservable);
			});
		});
		it('should delete a message', () => {
			scheduler.run(() => {
				service.branch = '10';
				service.connection = '10';
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(service.deleteMessage(messagesMock[0].id))
					.toBe(expectedMarble, expectedObservable);
			});
		});

		//TODO: doesn't work with signals
		xit('should remove a message', () => {
			scheduler.run(() => {
				service.branch = '10';
				service.connection = '10';
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(service.removeMessage(messagesMock[0].id))
					.toBe(expectedMarble, expectedObservable);
			});
		});

		it('should delete a submessage', () => {
			scheduler.run(() => {
				service.branch = '10';
				service.connection = '10';
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(
						service.deleteSubMessage(subMessagesMock[0].id)
					)
					.toBe(expectedMarble, expectedObservable);
			});
		});

		it('should remove a submessage', () => {
			scheduler.run(() => {
				service.branch = '10';
				service.connection = '10';
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(
						service.removeSubMessage(
							subMessagesMock[0].id,
							messagesMock[0].id
						)
					)
					.toBe(expectedMarble, expectedObservable);
			});
		});

		it('should fetch preferences', () => {
			scheduler.run(() => {
				const expectedFilterValues = { a: MimPreferencesMock };
				const expectedMarble = 'a';
				service.branch = '10';
				scheduler
					.expectObservable(service.preferences)
					.toBe(expectedMarble, expectedFilterValues);
			});
		});

		it('should update user preferences', () => {
			scheduler.run(() => {
				service.branch = '10';
				const expectedObservable = { a: transactionResultMock };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(
						service.updatePreferences({
							branchId: '10',
							allowedHeaders1: [
								'name',
								'description',
								'applicability',
							],
							allowedHeaders2: [
								'name',
								'description',
								'applicability',
							],
							allHeaders1: ['name', 'description'],
							allHeaders2: ['name', 'description'],
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

		it('done should emit', () => {
			scheduler.run(({ expectObservable, cold }) => {
				const expectedFilterValues = {
					a: true,
					b: undefined,
					c: false,
				};
				const expectedMarble = '-(a)';
				const delayMarble = '-a';
				cold(delayMarble).subscribe(() => (service.toggleDone = true));
				expectObservable(service.done).toBe(
					expectedMarble,
					expectedFilterValues
				);
			});
		});
	});

	it('should get a connection path', () => {
		scheduler.run(({ expectObservable }) => {
			service.branchId = '10';
			service.branchType = 'working';
			expectObservable(service.connectionsRoute).toBe('a', {
				a: '/ple/messaging/connections/working/10',
			});
		});
	});
	//TODO: test doesn't work with signals currently
	xdescribe('diffs', () => {
		it('should get messages,submessages with differences', () => {
			scheduler.run(({ expectObservable }) => {
				service.difference = changeReportMock;
				service.DiffMode = true;
				service.branch = '10';
				const expectedValues: {
					a: (message | messageWithChanges)[];
					b: (message | messageWithChanges)[];
					c: (message | messageWithChanges)[];
					d: (message | messageWithChanges)[];
					e: (message | messageWithChanges)[];
					f: (message | messageWithChanges)[];
				} = {
					a: [
						{
							id: '0',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message0',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [
								{
									id: '1',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '0',
									},
									autogenerated: false,
									applicability: {
										id: '1',
										name: 'Base',
									},
									changes: {
										name: {
											previousValue: '',
											currentValue: 'submessage0',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
								},
							],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '1',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '0',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							applicability: {
								id: '1',
								name: 'Base',
							},
							changes: {
								name: {
									previousValue: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: '',
									},
									currentValue: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'name',
									},
									transactionToken: {
										id: '-1',
										branchId: '-1',
									},
								},
							},
						},
					],
					b: [
						{
							id: '0',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message0',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [
								{
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '0',
									},
									id: '1',
									gammaId: '-1',
									autogenerated: false,
									applicability: {
										id: '1',
										name: 'Base',
									},
									changes: {
										name: {
											previousValue: '',
											currentValue: 'submessage0',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
								},
							],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '1',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '0',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							applicability: {
								id: '1',
								name: 'Base',
							},
							changes: {
								name: {
									previousValue: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: '',
									},
									currentValue: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'name',
									},
									transactionToken: {
										id: '-1',
										branchId: '-1',
									},
								},
							},
						},
						{
							id: '1',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message1',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '1',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '1',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							applicability: {
								id: '1',
								name: 'Base',
							},
						},
					],
					c: [
						{
							id: '0',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message0',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [
								{
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '0',
									},
									id: '1',
									gammaId: '-1',
									autogenerated: false,
									applicability: {
										id: '1',
										name: 'Base',
									},
									changes: {
										name: {
											previousValue: '',
											currentValue: 'submessage0',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
								},
							],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '1',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '0',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							applicability: {
								id: '1',
								name: 'Base',
							},
							changes: {
								name: {
									previousValue: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: '',
									},
									currentValue: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'name',
									},
									transactionToken: {
										id: '-1',
										branchId: '-1',
									},
								},
							},
						},
						{
							id: '1',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message1',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '1',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '1',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							applicability: {
								id: '1',
								name: 'Base',
							},
						},
						{
							id: '201289',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message4',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [
								{
									added: false,
									deleted: true,
									changes: {},
									id: '201300',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '',
									},
									applicability: { id: '1', name: 'Base' },
								},
								{
									added: false,
									deleted: true,
									changes: {
										name: {
											previousValue: 'test submessage 6',
											currentValue: 'test submessage 6',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										interfaceSubMessageNumber: {
											previousValue: '762',
											currentValue: '762',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										description: {
											previousValue: 'uiop',
											currentValue: 'uiop',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
									id: '201302',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '',
									},
									applicability: { id: '1', name: 'Base' },
								},
								{
									added: false,
									deleted: true,
									changes: {
										name: {
											previousValue: null,
											currentValue: 'test submessage 8',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										interfaceSubMessageNumber: {
											previousValue: null,
											currentValue: '85',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										description: {
											previousValue: null,
											currentValue: 'dfd',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
									id: '201305',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '',
									},
									applicability: { id: '1', name: 'Base' },
								},
							],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '5',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '2',
							},
							added: false,
							hasSubMessageChanges: true,
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							changes: {},
						},
					],
					d: [
						{
							id: '0',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message0',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [
								{
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '0',
									},
									id: '1',
									gammaId: '-1',
									autogenerated: false,
									applicability: {
										id: '1',
										name: 'Base',
									},
									changes: {
										name: {
											previousValue: '',
											currentValue: 'submessage0',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
								},
							],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '1',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '0',
							},
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							changes: {
								name: {
									previousValue: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: '',
									},
									currentValue: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'name',
									},
									transactionToken: {
										id: '-1',
										branchId: '-1',
									},
								},
							},
						},
						{
							id: '1',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message1',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '1',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '1',
							},
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
						},
						{
							id: '201289',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message4',
							},
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '5',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							added: false,
							changes: {},
							subMessages: [
								{
									added: false,
									deleted: true,
									changes: {},
									id: '201300',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '',
									},
									applicability: { id: '1', name: 'Base' },
								},
								{
									added: false,
									deleted: true,
									changes: {
										name: {
											previousValue: 'test submessage 6',
											currentValue: 'test submessage 6',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										interfaceSubMessageNumber: {
											previousValue: '762',
											currentValue: '762',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										description: {
											previousValue: 'uiop',
											currentValue: 'uiop',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
									id: '201302',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '',
									},
									applicability: { id: '1', name: 'Base' },
								},
								{
									added: false,
									deleted: true,
									changes: {
										name: {
											previousValue: null,
											currentValue: 'test submessage 8',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										interfaceSubMessageNumber: {
											previousValue: null,
											currentValue: '85',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										description: {
											previousValue: null,
											currentValue: 'dfd',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
									id: '201305',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '',
									},
									applicability: { id: '1', name: 'Base' },
								},
							],
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '2',
							},
							hasSubMessageChanges: true,
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
						},
						{
							id: '201300',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message3',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [
								{
									id: '201305',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'abcdef',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: 'ghijk',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '25',
									},
									applicability: { id: '1', name: 'Base' },
									changes: {
										name: {
											previousValue: null,
											currentValue: 'test submessage 8',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										interfaceSubMessageNumber: {
											previousValue: null,
											currentValue: '85',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										description: {
											previousValue: null,
											currentValue: 'dfd',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
									added: false,
								},
							],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '5',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '2',
							},
							hasSubMessageChanges: true,
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
						},
					],
					e: [
						{
							id: '0',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message0',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [
								{
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '0',
									},
									id: '1',
									gammaId: '-1',
									autogenerated: false,
									applicability: {
										id: '1',
										name: 'Base',
									},
									changes: {
										name: {
											previousValue: '',
											currentValue: 'submessage0',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
								},
							],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '1',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '0',
							},
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							changes: {
								name: {
									previousValue: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: '',
									},
									currentValue: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'name',
									},
									transactionToken: {
										id: '-1',
										branchId: '-1',
									},
								},
							},
						},
						{
							id: '1',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message1',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '1',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '1',
							},
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
						},
						{
							id: '201289',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message4',
							},
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '5',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [
								{
									id: '201300',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '',
									},
									applicability: {
										id: '1',
										name: 'Base',
									},
									added: false,
									deleted: true,
									changes: {},
								},
								{
									added: false,
									deleted: true,
									changes: {
										name: {
											previousValue: 'test submessage 6',
											currentValue: 'test submessage 6',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										interfaceSubMessageNumber: {
											previousValue: '762',
											currentValue: '762',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										description: {
											previousValue: 'uiop',
											currentValue: 'uiop',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
									id: '201302',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '',
									},
									applicability: {
										id: '1',
										name: 'Base',
									},
								},
								{
									added: false,
									deleted: true,
									changes: {
										name: {
											previousValue: null,
											currentValue: 'test submessage 8',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										interfaceSubMessageNumber: {
											previousValue: null,
											currentValue: '85',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										description: {
											previousValue: null,
											currentValue: 'dfd',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
									id: '201305',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '',
									},
									applicability: {
										id: '1',
										name: 'Base',
									},
								},
							],
							changes: {},
							added: false,
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '2',
							},
							hasSubMessageChanges: true,
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
						},
						{
							id: '201300',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message3',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [
								{
									id: '201305',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'abcdef',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: 'ghijk',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '25',
									},
									applicability: {
										id: '1',
										name: 'Base',
									},
									changes: {
										name: {
											previousValue: null,
											currentValue: 'test submessage 8',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										interfaceSubMessageNumber: {
											previousValue: null,
											currentValue: '85',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										description: {
											previousValue: null,
											currentValue: 'dfd',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
									added: false,
								},
							],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '5',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '2',
							},
							hasSubMessageChanges: true,
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
						},
						{
							id: '201303',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message0',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '1',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '0',
							},
							added: false,
							deleted: true,
							hasSubMessageChanges: false,
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [],
							subscriberNodes: [],
							changes: {},
						},
					],
					f: [
						{
							id: '0',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message0',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [
								{
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '0',
									},
									id: '1',
									gammaId: '-1',
									autogenerated: false,
									applicability: {
										id: '1',
										name: 'Base',
									},
									changes: {
										name: {
											previousValue: '',
											currentValue: 'submessage0',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
								},
							],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '1',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '0',
							},
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							changes: {
								name: {
									previousValue: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: '',
									},
									currentValue: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'name',
									},
									transactionToken: {
										id: '-1',
										branchId: '-1',
									},
								},
							},
						},
						{
							id: '1',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message1',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '1',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '1',
							},
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
						},
						{
							id: '201289',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message4',
							},
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '5',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [
								{
									id: '201300',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '',
									},
									applicability: {
										id: '1',
										name: 'Base',
									},
									added: false,
									deleted: true,
									changes: {},
								},
								{
									added: false,
									deleted: true,
									changes: {
										name: {
											previousValue: 'test submessage 6',
											currentValue: 'test submessage 6',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										interfaceSubMessageNumber: {
											previousValue: '762',
											currentValue: '762',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										description: {
											previousValue: 'uiop',
											currentValue: 'uiop',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
									id: '201302',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '',
									},
									applicability: {
										id: '1',
										name: 'Base',
									},
								},
								{
									added: false,
									deleted: true,
									changes: {
										name: {
											previousValue: null,
											currentValue: 'test submessage 8',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										interfaceSubMessageNumber: {
											previousValue: null,
											currentValue: '85',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										description: {
											previousValue: null,
											currentValue: 'dfd',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
									id: '201305',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'submessage0',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '',
									},
									applicability: {
										id: '1',
										name: 'Base',
									},
								},
							],
							changes: {},
							added: false,
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '2',
							},
							hasSubMessageChanges: true,
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
						},
						{
							id: '201300',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message3',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [
								{
									id: '201305',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'abcdef',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: 'ghijk',
									},
									interfaceSubMessageNumber: {
										id: '-1',
										typeId: '2455059983007225769',
										gammaId: '-1',
										value: '25',
									},
									applicability: {
										id: '1',
										name: 'Base',
									},
									changes: {
										name: {
											previousValue: null,
											currentValue: 'test submessage 8',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										interfaceSubMessageNumber: {
											previousValue: null,
											currentValue: '85',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
										description: {
											previousValue: null,
											currentValue: 'dfd',
											transactionToken: {
												id: '-1',
												branchId: '-1',
											},
										},
									},
									added: false,
								},
							],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '5',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '2',
							},
							hasSubMessageChanges: true,
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
						},
						{
							id: '201303',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message0',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [],
							deleted: true,
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '1',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '0',
							},
							added: false,
							hasSubMessageChanges: false,
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [],
							subscriberNodes: [],
							changes: {},
						},
						{
							id: '201304',
							gammaId: '-1',
							name: {
								id: '-1',
								typeId: '1152921504606847088',
								gammaId: '-1',
								value: 'message2',
							},
							description: {
								id: '-1',
								typeId: '1152921504606847090',
								gammaId: '-1',
								value: 'description',
							},
							subMessages: [],
							interfaceMessageRate: {
								id: '-1',
								typeId: '2455059983007225763',
								gammaId: '-1',
								value: '5',
							},
							interfaceMessagePeriodicity: {
								id: '-1',
								typeId: '3899709087455064789',
								gammaId: '-1',
								value: 'Periodic',
							},
							interfaceMessageWriteAccess: {
								id: '-1',
								typeId: '2455059983007225754',
								gammaId: '-1',
								value: true,
							},
							interfaceMessageType: {
								id: '-1',
								typeId: '2455059983007225770',
								gammaId: '-1',
								value: 'Connection',
							},
							interfaceMessageNumber: {
								id: '-1',
								typeId: '2455059983007225768',
								gammaId: '-1',
								value: '2',
							},
							applicability: {
								id: '1',
								name: 'Base',
							},
							interfaceMessageExclude: {
								id: '-1',
								typeId: '2455059983007225811',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageIoMode: {
								id: '-1',
								typeId: '2455059983007225813',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageModeCode: {
								id: '-1',
								typeId: '2455059983007225810',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRateVer: {
								id: '-1',
								typeId: '2455059983007225805',
								gammaId: '-1',
								value: '',
							},
							interfaceMessagePriority: {
								id: '-1',
								typeId: '2455059983007225806',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageProtocol: {
								id: '-1',
								typeId: '2455059983007225809',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptWordCount: {
								id: '-1',
								typeId: '2455059983007225807',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRptCmdWord: {
								id: '-1',
								typeId: '2455059983007225808',
								gammaId: '-1',
								value: '',
							},
							interfaceMessageRunBeforeProc: {
								id: '-1',
								typeId: '2455059983007225812',
								gammaId: '-1',
								value: false,
							},
							interfaceMessageVer: {
								id: '-1',
								typeId: '2455059983007225804',
								gammaId: '-1',
								value: '',
							},
							publisherNodes: [
								{
									id: '100',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node1',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							subscriberNodes: [
								{
									id: '101',
									gammaId: '-1',
									name: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'Node2',
									},
									description: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									applicability: applicabilitySentinel,
									interfaceNodeNumber: {
										id: '-1',
										typeId: '5726596359647826657',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeGroupId: {
										id: '-1',
										typeId: '5726596359647826658',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBackgroundColor: {
										id: '-1',
										typeId: '5221290120300474048',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeAddress: {
										id: '-1',
										typeId: '5726596359647826656',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeBuildCodeGen: {
										id: '-1',
										typeId: '5806420174793066197',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGen: {
										id: '-1',
										typeId: '4980834335211418740',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeCodeGenName: {
										id: '-1',
										typeId: '5390401355909179776',
										gammaId: '-1',
										value: '',
									},
									nameAbbrev: {
										id: '-1',
										typeId: '8355308043647703563',
										gammaId: '-1',
										value: '',
									},
									interfaceNodeToolUse: {
										id: '-1',
										typeId: '5863226088234748106',
										gammaId: '-1',
										value: false,
									},
									interfaceNodeType: {
										id: '-1',
										typeId: '6981431177168910500',
										gammaId: '-1',
										value: '',
									},
									notes: {
										id: '-1',
										typeId: '1152921504606847085',
										gammaId: '-1',
										value: '',
									},
								},
							],
							added: false,
							hasSubMessageChanges: false,
							changes: {
								interfaceMessageWriteAccess: {
									previousValue: {
										id: '-1',
										typeId: '2455059983007225754',
										gammaId: '-1',
										value: false,
									},
									currentValue: {
										id: '-1',
										typeId: '2455059983007225754',
										gammaId: '-1',
										value: true,
									},
									transactionToken: {
										id: '-1',
										branchId: '-1',
									},
								},
								name: {
									previousValue: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: '',
									},
									currentValue: {
										id: '-1',
										typeId: '1152921504606847088',
										gammaId: '-1',
										value: 'test message 7',
									},
									transactionToken: {
										id: '-1',
										branchId: '-1',
									},
								},
								interfaceMessagePeriodicity: {
									previousValue: {
										id: '-1',
										typeId: '3899709087455064789',
										gammaId: '-1',
										value: '',
									},
									currentValue: {
										id: '-1',
										typeId: '3899709087455064789',
										gammaId: '-1',
										value: 'Periodic',
									},
									transactionToken: {
										id: '-1',
										branchId: '-1',
									},
								},
								interfaceMessageType: {
									previousValue: {
										id: '-1',
										typeId: '2455059983007225770',
										gammaId: '-1',
										value: '',
									},
									currentValue: {
										id: '-1',
										typeId: '2455059983007225770',
										gammaId: '-1',
										value: 'Operational',
									},
									transactionToken: {
										id: '-1',
										branchId: '-1',
									},
								},
								description: {
									previousValue: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: '',
									},
									currentValue: {
										id: '-1',
										typeId: '1152921504606847090',
										gammaId: '-1',
										value: 'dafda',
									},
									transactionToken: {
										id: '-1',
										branchId: '-1',
									},
								},
								interfaceMessageNumber: {
									previousValue: {
										id: '-1',
										typeId: '2455059983007225768',
										gammaId: '-1',
										value: '',
									},
									currentValue: {
										id: '-1',
										typeId: '2455059983007225768',
										gammaId: '-1',
										value: '741',
									},
									transactionToken: {
										id: '-1',
										branchId: '-1',
									},
								},
								interfaceMessageRate: {
									previousValue: {
										id: '-1',
										typeId: '2455059983007225763',
										gammaId: '-1',
										value: '',
									},
									currentValue: {
										id: '-1',
										typeId: '2455059983007225763',
										gammaId: '-1',
										value: '20',
									},
									transactionToken: {
										id: '-1',
										branchId: '-1',
									},
								},
							},
						},
					],
				};
				expectObservable(service.messages).toBe(
					'500ms (abcdef)',
					expectedValues
				);
			});
		});
		afterAll(() => {
			service.DiffMode = false;
			service.difference = [];
		});
	});
});
