/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.uniffle.server.storage.multi;

import org.apache.uniffle.server.ShuffleDataFlushEvent;
import org.apache.uniffle.server.ShuffleServerConf;
import org.apache.uniffle.server.storage.AbstractStorageManagerFallbackStrategy;
import org.apache.uniffle.server.storage.StorageManager;

import static org.apache.uniffle.server.ShuffleServerConf.FLUSH_COLD_STORAGE_THRESHOLD_SIZE;

public class DefaultStorageManagerSelector extends FallbackBasedStorageManagerSelector {
  private final long flushColdStorageThresholdSize;

  public DefaultStorageManagerSelector(
      StorageManager warmStorageManager,
      StorageManager coldStorageManager,
      AbstractStorageManagerFallbackStrategy fallbackStrategy,
      ShuffleServerConf rssConf) {
    super(warmStorageManager, coldStorageManager, fallbackStrategy);
    this.flushColdStorageThresholdSize = rssConf.get(FLUSH_COLD_STORAGE_THRESHOLD_SIZE);
  }

  @Override
  StorageManager regularSelect(ShuffleDataFlushEvent flushEvent) {
    StorageManager storageManager = warmStorageManager;
    if (flushEvent.getSize() > flushColdStorageThresholdSize) {
      storageManager = coldStorageManager;
    }
    return storageManager;
  }
}
