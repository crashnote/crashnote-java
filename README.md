# [Crashnote](http://crashnote.com#java): Error tracking for mobile, desktop and web apps

[crashnote.com](http://crashnote.com#java) is an online service that tracks errors in applications.

This makes it easier to understand where problems are and helps you to fix bugs faster.

To use the service for your Java-based apps, you just need to embed a small library: the notifier,
which manages everything for you.


## General Features

- pure Java
- works with JDK 1.5+
- smaller than 150 KB
- single JAR, no dependencies
- works asynchronously by default
- uses JSON format and GZIP compression
- supports all major logging tools: JDK JUL, Log4j and Logback


## Notifiers

Each application platform has it's own specific library that leverages its unique features and
obeys the according restrictions:

* **[Servlet](https://github.com/crashnote/crashnote-java/master/servlet)**
* **[App Engine](https://github.com/crashnote/crashnote-java/master/appengine)**
* **Android** (coming soon)
* **Desktop** (coming soon)


# License

This software is licensed under the Apache 2 license, quoted below.

Licensed under the Apache License, Version 2.0 (the “License”); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an “AS IS” BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.