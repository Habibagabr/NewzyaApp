
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.habiba.newsapp.repository.repository
import com.habiba.newsapp.viewmodel.SearchViewModel

class SearchViewModelFactory(private val repository: repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}