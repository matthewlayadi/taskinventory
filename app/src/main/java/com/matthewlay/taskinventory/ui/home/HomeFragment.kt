package com.matthewlay.taskinventory.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.matthewlay.taskinventory.R
import com.matthewlay.taskinventory.data.model.Task
import com.matthewlay.taskinventory.data.remote.RetrofitClient
import com.matthewlay.taskinventory.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    // Ini teknik ViewBinding agar kita bisa akses ID di XML (binding.tvJudul, dll)
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- SETUP NAVIGASI ---

        // 1. Klik Tombol Tambah (+) -> Ke Create Task
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createTaskFragment)
        }

        // 2. Klik Kartu Statistik -> Ke Task List
        val goToList = View.OnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_taskListFragment)
        }

        binding.cardNew.setOnClickListener(goToList)
        binding.cardProgress.setOnClickListener(goToList)
        binding.cardDone.setOnClickListener(goToList)
    }

    // Menggunakan onResume agar saat kembali dari halaman "Create",
    // data otomatis ter-refresh dan angka bertambah.
    override fun onResume() {
        super.onResume()
        fetchTasks()
    }

    private fun fetchTasks() {
        // Panggil API GET /tasks
        RetrofitClient.instance.getAllTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                if (response.isSuccessful) {
                    val tasks = response.body() ?: emptyList()
                    // Jika sukses, hitung dan update tampilan
                    updateDashboard(tasks)
                } else {
                    Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                // Tampilkan error jika server mati / tidak ada internet
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", t.message.toString())
            }
        })
    }

    private fun updateDashboard(tasks: List<Task>) {
        // --- LOGIKA MENGHITUNG JUMLAH TASK ---

        val newCount = tasks.count { it.status == "New" }
        val progressCount = tasks.count { it.status == "In Progress" }
        val doneCount = tasks.count { it.status == "Done" }

        // Update angka di layar (XML)
        binding.tvCountNew.text = newCount.toString()
        binding.tvCountProgress.text = progressCount.toString()
        binding.tvCountDone.text = doneCount.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}