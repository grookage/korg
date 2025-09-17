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

import com.grookage.korg.config.KorgHttpConfiguration;
import com.grookage.korg.endpoint.KorgEndPoint;
import com.grookage.korg.endpoint.KorgEndPointProvider;
import com.grookage.korg.endpoint.SimpleEndPointProvider;
import com.grookage.korg.exceptions.KorgErrorCode;
import com.grookage.korg.exceptions.KorgException;
import com.grookage.korg.marshal.Marshaller;
import com.grookage.korg.utils.OkHttpUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
public abstract class KorgHttpSupplier<T> implements KorgSupplier<T> {

    private final Marshaller<T> marshaller;
    private final KorgEndPointProvider endpointProvider;
    private final OkHttpClient okHttpClient;

    @SneakyThrows
    protected KorgHttpSupplier(
            KorgHttpConfiguration httpConfiguration,
            Marshaller<T> marshaller,
            String name
    ) {
        this.marshaller = marshaller;
        this.endpointProvider = SimpleEndPointProvider.builder()
                .endPoint(
                        KorgEndPoint.builder()
                                .host(httpConfiguration.getHost())
                                .port(httpConfiguration.getPort())
                                .rootPathPrefix(httpConfiguration.getRootPathPrefix())
                                .scheme(httpConfiguration.getScheme())
                                .build()
                )
                .build();
        this.okHttpClient = OkHttpUtils.okHttpClient(httpConfiguration, name);
    }

    @SneakyThrows
    protected KorgHttpSupplier(
            KorgHttpConfiguration httpConfiguration,
            Marshaller<T> marshaller,
            KorgEndPointProvider endPointProvider,
            String name
    ) {
        this.marshaller = marshaller;
        this.endpointProvider = endPointProvider;
        this.okHttpClient = OkHttpUtils.okHttpClient(httpConfiguration, name);
    }


    protected HttpUrl endPoint(final String path) {
        return endpointProvider.endPoint()
                .orElseThrow(IllegalArgumentException::new)
                .url(path);
    }

    @Override
    public T get() {
        final var url = url();
        try {
            final var request = getRequest(url);
            if (null == request) {
                return null; //Gracefully ignore here, the configSupplier will not override, on the null value
            }
            final var response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                log.error("Request Failed for uri {} with response:{}", url,
                        Objects.requireNonNull(response.body()).string());
                throw KorgException.error(KorgErrorCode.BAD_REQUEST);
            }
            final var body = OkHttpUtils.bodyAsBytes(response);
            return marshaller.marshall(body);
        } catch (Exception e) {
            log.error("Error while executing API with uri: {}", url, e);
            throw KorgException.error(KorgErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    protected abstract String url();

    protected abstract Request getRequest(String url);

    public void start() {
        endpointProvider.start();
    }

    public void stop() {
        endpointProvider.stop();
    }
}
