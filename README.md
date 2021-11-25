## Artic

A simple library to send notifications with Firebase.

<hr>

### Getting started

It is easy to work with Artic! You have to just add one dependency.

- Firebase Database Legacy Library

#### Using Artic

````java
 // call this when the application starts
 Artic.initialize(activity);

 // initialize Artic with an firebase details
 artic = new Artic(activity, token, url);
 
 // send a simple message accross all devices
 // noitificationTag: unique identifier string for the notification 
 // title, message: values for the notification        
         
 artic.simpleMessage(notificationTag, title, message);
````

Artic uses Firebase to send notifications across devices.

#### Handle notifications

Artic is mainly designed to work in the background.

````java
// save the values so artic can use them
Artic.watch(activity, token, url, topic);

// initialize the service
ArticService.initialize(activity);
````

To use Artic, you also need to declare some things.

````xml
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>

<application>
    <service android:name="xyz.kumaraswamy.artic.ArticService"
             android:permission="android.permission.BIND_JOB_SERVICE"
             android:exported="true"/>
    <receiver android:name="xyz.kumaraswamy.artic.ArticAlarm"
              android:exported="true"/>
</application>
````

<hr>

### Preview

![](images/screenshot.png)