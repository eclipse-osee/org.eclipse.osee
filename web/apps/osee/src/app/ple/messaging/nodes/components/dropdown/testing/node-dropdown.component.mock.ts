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
import { Component, input } from '@angular/core';
import { applicabilitySentinel } from '@osee/applicability/types';
import { nodeData, transportType } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-node-dropdown',
	template: '<div>Dummy</div>',
	standalone: true,
})
export class MockNodeDropdownComponent {
	selectedNodes = input.required<nodeData[]>();
	protectedNode = input<nodeData>({
		id: '-1',
		gammaId: '-1',
		name: {
			id: '-1',
			typeId: '1152921504606847088',
			gammaId: '-1',
			value: '',
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
	});
	transportType = input.required<transportType>();
	validationType = input<'connection' | 'publish' | 'subscribe'>(
		'connection'
	);
	required = input(false);
	disabled = input(false);

	hintHidden = input(false);
	label = input<string>('Select Nodes');
}
