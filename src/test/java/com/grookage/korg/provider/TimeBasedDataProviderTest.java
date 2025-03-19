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

package com.grookage.korg.provider;

import com.grookage.korg.exceptions.KorgErrorCode;
import com.grookage.korg.exceptions.KorgException;
import com.grookage.korg.provider.TimeBasedDataProvider;
import com.grookage.korg.stubs.TestSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.awaitility.Awaitility.await;


class TimeBasedDataProviderTest {

    @Test
    void testTimeBasedProvider() {
        final var testSupplier = new TestSupplier();
        final var timeBasedProvider = new TimeBasedDataProvider<>(
                testSupplier,
                1,
                TimeUnit.SECONDS,
                true
        );
        var testDetails = timeBasedProvider.getData();
        Assertions.assertNull(testDetails);
        timeBasedProvider.start();
        testDetails = timeBasedProvider.getData();
        Assertions.assertNotNull(testDetails);
        Assertions.assertEquals("attribute1", testDetails.getAttribute1());
        Assertions.assertEquals("attribute2", testDetails.getAttribute2());
        Assertions.assertEquals("attribute3", testDetails.getAttribute3());
        testSupplier.unmark();
        await().pollDelay(Duration.ofSeconds(4)).until(testSupplier::referenceUnset);
        testDetails = timeBasedProvider.getData();
        Assertions.assertNotNull(testDetails);
        Assertions.assertNull(testDetails.getAttribute1());
        Assertions.assertNull(testDetails.getAttribute2());
        Assertions.assertNull(testDetails.getAttribute3());
    }

    @Test
    void testTimeBasedProviderStart_whenSupplierReturnNull() {
        final var testSupplier = Mockito.mock(Supplier.class);
        final var timeBasedProvider = new TimeBasedDataProvider<>(
                testSupplier,
                1,
                TimeUnit.SECONDS,
                true
        );
        Assertions.assertNull(timeBasedProvider.getData());
        Mockito.doReturn(null).when(testSupplier).get();
        final var exception = Assertions.assertThrows(KorgException.class, timeBasedProvider::start);
        Assertions.assertEquals(KorgErrorCode.REFRESH_FAILED.getStatus(), exception.getStatus());
    }

    @Test
    void testTimeBasedProviderStart_WhenSupplierThrowsException() {
        final var testSupplier = Mockito.mock(Supplier.class);
        final var timeBasedProvider = new TimeBasedDataProvider<>(
                testSupplier,
                1,
                TimeUnit.SECONDS,
                true
        );
        Assertions.assertNull(timeBasedProvider.getData());
        final var runtimeException = new RuntimeException("Error in fetching data from supplier");
        Mockito.doThrow(runtimeException).when(testSupplier).get();
        final var exception = Assertions.assertThrows(KorgException.class, timeBasedProvider::start);
        Assertions.assertEquals(KorgErrorCode.REFRESH_FAILED.getStatus(), exception.getStatus());
    }
}
