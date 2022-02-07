<h2>Project Overview</h2>
<p>This project consist of two modules, one SDK module that provides some public APIs and one App module that uses these APIs.</p>

<h3>SDK Module</h3>
<p>The SDK module has been developed using Kotlin language and RxKotlin to manage most of the blocking API calls.<br>
Retrofit has been used to map HTTP calls needed and Jackson has been used as Json parsing library.<br>
KoTest has been used as main testing library, most of the code has been tested through property-based testing, using custom and existing object generators.<br>
Espresso has been used for UI tests<br>
The SDK is also protected by R8 obfuscation that is applied if the SDK is built alone and even if it is shipped inside any application.</p>

<h4>SDK Architecture and Structure</h4>
<p>In terms of Architecture, the SDK structure tried to follow the Clean Architecture concepts.<br>
The application logic is represented by the UseCases classes; their dependencies are all interfaces or function, in this way all of them are easily mockable in the test environment.<br>
So, the external ("dirty") layer of the environment can not take place inside the Architecture thanks to the interfaces used.</p>
<p>Considering the goal of the SDK, there were no needs to create a Business Logic layer. Usually the Business Logic layer (with Business models) is the core of a software Architecture but in this case there are just three standalone public API exposed.<br>
It would be possible to create a Business layer even here: the HTTP responses should be taken and then shaped in a more expressive and meaningful way, creating new models in order to use them in the Application Logic layer.</p>

<h3>App Module</h3>
<p>The application goal was to allow users to search a pokemon getting his image and his "shakespeare" description. This task should be done using the SDK built<br>
The application uses the same technologies of the SDK (RxKotlin, KoTest, Espresso).</p>

<h4>App Architecture and Structure</h4>
<p>The app is built upon a kind of M-V-I architecture.<br>
All the potential external events (mapped in observables thanks to RxBindings) are attached at the start of the view, and disposed at the stop<br>
While the view is active, the events flow through the observables and the ViewModel take care of their reactions. Every external event will result in a potential state change.<br>
This is possible because the external event chains will execute Application Logic and they emit an Action that will be take and evaluate in order to change an internal ViewModel state.<br>
This state will be then emitted to the subscriber (in this case the View)</p>
<p>It's important to understand that this ViewModel state doesn't represent a Business Model, but it is just a representation of the UI state (we could consider it as an interface between dirty layer and application layer).</p>
<p>In this case the Business layer is represented just by the data class PokemonModel, that is a re-shape of the SDK return objects.</p>
<p>Even in the App all the external ("dirty") dependencies (as the SDK itself for example) are well covered by interfaces</p>