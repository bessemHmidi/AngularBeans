# AngularBeans
AngularBeans is a framework whose intention is to use javaEE7 and more precisely the CDI specification with AngularJS 
it allows to create "AngularJS aware" JavaEE Applications .

#features:

- Generate ready to inject && callables AngularJs Service from CDI Beans.
- Binding $scope with beans models
- Handle HTTP methods calls.  
- Finest control over server side & client side data updates.
- Handle Real Time calls via webSockets or SockJS integration.
- queryModels: query's from server to client's to update their model's.
- Events and queryModels broadcast.
- Angular form validation based on Bean Validation annotations.
- Built in I18n Translation.
- Event Driven, Real Time, lightweight.

#How to run the demo:

1. Install maven.
2. Under the AngularBeans directory run `mvn clean install`.
3. Navigate to the demoApp directory and run `mvn package`.
4. deploy the war file to your application server.
