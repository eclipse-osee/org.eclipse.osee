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
import {
	legacyArtifact,
	legacyCreateArtifact,
	legacyRelation,
	legacyTransaction,
} from '@osee/transactions/types';
import { TransactionBuilderService } from '../transaction-builder.service';
import { transactionMock } from '@osee/transactions/testing';

export const transactionBuilderMock: Partial<TransactionBuilderService> = {
	createArtifact<T extends Partial<legacyCreateArtifact>>(
		value: T,
		artifactType: string,
		relations: legacyRelation[],
		transaction?: legacyTransaction,
		branchId?: string,
		txComment?: string
	) {
		return transactionMock;
	},
	deleteArtifact(
		value: string,
		transaction?: legacyTransaction,
		branchId?: string,
		txComment?: string
	) {
		return transactionMock;
	},
	modifyArtifact<T extends Partial<legacyArtifact>>(
		value: T,
		transaction?: legacyTransaction,
		branchId?: string,
		txComment?: string
	) {
		return transactionMock;
	},
	addRelation(
		typeName?: string,
		typeId?: string,
		firstId?: string,
		secondId?: string,
		afterArtifactId?: string,
		rationale?: string,
		transaction?: legacyTransaction,
		branchId?: string,
		txComment?: string
	) {
		return transactionMock;
	},
	deleteRelation(
		typeName?: string,
		typeId?: string,
		firstId?: string,
		secondId?: string,
		rationale?: string,
		transaction?: legacyTransaction,
		branchId?: string,
		txComment?: string
	) {
		return transactionMock;
	},
};
