Hiroaki [![CircleCI](https://circleci.com/gh/JorgeCastilloPrz/hiroaki/tree/master.svg?style=svg&circle-token=3824cb7754fef5b81f1a67c6e86786df5db242c5)](https://circleci.com/gh/JorgeCastilloPrz/hiroaki/tree/master)
======

<img src="./art/sakura_logo.svg" width="256" height="256" />

*Hiroaki is a Japanese name that literally means 'spreading brightness'. It is derived from the 
words 'hiro', which means 'large or wide', and 'aki', which means 'bright or clear'.*

The intention of Hiroaki is to achieve that on your tests by simplifying the way you prepare 
your test environment prior to the test execution.

Usage
-----

Add the following code to your ``build.gradle``.

```groovy
dependencies{
    implementation 'com.jorgecastillo:hiroaki:0.0.1'
}
```

Hiroaki provides the following features:

Do you want to contribute?
--------------------------

I would love to get contributions from anybody. So if you feel that the library is lacking any features 
you consider key, please open an issue asking for it or a pull request providing an implementation for it. 

Any PR's must pass CI and that includes code style. Run the following commands to check code style or 
automatically format it.
```
// check code style
./gradlew app:ktlint
./gradlew hiroaki:ktlint 

// autoformat
./gradlew app:ktlintFormat
./gradlew hiroaki:ktlintFormat
```

Libraries used in this project
------------------------------

* [JUnit][4]
* [Mockito][5]

License
-------

    Copyright 2018 Jorge Castillo Pérez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

