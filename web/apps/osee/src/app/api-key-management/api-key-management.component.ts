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
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ApiKeyGeneratorComponent } from './lib/components/api-key-generator/api-key-generator.component';

@Component({
	selector: 'osee-api-key-management',
	imports: [ApiKeyGeneratorComponent],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: '<osee-api-key-generator></osee-api-key-generator>',
})
export class ApiKeyManagementComponent {}
export default ApiKeyManagementComponent;
