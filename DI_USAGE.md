# Dependency Injection Kullanımı

Bu projede Hilt yerine basit singleton pattern bazlı manuel DI kullanıyoruz.

## AppContainer Kullanımı

Tüm bağımlılıklar `AppContainer` object'inde singleton olarak tutuluyor.

### Nasıl Kullanılır?

```kotlin
// Repository'de kullanım
class BookRepositoryImpl : BookRepository {
    private val bookDao = AppContainer.bookDao
    private val apiService = AppContainer.libraryApiService

    override suspend fun getBooks(): List<Book> {
        return bookDao.getAllBooks()
    }
}

// ViewModel'de kullanım
class BookViewModel(
    private val repository: BookRepository = BookRepositoryImpl()
) : ViewModel() {
    // ViewModel logic
}

// Manuel olarak dependency geçme
val viewModel = BookViewModel(
    repository = BookRepositoryImpl()
)
```

### ViewModel Factory Kullanımı

ViewModels için factory pattern kullan:

```kotlin
class BookViewModelFactory(
    private val repository: BookRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// Compose'da kullanım
@Composable
fun BookScreen() {
    val viewModel: BookViewModel = viewModel(
        factory = BookViewModelFactory(BookRepositoryImpl())
    )
    // UI code
}
```

### Mevcut Dependencies

AppContainer şu anda şunları provide ediyor:

- `dataStore: DataStore<Preferences>` - Preferences storage
- `database: LibraryDatabase` - Room database
- `userDao`, `bookDao`, `favoriteDao`, `reviewDao` - DAOs
- `libraryApiService: LibraryApiService` - REST API service
- `authApiService: AuthApiService` - Auth API service

### Yeni Dependency Ekleme

AppContainer'a yeni dependency eklemek için:

```kotlin
object AppContainer {
    // ... existing code ...

    // Yeni dependency
    val myNewService: MyService by lazy {
        MyServiceImpl(/* dependencies */)
    }
}
```

## Test Edilebilirlik

Test'lerde mock dependencies kullanmak için:

```kotlin
@Test
fun testBookViewModel() {
    val mockRepository = mock(BookRepository::class.java)
    val viewModel = BookViewModel(mockRepository)
    // Test assertions
}
```

## Gelecekte Hilt'e Geçiş

Eğer ileride Hilt kullanmak isterseniz:

1. Hilt dependencies'i gradle'a ekleyin
2. AppContainer'ı Hilt modullerine dönüştürün
3. @HiltAndroidApp ve @AndroidEntryPoint annotationlarını ekleyin
4. @Inject constructorları kullanın

Ancak şimdilik bu basit yaklaşım yeterli ve çalışıyor!
