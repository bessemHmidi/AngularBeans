[![Build Status](https://travis-ci.org/bessemHmidi/AngularBeans.svg?branch=master)](https://travis-ci.org/bessemHmidi/AngularBeans)

# AngularBeans

![AngularBeans_Logo](https://cloud.githubusercontent.com/assets/1442690/8021018/e493e554-0c87-11e5-81ab-4dc894897044.png "AngularBeans Logo")

AngularBeans is a framework whose intention is to use JavaEE7 -and more precisely the CDI specification- with AngularJS. It creates "_AngularJS aware_" JavaEE Applications.

## Features:
- Generate ready to inject and callable AngularJS Service from CDI Beans.
- Binds `$scope` to bean models.
- Handles HTTP methods calls.  
- Detailed control over server side and client side data updates.
- Handle real-time calls via WebSockets or SockJS integration.
- queryModels: queries from server to client and updates the models.
- Events and queryModels broadcast.
- Angular form validation based on Bean Validation annotations.
- Built in i18n Translation.
- Event Driven, Real Time, and lightweight.

## How to run the [demo](https://github.com/bessemHmidi/AngularBeans/tree/master/demoApp):
1. Install [maven](https://maven.apache.org/).
2. Under the AngularBeans directory run `mvn clean install`.
3. Navigate to the demoApp directory and run `mvn package`.
4. Deploy the war file to your application server.
 

[![Join the chat at https://gitter.im/bessemHmidi/AngularBeans](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/bessemHmidi/AngularBeans?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
