package com.example.expensetracker

import ExpenseListViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.expensetracker.databinding.FragmentExpenseListBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.database.Expense

private const val TAG = "ExpenseListFragment"
class ExpenseListFragment : Fragment() {

    private val expenseListViewModel: ExpenseListViewModel by viewModels()
    private var _binding: FragmentExpenseListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExpenseListBinding.inflate(inflater, container, false)
        val adapter = ExpenseListAdapter(emptyList(), this::onItemClick)
        binding.expenseRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.expenseRecyclerView.adapter = adapter

        val addExpenseButton = binding.addExpenseButton
        addExpenseButton.setOnClickListener {
            findNavController().navigate(R.id.action_expenseListFragment_to_addExpenseFragment)
        }

        val categories = arrayOf("All Categories", "Food", "Entertainment", "Housing", "Utilities", "Fuel", "Automotive", "Misc")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = spinnerAdapter

        // Handle spinner item selection
        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = parent?.getItemAtPosition(position).toString()
                // Handle selection
                if (selectedCategory == "All Categories") {
                    // Load all expenses
                    expenseListViewModel.getAllExpenses()
                } else {
                    // Load expenses for selected category
                    expenseListViewModel.getExpensesByCategory(selectedCategory)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        expenseListViewModel.expenseList.observe(viewLifecycleOwner) { expenseList ->
            adapter.getExpenseList(expenseList)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun onItemClick(expense: Expense) {

        val bundle = Bundle().apply {
            // Pass individual parameters
            putLong("id", expense.id)
            putString("title", expense.title)
            putDouble("amount", expense.amount)
            putString("category", expense.category)
            putLong("selectedDate", expense.date.time)
        }
        // Navigate to NewsDetailFragment using NavController
        findNavController().navigate(
            R.id.action_expenseListFragment_to_editExpenseFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}