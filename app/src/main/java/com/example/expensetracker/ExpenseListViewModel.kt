import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.expensetracker.database.Expense
import com.example.expensetracker.database.ExpenseDao
import com.example.expensetracker.database.ExpenseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

private const val TAG = "ViewModel"
class ExpenseListViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        ExpenseDatabase::class.java,
        "expense_database"
    ).build()

    private val expenseDao: ExpenseDao = db.expenseDao()

    private val _expenseList = MutableLiveData<List<Expense>>()
    val expenseList: LiveData<List<Expense>> = _expenseList

    fun addExpense(title: String, amount: Double, type: String, date: Date) {
        val expense = Expense(title = title, amount = amount, category = type, date = date)
        viewModelScope.launch(Dispatchers.IO) {
            expenseDao.addExpense(expense)
        }
    }

    fun getAllExpenses(){
        viewModelScope.launch(Dispatchers.IO) {
            _expenseList.postValue(expenseDao.findAllExpenses())
        }
    }

    fun getExpensesByCategory(category : String){
        viewModelScope.launch(Dispatchers.IO) {
            _expenseList.postValue(expenseDao.findCategoryExpenses(category))
        }
    }

    fun getExpenseById(expenseId: Long): LiveData<Expense?> {
        val expenseLiveData = MutableLiveData<Expense?>()
        viewModelScope.launch(Dispatchers.IO) {
            val expense = expenseDao.findExpenseById(expenseId)
            expenseLiveData.postValue(expense)
        }
        return expenseLiveData
        Log.d(TAG, "id is $expenseId")
    }

    fun updateExpense(id: Long, newTitle: String, newAmount: Double, newCategory: String, newDate: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            // Retrieve the existing expense by its ID
            val existingExpense = expenseDao.findExpenseById(id)
            val updatedExpense = existingExpense?.copy(
                title = newTitle,
                amount = newAmount,
                category = newCategory,
                date = newDate
            )

            updatedExpense?.let {
                // Update the existing expense in the database
                expenseDao.updateExpense(it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        db.close()
    }
}