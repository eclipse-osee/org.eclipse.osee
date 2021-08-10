import {
  artifact,
  createArtifact,
  relation,
  transaction,
} from './transaction';
import { TransactionBuilderService } from './transaction-builder.service';
import { transactionMock } from './transaction.mock';

export const transactionBuilderMock: Partial<TransactionBuilderService> = {
  createArtifact<T extends Partial<createArtifact>>(
    value: T,
    artifactType: string,
    relations: relation[],
    transaction?: transaction,
    branchId?: string,
    txComment?: string
  ) {
    return transactionMock;
  },
  deleteArtifact(
    value: string,
    transaction?: transaction,
    branchId?: string,
    txComment?: string
  ) {
    return transactionMock;
  },
  modifyArtifact<T extends Partial<artifact>>(
    value: T,
    transaction?: transaction,
    branchId?: string,
    txComment?: string
  ) {
    return transactionMock;
  },
  addRelation(typeName?: string, typeId?: string, firstId?: string, secondId?: string, rationale?: string, transaction?: transaction, branchId?: string, txComment?: string) {
    return transactionMock;
  },
  deleteRelation(typeName?: string, typeId?: string, firstId?: string, secondId?: string, rationale?: string, transaction?: transaction, branchId?: string, txComment?: string) {
    return transactionMock;
  }
};
