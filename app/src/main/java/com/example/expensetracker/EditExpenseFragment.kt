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
import com.example.expensetracker.databinding.FragmentEditExpenseBinding
import java.util.Calendar
import java.util.Date

private const val TAG = "EditExpenseFragment"

class EditExpenseFragment : Fragment() {
    private var _binding: FragmentEditExpenseBinding? = null

    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding. Is view visible?"
        }

    private val expenseListViewModel: ExpenseListViewModel by viewModels()

    private var title: String = ""
    private var amountText: String = ""
    private var category: String = ""
    private var date: Long = 0
    private var selectedDate: Long? = null

    private val categories = arrayOf("Food", "Entertainment", "Housing", "Utilities", "Fuel", "Automotive", "Misc")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the expense details from arguments
        val args = requireArguments()
        val expenseId = args.getLong("id", -1L)
        val title = args.getString("title", "")
        val amount = args.getDouble("amount", 0.0)
        val category = args.getString("category", "")
        var selectedDate = args.getLong("selectedDate", 0L)

        // Check if the retrieved values are valid
        if (expenseId != -1L && title != null && amount != null && category != null && selectedDate != 0L) {
            // Populate the UI with the retrieved expense details
            binding.updateTextName.setText(title)
            binding.updateTextAmount.setText(amount.toString())
            // Set the selected category in the spinner
            val categoryIndex = categories.indexOf(category)
            if (categoryIndex != -1) {
                binding.updateSpinnerCategory.setSelection(categoryIndex)
            }
            // Convert the selected date back to a Date object and set it in the date picker
            val calendar = Calendar.getInstance().apply {
                timeInMillis = selectedDate
            }
            val currentDate = Calendar.getInstance()
            val year = currentDate.get(Calendar.YEAR)
            val month = currentDate.get(Calendar.MONTH)
            val day = currentDate.get(Calendar.DAY_OF_MONTH)
            binding.updateDatePicker.init(year, month, day) { _, year, monthOfYear, dayOfMonth ->
                // Handle selected date
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, monthOfYear)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                selectedDate = selectedCalendar.timeInMillis // Assign selectedDate to the Long object
                Log.d(TAG, "Selected date: $selectedDate")
            }
        } else {
            // Handle invalid or missing arguments
            Toast.makeText(requireContext(), "Invalid expense details", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp() // Navigate back if necessary
        }
        setupCategorySpinner()
        // Set onClickListener for the "Save Expense" button
        binding.updateExpenseButton.setOnClickListener {
            // Call the saveExpense function with the retrieved expenseId
            saveExpense(expenseId)
        }
    }

    private fun saveExpense(id: Long) {
        title = binding.updateTextName.text.toString().trim()
        // Log.d(TAG,"title is $title")
        amountText = binding.updateTextAmount.text.toString().trim()
        // Log.d(TAG, "amount is $amountText")
        category = binding.updateSpinnerCategory.selectedItem.toString()
        // Log.d(TAG,"Category is $category")
        val date = selectedDate?.let { Date(it) } ?: Date()
        Log.d(TAG, "date is $date")
        Log.d(TAG, "selectedDate is $selectedDate")
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
        Log.d(TAG, "Date formatted is $date")
        // Update the expense in the database
        expenseListViewModel.updateExpense(id, title, amount, category, date)

        // Show success message
        showToast("Expense updated successfully")

        // Navigate back to the expense list fragment
        findNavController().navigateUp()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.updateSpinnerCategory.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
