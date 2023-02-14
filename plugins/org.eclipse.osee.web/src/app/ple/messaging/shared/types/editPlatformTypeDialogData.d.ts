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
import { editPlatformTypeDialogDataMode } from './EditPlatformTypeDialogDataMode.enum';
import type { PlatformType } from './platformType';

/**
 * Container containing info on whether or not the Edit Dialog should open in create/edit mode and what data to pre populate the fields with.
 */
export interface editPlatformTypeDialogData {
	mode: editPlatformTypeDialogDataMode;
	type: PlatformType;
}
