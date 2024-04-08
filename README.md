<p align="center">
<img src="/src/frontend/static/icons/Hipster_HeroLogoMaroon.svg" width="300" alt="Online Boutique" />
</p>

**Online Boutique** is a cloud-first microservices demo application.
Online Boutique consists of an 10-tier microservices application. The application is a
web-based e-commerce app where users can browse items,
add them to the cart, and purchase them.A

This repository is Froked from Google's https://github.com/GoogleCloudPlatform/microservices-demo. We use it to help us demonstrate Sealight's capabilities and features using a working example with documentation.

## Screenshots

| Home Page                                                                                                         | Checkout Screen                                                                                                    |
| ----------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------ |
| [![Screenshot of store homepage](/docs/img/online-boutique-frontend-1.png)](/docs/img/online-boutique-frontend-1.png) | [![Screenshot of checkout screen](/docs/img/online-boutique-frontend-2.png)](/docs/img/online-boutique-frontend-2.png) |



## Architecture

**Online Boutique** is composed of 10 microservices written in different
languages that talk to each other over gRPC.



[![Architecture of
microservices](/docs/img/architecture-diagram.png)](/docs/img/architecture-diagram.png)

| Service                                              | Language      | Sealights  agent type | Description                                                                                                                       |
| ---------------------------------------------------- | ------------- | --------------------- |--------------------------------------------------------------------------------------------------------------------------------- |
| [frontend](/src/frontend)                           | Go            | Normal agent           | Exposes an HTTP server to serve the website. Does not require signup/login and generates session IDs for all users automatically. |
| [cartservice](/src/cartservice)                     | C#            | CD agent               | Stores the items in the user's shopping cart in Redis and retrieves it.                                                           |
| [productcatalogservice](/src/productcatalogservice) | Go            | Normal agent           | Provides the list of products from a JSON file and ability to search products and get individual products.                        |
| [currencyservice](/src/currencyservice)             | Node.js       | Normal agent           | Converts one money amount to another currency. Uses real values fetched from European Central Bank. It's the highest QPS service. |
| [paymentservice](/src/paymentservice)               | Node.js       | Normal agent           | Charges the given credit card info (mock) with the given amount and returns a transaction ID.                                     |
| [shippingservice](/src/shippingservice)             | Go            | Normal agent           | Gives shipping cost estimates based on the shopping cart. Ships items to the given address (mock)                                 |
| [emailservice](/src/emailservice)                   | Python        | Normal agent           | Sends users an order confirmation email (mock).                                                                                   |
| [checkoutservice](/src/checkoutservice)             | Go            | Normal agent           | Retrieves user cart, prepares order and orchestrates the payment, shipping and the email notification.                            |
| [recommendationservice](/src/recommendationservice) | Python        | Normal agent           | Recommends other products based on what's given in the cart.                                                                      |
| [adservice](/src/adservice)                         | Java          | CD agent               | Provides text ads based on given context words.                                                                                   |

## Tests
We've prepared a bunch of test written in multiple testing frameworks, all are supported by sealights and sealights integrated with them. You can find all tests in ./integration-tests folder.

The tests are reported to sealights using either a normal agent with specific configuration or dedicated plugin.

| Testing framework                                     | Method        | Location                                   |
| ----------------------------------------------------- | ------------- | ------------------------------------------ |
| [Cypress](integration-tests/cypress)                  | Plugin        | integration-tests/cypress                  |
| [MS](integration-tests/dotnet-tests/MS-Tests)         | Agent         | integration-tests/dotnet-tests/MS-Tests    |
| [Cucumber-js](integration-tests/Cucumber-js)          | Plugin        | integration-tests/Cucumber-js              |
| [N-Unit](integration-tests/dotnet-tests/NUnit-Tests)  | Agent         | integration-tests/dotnet-tests/NUnit-Tests |
| [TestNG](integration-tests/support-testNG)            | Agent         | integration-tests/support-testNG           |
| [Robot](integration-tests/robot-tests)                | Plugin        | integration-tests/robot-tests              |
| [Cucumber](integration-tests/cucumber-framework)      | Agent         | integration-tests/cucumber-framework       |
| [Junit](integration-tests/java-tests)                 | Agent         | integration-tests/java-tests               |
| [Postman](integration-tests/postman-tests)            | Plugin        | integration-tests/postman-tests            |
| [Mocha](integration-tests/mocha)                      | Agent         | integration-tests/mocha                    |
| [SoapUI](integration-tests/soapUI)                    | Agent         | integration-tests/soapUI                   |
| [Pytest](integration-tests/python-tests)              | Agent         | integration-tests/python-tests             |
| [Karate](integration-tests/karate-tests)              | Agent         | integration-tests/karate-tests             |




Please refer to the documentation https://docs.sealights.io/boutique-project/