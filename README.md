# smart-grocery-agent-android
<div align="center">
  <video src="demo.mp4" width="320" autoplay loop muted></video>
  <p><i>Real-time AI streaming and dynamic cart management demo.</i></p>
</div>

AI-Powered Shopping Agent
This App is an advanced Android application that integrates an AI Shopping Agent capable of understanding user requests in real-time, fetching product recommendations from a backend data source, and managing an interactive shopping cart.

Built using modern Android development practices, the application communicates with a LangServe/LangChain backend over **Server-Sent Events (SSE)** to stream both AI conversational text and structured database product updates simultaneously.

## Features
* **AI Agent Chat Interface:** Direct, streaming chat interface where users can declare shopping needs (e.g., "What do you have?").

* **Real-time Server-Sent Events (SSE):** Seamless text chunk and product payload streaming using an asynchronous OkHttp EventSource listener.

* **Interactive Product Selection:** AI-driven category generation allows users to expand product items, alter quantities, and dynamically build a shopping cart directly inside the chat bubbles.

* **Dynamic Cart & Invoice Generation:** Automatic calculation of item counts, discount rates (`price_after_off`), and final total price presented in an optimized invoice summary layout.

* **Modern Jetpack Compose UI:** Fluid animations (`AnimatedVisibility`), customized edge-to-edge layouts, and RTL (Right-to-Left) language support for Persian localized UI elements.

---

## Project Architecture
The project is structured following clean architectural patterns and separation of concerns split cleanly into **Data** and **UI** layers:

```plaintext
app/src/main/java/com/example/productlistview/
├── ProductsApplication.kt        # Custom App subclass handling Manual DI initialization
├── MainActivity.kt               # Single Activity setup hosting the Compose NavHost
│
├── data/                         # Core Business Logic & Data Handling
│   ├── di/
│   │   └── AppContainer.kt       # Manual Dependency Injection container
│   ├── model/
│   │   ├── chat/
│   │   │   └── ChatMessage.kt    # Domain models for Chat states and counting UI data
│   │   ├── request/
│   │   │   └── LangServeRequest.kt  # Serialization request contracts for LangServe backend
│   │   └── response/
│   │       └── LangServeResponse.kt # Product schemas and structured JSON streaming payloads
│   ├── remote/
│   │   └── LangServeRemoteDataSource.kt # CallbackFlow-driven EventSource SSE handler
│   └── repository/
│       └── ProductsPhotosRepository.kt  # Clean repository interface wrapping data sources
│
└── ui/                           # Declarative Presentation Layer (Jetpack Compose)
    ├── ProductsApp.kt            # Navigation Host configuration and Screen enumerations
    ├── theme/                    # Material 3 typography, shapes, and color configurations
    └── screens/
        ├── introduce/
        │   └── IntroduceScreen.kt # Animated onboarding screen featuring developer details
        ├── products/
        │   ├── ProductsScreen.kt  # Main messaging zone containing list state management
        │   ├── ProductsUiState.kt # UI status trackers (loading formats, errors)
        │   └── ProductsViewModel.kt # StateFlow provider bridging repository events into state mutations
        └── invoice/
            └── InvoiceScreen.kt   # final breakdown summary computation UI
```
---

## Tech Stack & Libraries
* **Language:** Kotlin

* **UI Framework:** Jetpack Compose (Material 3)

* **Asynchronous Flow:** Kotlin Coroutines & Asynchronous Flows (`callbackFlow`, `StateFlow`)

* **Networking & Streaming:** OkHttp3 & OkHttp Server-Sent Events (`EventSources`)

* **Serialization:** Kotlinx Serialization (`@Serializable`)

* **Architecture Design:** MVVM (Model-View-ViewModel) + Repository Pattern

* **Dependency Injection:** Manual Dependency Injection via an explicit `AppContainer` asset initialized globally inside the custom `Application` subclass lifecycle.
---

## Core Mechanics: SSE Streaming Flow
The core powerhouse of the application lies within the `LangServeRemoteDataSource`. Instead of relying on standard REST responses, it builds an Event Pipeline that continuously monitors active event loops coming from the backend server:

```plaintext
 [User Inputs Query] 
         │
         ▼
 [ProductsViewModel] ────► passes request parameters to ────► [Repository]
                                                                  │
                                                                  ▼
 [StateFlow UI Re-render] ◄── [Emits UiStreamEvents] ◄── [LangServeRemoteDataSource]
                                                             (OkHttp EventSource)
```
1. `on_chain_start`: Captures pipeline execution states to post processing banners (e.g., "در حال تحلیل درخواست...").

2. `on_chat_model_stream`: Collects incremental final answer stream text chunks to build conversational responses seamlessly token-by-token.

3. `on_chain_end`: Extracts structural target datasets (`raw_db_data`) into concrete model parsing routines, dropping structured product UI boxes inside the ongoing chat history.
---

## Getting Started & Installation
Follow these steps to run the project locally on your machine:

### Prerequisites
* Android Studio (Ladybug or newer recommended)

* Android SDK 34+

* Gradle JDK 17+

### Steps
1. Clone this repository to your local directory:

```bash
git clone https://github.com/your-username/your-repo-name.git
```
2. Open Android Studio and choose **File > Open**, then select the root folder of the cloned repository.

3. Wait for Gradle synchronization to finish successfully.

4. Ensure the backend endpoint setup inside `DefaultAppContainer` is available or modify the target string to match your environment variables:

```Kotlin
private val baseUrl = "https://masoudsarafzadeh-shopping-agent-backend.hf.space/"
```
5. Select an active emulator or real device and click the **Run** button (`Shift + F10`).
---

## Developer Profile
**Masoud Sarafzadeh**

Android + AI Developer

* **Phone:** +98 933 118 4568

* **Telegram/Socials:** @MasoudSarafZadeh

* **Email:** masoudsarafzadeh@gmail.com
