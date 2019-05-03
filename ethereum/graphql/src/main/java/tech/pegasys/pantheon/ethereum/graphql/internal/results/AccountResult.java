/*
 * Copyright 2018 ConsenSys AG.
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
package tech.pegasys.pantheon.ethereum.graphql.internal.results;

import tech.pegasys.pantheon.ethereum.core.Address;
import tech.pegasys.pantheon.ethereum.core.Wei;
import tech.pegasys.pantheon.util.bytes.BytesValue;
import tech.pegasys.pantheon.util.uint.UInt256;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({
  "address",
  "balance",
  "transactionCount",
  "code",
  "storage"
})
public class AccountResult implements GraphQLRpcResult {

  private final String address;
  private final String balance;
  private final String transactionCount;
  private final String code;
  private final String storage;


  public AccountResult(
      final Address address,
      final Wei balance,
      final Long transactionCount,
      final BytesValue code,
      final UInt256 storage) {
    this.address = address.toString();
    this.balance = balance.toString();
    this.transactionCount = transactionCount.toString();
    this.code = code.toString();
    this.storage = Quantity.create(storage);
  }

  @JsonGetter(value = "address")
  public String getAddress() {
    return address;
  }

  @JsonGetter(value = "balance")
  public String getBalance() {
    return balance;
  }

  @JsonGetter(value = "transactionCount")
  public String getTransactionCount() {
    return transactionCount;
  }

  @JsonGetter(value = "code")
  public String getCode() {
    return code;
  }

  @JsonGetter(value = "storage")
  public String getStorage() {
    return storage;
  }

}

