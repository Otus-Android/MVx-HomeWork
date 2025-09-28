# MVIHomework

Данный проект является домашним заданием, посвященным миграции с MVVM на MVI (Model-View-Intent).

## Описание

Проект представляет собой приложение для отображения криптовалют с возможностью добавления монет в избранное. Текущая реализация использует архитектуру MVVM с `ViewModel` и `StateFlow`.

<img width="200" src="https://github.com/user-attachments/assets/c56619f9-e39a-44de-9ac8-4364a5e9988c" />
<img width="200" src="https://github.com/user-attachments/assets/23a9d4d4-5837-4ac2-9307-72cd26511bee" />

### Текущая архитектура (MVVM)

*   **ViewModel**: `CoinListViewModel`, `FavoriteViewModel` управляют состоянием экранов
*   **State**: `CoinsScreenState`, `FavoriteCoinsScreenState` описывают состояние UI
*   **Use Cases**: Бизнес-логика вынесена в отдельные use case классы
*   **Repository Pattern**: Доступ к данным через репозитории

## Задача

Необходимо мигрировать существующую MVVM архитектуру на MVI паттерн, сохранив текущую функциональность приложения.

### Рекомендуемые фреймворки

Можно выбрать любой из следующих подходов для MVI:

*   **[MVIKotlin](https://github.com/arkivanov/MVIKotlin)** - Kotlin Multiplatform MVI framework
*   **[OrbitMVI](https://github.com/orbit-mvi/orbit-mvi)** - Simple MVI for Android & Multiplatform
*   **[Circuit](https://github.com/slackhq/circuit)** - A Compose-driven architecture for Kotlin and Android apps
*   **[Redux-Kotlin](https://github.com/reduxkotlin/redux-kotlin)** - Redux implementation for Kotlin
*   **Собственная реализация MVI** - Создание MVI архитектуры без внешних библиотек

### Требования

*   ✅ Выделение Intent'ов для всех пользовательских действий
*   ✅ Единое immutable состояние для каждого экрана
*   ✅ Однонаправленный поток данных (Unidirectional Data Flow)
*   ✅ Отсутствие мутабельного состояния в UI слое
*   ✅ Сохранение всей текущей функциональности
