# Codespector

This is an IntelliJ plugin which does code inspection.

It inspects Kotlin data class and checks if each of the params are annotated with the required annotation.
If there is a missing annotation, then it highlights the error to the user.

Currently the following annotation are supported:
* SerializedName - Gson
* Json - Moshi
* SerialName - Kotlinx Serialization

You can get the plugin from [here][marketplace]

[marketplace]:https://plugins.jetbrains.com/plugin/19430-codespector


<img width="1512" alt="Screenshot 2022-07-27 at 11 42 53 PM" src="https://user-images.githubusercontent.com/31680582/181813661-dec86b26-2cf5-43e0-9867-bf83ad3e3e78.png">
<img width="1094" alt="Screenshot 2022-07-27 at 11 41 23 PM" src="https://user-images.githubusercontent.com/31680582/181813737-314c34b6-24d5-40d6-8acb-30d0ec39a58b.png">
