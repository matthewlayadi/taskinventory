package com.matthewlay.taskinventory.ui.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.matthewlay.taskinventory.data.model.Task
import com.matthewlay.taskinventory.data.remote.RetrofitClient
import com.matthewlay.taskinventory.databinding.FragmentTaskDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!
    private var currentTask: Task? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Ambil data yang dikirim dari List
        currentTask = arguments?.getSerializable("task_data") as? Task

        if (currentTask != null) {
            displayTaskData(currentTask!!)
        }

        // 2. Tombol START (Ubah status ke In Progress)
        binding.btnStart.setOnClickListener {
            updateTaskStatus("In Progress")
        }

        // 3. Tombol FINISH (Ubah status ke Done)
        binding.btnFinish.setOnClickListener {
            updateTaskStatus("Done")
        }
    }

    private fun displayTaskData(task: Task) {
        binding.tvDetailTitle.text = task.title
        binding.tvDetailDesc.text = task.description
        binding.tvDetailCategory.text = "Category: ${task.category}"

        // Atur tampilan Status Badge
        binding.tvStatusBadge.text = task.status
        when (task.status) {
            "New" -> {
                binding.tvStatusBadge.setTextColor(Color.parseColor("#1976D2"))
                binding.tvStatusBadge.setBackgroundColor(Color.parseColor("#E3F2FD"))
                // Tampilkan tombol Start, sembunyikan Finish
                binding.btnStart.visibility = View.VISIBLE
                binding.btnFinish.visibility = View.GONE
                binding.layoutDuration.visibility = View.GONE
            }
            "In Progress" -> {
                binding.tvStatusBadge.setTextColor(Color.parseColor("#F57C00"))
                binding.tvStatusBadge.setBackgroundColor(Color.parseColor("#FFF3E0"))
                // Sembunyikan Start, tampilkan Finish
                binding.btnStart.visibility = View.GONE
                binding.btnFinish.visibility = View.VISIBLE
                binding.layoutDuration.visibility = View.GONE
            }
            "Done" -> {
                binding.tvStatusBadge.setTextColor(Color.parseColor("#388E3C"))
                binding.tvStatusBadge.setBackgroundColor(Color.parseColor("#E8F5E9"))
                // Sembunyikan semua tombol aksi
                binding.btnStart.visibility = View.GONE
                binding.btnFinish.visibility = View.GONE

                // Tampilkan durasi jika ada
                binding.layoutDuration.visibility = View.VISIBLE
                binding.tvDuration.text = task.duration ?: "Calculated by Server"
            }
        }
    }

    private fun updateTaskStatus(newStatus: String) {
        val task = currentTask ?: return

        // Update object lokal
        task.status = newStatus

        // Panggil API PUT
        // Pastikan task.id tidak null
        if (task.id == null) return

        RetrofitClient.instance.updateTaskStatus(task.id!!, task).enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Status Updated to $newStatus", Toast.LENGTH_SHORT).show()
                    // Update tampilan UI dengan data baru dari server (termasuk durasi)
                    val updatedTask = response.body()
                    if (updatedTask != null) {
                        currentTask = updatedTask
                        displayTaskData(updatedTask)
                    }
                } else {
                    Toast.makeText(context, "Update Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
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