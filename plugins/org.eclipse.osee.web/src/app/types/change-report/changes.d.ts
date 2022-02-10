import { difference } from "./change-report";
import { transactionToken } from "./transaction-token";

export type hasChanges<T = any> = {
    [K in keyof T]?:difference<T[K]>
}