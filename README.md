![](https://cdn.rawgit.com/voxxrin/static/master/images/voxxrin.png =100x)

# Voxxrin 2

Your conference companion !

It's licensed under the very commercial friendly Apache License 2.

You can get more details from the web site at http://voxxr.in/

## Main features

* Social authentication
* List of available conferences (past / futures)
* List of scheduled presentations
* Tag a talk as favorite in order to be notified when it starts
 	* PUSH notification (mobile only)
* Tag a talk in order to be notified when a video content is published
	* PUSH notification (mobile only)
* Rate a talk (stars system)
* Consult the most favored talks and build your conference program
* Statistics (only for event administrators)
	* Most favored talk
	* Most stared talk
	* Best rated talk (and average rate)
	* Publish content (talk video recording for instance ...)
	* ...

## Conferences / Crawling

A set of HTTP crawlers is already available, so If you want an application to be available in voxxrin, thank you to open a pull request :) Available conferences are :
* Devoxx (FR, PL, BE, UK, ...)
* BDX I/O
* Mix-IT
* Codeurs en seine
* BreizhCamp
* Jug Summer Camp


## Main screens

#### Social authentication (Twitter, LinkedIn)
![](https://cdn.rawgit.com/voxxrin/static/master/images/voxxrin_1.png =300x)

#### List of all available events
![](https://cdn.rawgit.com/voxxrin/static/master/images/voxxrin_2.png =300x)

#### Scheduled talks
![](https://cdn.rawgit.com/voxxrin/static/master/images/voxxrin_3.png =300x)

#### Scheduled talk details (with room, summary, speaker bio, ...)
![](https://cdn.rawgit.com/voxxrin/static/master/images/voxxrin_4.png =300x)

#### Some statistics (only for event administrators)
![](https://cdn.rawgit.com/voxxrin/static/master/images/voxxrin_5.png =300x)

## Available into the next version

* Offline mode

## TODO

* Some other statistics
* Other type of notification when PUSH is not available : especially for web users
* Notify user when a new conference is available
* Make configurable notification system PUSH (what type of notification, a user receives) from mobile app
* Display a link to ticketing website from an event
* ...

## Contributing 

Contributions are welcome, fork the repo, push your changes to a branch and send a pull request :) !

Principal commiters / providers of idea are : 

* Elian ORIOU (@eoriou)
* Frédéric CAMBLOR (@fcamblor)
* Robin LOPEZ (@lopezrobin)

## Technologies

Used technologies are :

* server side
    * Java 7
    * [restx.io](http://restx.io)
    * [MongoDB](https://www.mongodb.com)
    * [Nimbus (JOSE + JWT)](http://connect2id.com/products/nimbus-jose-jwt)
* client side
    * [cordova](https://cordova.apache.org/)
    * [ionic](http://ionicframework.com)
    * [angularjs](https://angularjs.org)

The app is compound by a multi-module maven artifact:
##### common
> The shared model. All POJOs + serialization logic are stored into this module.
##### crawlers
> All implemented crawlers and configuration manager. Each crawler much extends the AbstractHttpCrawler class.
##### srv
> The voxxrin backend providing a REST API. Based on the web fmwk [restx.io](http://restx.io) and connected to a NoSQL [MongoDB](https://www.mongodb.com) database.
> Authentication is based on JWT (Json Web Token) & Social Providers (twitter, ...).
##### mobile
> The mobile / web app. Based on the mobile component fmwk [ionic](http://ionicframework.com).
> Web resources are served by the backend but are configured to be deployed into a [cordova](https://cordova.apache.org/) application.

## Bootstraping

##### server
Launch Java class ```voxxrin2.AppServer``` providing mandatory VM options with default values :
```
-Drestx.mode=prod
-Doauth.twitter.apiKey=_
-Doauth.twitter.apiSecret=_
-Doauth.linkedin.clientId=_
-Doauth.linkedin.apiSecret=_
-Doauth.secrets.token=_
```

##### client
In progress ...