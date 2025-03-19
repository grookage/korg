/*
 * Copyright (c) 2024. Koushik R <rkoushik.14@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grookage.korg.refresher;

import com.grookage.korg.provider.TimeBasedDataProvider;
import com.grookage.korg.suppliers.KorgSupplier;

import java.util.concurrent.TimeUnit;

public abstract class AbstractKorgRefresher<T> implements KorgRefresher<T> {

    private final TimeBasedDataProvider<T> dataProvider;

    protected AbstractKorgRefresher(final KorgSupplier<T> supplier,
                                    final int dataRefreshInterval,
                                    final boolean periodicRefresh) {
        this.dataProvider = new TimeBasedDataProvider<>(
                supplier,
                null,
                dataRefreshInterval,
                TimeUnit.SECONDS,
                periodicRefresh
        );
        this.dataProvider.start();
    }

    @Override
    public T getData() {
        return dataProvider.getData();
    }

    @Override
    public void refresh() {
        dataProvider.update();
    }

}
