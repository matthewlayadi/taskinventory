package com.matthewlay.taskinventory.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.matthewlay.taskinventory.R
import com.matthewlay.taskinventory.data.model.Task
import com.matthewlay.taskinventory.data.remote.RetrofitClient
import com.matthewlay.taskinventory.databinding.FragmentTaskListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskAdapter: TaskAdapter

    // Master data (semua task dari API)
    private var allTasksList: List<Task> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFilterButtons()
        fetchTasks()
    }

    // ================= RecyclerView =================
    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter { task ->
            val bundle = Bundle().apply {
                putSerializable("task_data", task)
            }
            findNavController().navigate(
                R.id.action_taskListFragment_to_taskDetailFragment,
                bundle
            )
        }

        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }

    // ================= Filter Buttons =================
    private fun setupFilterButtons() {
        binding.btnFilterAll.setOnClickListener { filterTasks("All") }
        binding.btnFilterNormal.setOnClickListener { filterTasks("Normal") }
        binding.btnFilterUrgent.setOnClickListener { filterTasks("Urgent") }
        binding.btnFilterImportant.setOnClickListener { filterTasks("Important") }
    }

    // ================= Filtering Logic =================
    private fun filterTasks(category: String) {
        val filteredList = if (category == "All") {
            allTasksList
        } else {
            allTasksList.filter {
                it.category.equals(category, ignoreCase = true)
            }
        }

        taskAdapter.submitList(filteredList)

        if (filteredList.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvEmpty.text = "No $category tasks found"
        } else {
            binding.tvEmpty.visibility = View.GONE
        }
    }

    // ================= API Call =================
    private fun fetchTasks() {
        binding.progressBar.visibility = View.VISIBLE

        RetrofitClient.instance.getAllTasks()
            .enqueue(object : Callback<List<Task>> {

                override fun onResponse(
                    call: Call<List<Task>>,
                    response: Response<List<Task>>
                ) {
                    binding.progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val tasks = response.body().orEmpty()
                        allTasksList = tasks

                        if (tasks.isEmpty()) {
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.tvEmpty.text = "No tasks available"
                        } else {
                            binding.tvEmpty.visibility = View.GONE
                            taskAdapter.submitList(tasks)
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to load tasks",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
