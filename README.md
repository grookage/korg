# korg

Korg is a periodic, time-backed in-memory object refresher. It aims at providing

- An http-backend from where you could fetch your java object that can be held in-memory and refreshed according to the interval specified
- Korg also provides a marshaller interface that you can use to convert the in-memory object (byte array) into the POJO of your choice.

## Maven Dependency

- The bom is available at

```
<dependency>
    <groupId>com.grookage.apps</groupId>
    <artifactId>korg</artifactId>
    <version>latest</version>
</dependency>
```

## Build Instructions

- Clone the source:

      git clone github.com/grookage/korg

- Build

      mvn install

## Getting Started

### Concepts

- **KorgHttpSupplier** - An abstract class. The http backend that you can customize to construct your own supplier, for that you'll just need to supply the relevant http_url and construct the OkHttpRequest
- **KorgHttpRefresher** - The time backed refresher that takes care of periodic refresher. 
- **Marshaller** - The marshaller that you can bind to convert into the required POJO

## Example

Let's imagine you'll need the following object `UserDetails` fetched and cached in-memory. And assume `userDetails` looks like the following

```
{
    "firstName" : "John",
    "lastName" : "Cena"
}
```

And is available at abc.com/apis/userDetails/{id}

To support the above object caching in-memory. 

1. Have a UserDetails model included in your java code

```
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDetails {
    String firstName;
    String lastName;
}
```

2. The marshaller for the same looks like the following 

```
public class TestMarshaller implements Marshaller<TestDetails> {
    @Override
    public UserDetails marshall(byte[] body) {
        return ObjectMapper.readValue(body, UserDetails.class)
    }
}
```

3. Define the following Supplier

```
    final var supplier = new TestHttpSupplier<>(clientConfig, new TestMarshaller(), "testSupplier") {
            @Override
            protected String url() {
                return "/apis/userDetails/{id}";
            }

            @Override
            protected Request getRequest(String url) {
                return new Request.Builder()
                        .url(endPoint(url))
                        .get()
                        .build();
            }
     };
     supplier.start();
```

clientConfig in the above is the `KorgHttpConfiguration`

4. Instantiate the refresher

```
public class TestRefresher extends KorgHttpRefresher<UserDetails> {

    @Builder
    public TestRefresher(TestHttpSupplier supplier, int refreshTimeInSeconds,
                               boolean periodicRefresh) {
        super(supplier, refreshTimeInSeconds, periodicRefresh);
    }
}

final var refresher = new TestRefresher(supplier, refreshTimeSeconds, periodicRefresh);
```

periodicRefresh - can be set to false if only one time fetch is required. 
refresher.getData() gives you the current in-memory data. Null values on endPoint are ignored and older value is retained. There is also a staleness threshold configured after which older values can be evicted.
refresher.refresh() does explicit refresh

LICENSE
-------

Copyright 2024 Koushik R <rkoushik.14@gmail.com>.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
