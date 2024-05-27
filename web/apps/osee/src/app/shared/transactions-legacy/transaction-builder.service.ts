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
import { Injectable } from '@angular/core';
import {
	legacyArtifact,
	legacyCreateArtifact,
	legacyAttributeType,
	legacyModifyArtifact,
	legacyTransaction,
	legacyRelation,
} from '@osee/transactions/types';
import { TransactionTranslations } from './transactions.translations';

@Injectable({
	providedIn: 'root',
})
export class TransactionBuilderService {
	translations: TransactionTranslations = new TransactionTranslations();

	createArtifact<T extends Partial<legacyCreateArtifact & legacyArtifact>>(
		value: T,
		artifactType: string,
		relations: legacyRelation[],
		transaction?: legacyTransaction,
		branchId?: string,
		txComment?: string,
		key?: string
	): legacyTransaction {
		const currentTransaction: legacyTransaction = transaction || {
			branch: branchId || '',
			txComment: txComment || '',
			createArtifacts: [],
		};
		if (currentTransaction.createArtifacts === undefined) {
			currentTransaction.createArtifacts = [];
		}
		const attributes: legacyAttributeType[] = [];
		(
			Object.entries(value) as [
				string,
				string | number | boolean | string[],
			][]
		).forEach((entry) => {
			if (
				entry[0] !== 'name' &&
				entry[0] !== 'applicabilityId' &&
				entry[0] !== 'applicability' &&
				entry[0] !== 'id' &&
				!this.translations.contains(entry[0])
			) {
				attributes.push({
					typeName: this.attributeNameToHumanReadable(entry[0]),
					value: entry[1],
				});
			} else if (
				this.translations.contains(entry[0]) &&
				this.translations.transform(entry[0]) !== entry[0]
			) {
				attributes.push({
					typeName: this.translations.transform(entry[0]),
					value: entry[1],
				});
			}
		});
		const artifact: legacyCreateArtifact = {
			typeId: artifactType,
			name: value?.name || '',
			applicabilityId: value?.applicabilityId || value?.applicability?.id,
			attributes: attributes,
			relations: relations,
			key: key,
		};
		currentTransaction.createArtifacts!.push(artifact);
		return currentTransaction;
	}

	modifyArtifact<T extends Partial<legacyArtifact>>(
		value: T,
		transaction?: legacyTransaction,
		branchId?: string,
		txComment?: string
	) {
		const currentTransaction: legacyTransaction = transaction || {
			branch: branchId || '',
			txComment: txComment || '',
			modifyArtifacts: [],
		};
		const attributes: legacyAttributeType[] = [];
		(
			Object.entries(value) as [
				string,
				string | number | boolean | string[],
			][]
		).forEach((entry) => {
			if (
				// entry[0] !== 'name' && //name needs to be modifiable attribute
				entry[0] !== 'applicabilityId' &&
				entry[0] !== 'applicability' &&
				entry[0] !== 'id' &&
				!this.translations.contains(entry[0])
			) {
				attributes.push({
					typeName: this.attributeNameToHumanReadable(entry[0]),
					value: entry[1],
				});
			} else if (
				this.translations.contains(entry[0]) &&
				this.translations.transform(entry[0]) !== entry[0]
			) {
				attributes.push({
					typeName: this.translations.transform(entry[0]),
					value: entry[1],
				});
			}
		});
		const modifyArtifact: legacyModifyArtifact = {
			id: value?.id || '',
			applicabilityId: value?.applicability?.id,
			setAttributes: attributes,
		};
		if (!currentTransaction.modifyArtifacts) {
			currentTransaction.modifyArtifacts = [];
		}
		currentTransaction.modifyArtifacts!.push(modifyArtifact);
		//relation code
		return currentTransaction;
	}

	deleteArtifact(
		value: string,
		transaction?: legacyTransaction,
		branchId?: string,
		txComment?: string
	) {
		const currentTransaction: legacyTransaction = transaction || {
			branch: branchId || '',
			txComment: txComment || '',
			deleteArtifacts: [],
		};
		currentTransaction.deleteArtifacts!.push(value);
		return currentTransaction;
	}

	deleteArtifacts(values: string[], branchId: string, txComment: string) {
		let transaction: legacyTransaction = {
			branch: branchId,
			txComment: txComment,
			deleteArtifacts: [],
		};
		values.forEach((value) => {
			transaction = this.deleteArtifact(value, transaction);
		});
		return transaction;
	}

	modifyArtifacts(
		values: unknown[],
		branchId: string,
		txComment: string
	): legacyTransaction {
		let transaction: legacyTransaction = {
			branch: branchId,
			txComment: txComment,
			modifyArtifacts: [],
		};
		values.forEach((value) => {
			transaction = this.modifyArtifact(
				value as legacyArtifact,
				transaction
			);
		});
		return transaction;
	}

	createArtifacts(
		values: unknown[],
		relations: legacyRelation[][],
		branchId: string,
		txComment: string,
		artifactTypes: string[]
	): legacyTransaction {
		let transaction: legacyTransaction = {
			branch: branchId,
			txComment: txComment,
			createArtifacts: [],
		};
		values.forEach((value, index) => {
			transaction = this.createArtifact(
				value as legacyCreateArtifact & legacyArtifact,
				artifactTypes[index],
				relations[index],
				transaction
			);
		});
		return transaction;
	}

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
		const currentTransaction: legacyTransaction = transaction || {
			branch: branchId || '',
			txComment: txComment || '',
			addRelations: [],
		};
		if (!currentTransaction?.addRelations) {
			currentTransaction.addRelations = [];
		}
		currentTransaction.addRelations.push({
			typeName: typeName,
			typeId: typeId,
			aArtId: firstId,
			bArtId: secondId,
			afterArtifact: afterArtifactId,
			rationale: rationale,
		});
		return currentTransaction;
	}

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
		const currentTransaction: legacyTransaction = transaction || {
			branch: branchId || '',
			txComment: txComment || '',
			deleteRelations: [],
		};
		if (!currentTransaction?.deleteRelations) {
			currentTransaction.deleteRelations = [];
		}
		currentTransaction.deleteRelations.push({
			typeName: typeName,
			typeId: typeId,
			aArtId: firstId,
			bArtId: secondId,
			rationale: rationale,
		});
		return currentTransaction;
	}

	private attributeNameToHumanReadable(value: string): string {
		const values = value
			.match(/[A-Z]+[^A-Z]*|[^A-Z]+/g)
			?.map((val) =>
				val.replace(/(^\w{1})|(\s+\w{1})/g, (letter) =>
					letter.toUpperCase()
				)
			);
		return values!.join(' ');
	}
}
