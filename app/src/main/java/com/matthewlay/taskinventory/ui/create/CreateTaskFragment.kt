package com.matthewlay.taskinventory.ui.create

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.matthewlay.taskinventory.data.model.Task
import com.matthewlay.taskinventory.data.remote.RetrofitClient
import com.matthewlay.taskinventory.databinding.FragmentCreateTaskBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateTaskFragment : Fragment() {

    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            createTask()
        }
    }

    private fun createTask() {
        val title = binding.etTitle.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()

        if (title.isEmpty()) {
            binding.etTitle.error = "Title is required"
            return
        }

        // Ambil kategori dari RadioButton
        val selectedId = binding.rgCategory.checkedRadioButtonId
        if (selectedId == -1) {
            Toast.makeText(context, "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }
        val category = view?.findViewById<RadioButton>(selectedId)?.text.toString()

        // Siapkan data
        val newTask = Task(
            title = title,
            description = desc,
            category = category,
            status = "New" // Default status
        )

        // Kirim ke API
        RetrofitClient.instance.createTask(newTask).enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Task Created!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp() // Kembali ke halaman sebelumnya
                } else {
                    Toast.makeText(context, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Task>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}