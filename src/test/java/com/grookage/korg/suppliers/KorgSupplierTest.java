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

package com.grookage.korg.suppliers;

import com.grookage.korg.stubs.TestSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class KorgSupplierTest {

    @Test
    void testLeiaSupplier() {
        final var testableSupplier = new TestSupplier();
        testableSupplier.mark();
        final var testDetails = testableSupplier.get();
        Assertions.assertNotNull(testDetails);
        Assertions.assertEquals("attribute1", testDetails.getAttribute1());
        Assertions.assertEquals("attribute2", testDetails.getAttribute2());
        Assertions.assertEquals("attribute3", testDetails.getAttribute3());
    }
}
