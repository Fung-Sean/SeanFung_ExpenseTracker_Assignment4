package com.example.expensetracker

import ExpenseListViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.expensetracker.databinding.FragmentAddExpenseBinding
import com.example.expensetracker.databinding.FragmentExpenseListBinding
import java.util.Calendar
import java.util.Date


private const val TAG = "AddExpenseFragment"
class AddExpenseFragment : Fragment() {
    private var _binding: FragmentAddExpenseBinding? = null

    private val binding
        get() = checkNotNull(_binding){
            "Cannot access binding. Is view visible?"
        }

    private val expenseListViewModel: ExpenseListViewModel by viewModels()

    private var title: String = ""
    private var amountText: String = ""
    private var category: String = ""
    private var date: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddExpenseBinding.inflate(inflater,container,false)

        return binding.root
    }
    private var selectedDate: Date? = null // Change the type to Date

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize date picker with current date
        val datePicker = view.findViewById<DatePicker>(R.id.datePicker)
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)
        datePicker.init(year, month, day) { _, year, monthOfYear, dayOfMonth ->
            // Handle selected date
            val selectedDateInMillis = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, monthOfYear)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }.timeInMillis
            selectedDate = Date(selectedDateInMillis) // Assign selectedDate to the Date object
            Log.d(TAG, "Selected date: $selectedDate")
        }
        val categories = arrayOf("Food", "Entertainment", "Housing", "Utilities", "Fuel", "Automotive", "Misc")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
        // Set onClickListener for the "Add Expense" button
        binding.addExpenseButton.setOnClickListener {
            addExpense()
        }
    }


    private fun addExpense() {
        title = binding.editTextName.text.toString().trim()
        Log.d(TAG,"title is $title")
        amountText = binding.editTextAmount.text.toString().trim()
        Log.d(TAG, "amount is $amountText")
        category = binding.spinnerCategory.selectedItem.toString()
        Log.d(TAG,"Category is category")
        val date = selectedDate
        Log.d(TAG, "date is $date")

        if (title.isEmpty() || amountText.isEmpty()) {
            // Show error message if expense name or amount is empty
            showToast("Please enter a valid expense name and amount")
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            // Show error message if amount is not a valid number or is non-positive
            showToast("Please enter a valid amount")
            return
        }

        if (category.isEmpty() || category == "Select Category") {
            // Show error message if category is not selected
            showToast("Please select a category")
            return
        }

        if (date == null) {
            // Show error message if date is not selected
            showToast("Please select a date")
            return
        }

        // Call the addExpense function in the view model to add the expense to the database
        expenseListViewModel.addExpense(title, amount, category, date)
        showToast("Successfully added Expense")
        findNavController().navigateUp()
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}