/*
 * Copyright 2019 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

/*
 *
 *# Account is an Ethereum account at a particular block.
type Account {
    # Address is the address owning the account.
    address: Address!
    # Balance is the balance of the account, in wei.
    balance: BigInt!
    # TransactionCount is the number of transactions sent from this account,
    # or in the case of a contract, the number of contracts created. Otherwise
    # known as the nonce.
    transactionCount: Long!
    # Code contains the smart contract code for this account, if the account
    # is a (non-self-destructed) contract.
    code: Bytes!
    # Storage provides access to the storage of a contract account, indexed
    # by its 32 byte slot identifier.
    storage(slot: Bytes32!): Bytes32!
}
 */
package tech.pegasys.pantheon.ethereum.graphql.internal.methods;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;


public class Account {
	private final BlockchainQueries blockchain;
	public Account(BlockchainQueries blockchain) {
		this.blockchain = blockchain;
	}
}
