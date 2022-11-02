import { of } from 'rxjs';
import { subMessage } from '../../message-interface/types/sub-messages';
import { WarningDialogService } from '../services/ui/warning-dialog.service';
import { element } from '../types/element';
import { structure } from '../types/structure';

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
export const warningDialogServiceMock: Partial<WarningDialogService> = {
	openSubMessageDialog(body: Partial<subMessage>) {
		return of(body);
	},

	openStructureDialog(body: Partial<structure>) {
		return of(body);
	},

	openElementDialog(body: Partial<element>) {
		return of(body);
	},
};
