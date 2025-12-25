package com.matthewlay.taskinventory.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.matthewlay.taskinventory.data.model.Task
import com.matthewlay.taskinventory.databinding.ItemTaskBinding

class TaskAdapter(
    private val onItemClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val taskList = ArrayList<Task>()

    fun submitList(list: List<Task>) {
        taskList.clear()
        taskList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(taskList[position])
    }

    override fun getItemCount(): Int = taskList.size

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {

            // ===== TITLE & CATEGORY =====
            binding.tvItemTitle.text = task.title ?: "-"
            binding.tvItemCategory.text = task.category ?: "-"

            // ===== RESET VISIBILITY =====
            binding.btnAction.visibility = View.GONE
            binding.tvDuration.visibility = View.GONE

            // ===== STATUS HANDLING =====
            when (task.status ?: "") {

                "New" -> {
                    binding.btnAction.visibility = View.VISIBLE
                    binding.btnAction.text = "Take"
                    binding.btnAction.setOnClickListener {
                        onItemClick(task)
                    }
                }

                "In Progress" -> {
                    binding.btnAction.visibility = View.VISIBLE
                    binding.btnAction.text = "Done"
                    binding.btnAction.setOnClickListener {
                        onItemClick(task)
                    }
                }

                "Done" -> {
                    binding.tvDuration.visibility = View.VISIBLE
                    binding.tvDuration.text =
                        "Duration: ${task.duration ?: "-"}"
                }
            }

            // ===== ITEM CLICK =====
            binding.root.setOnClickListener {
                onItemClick(task)
            }
        }
    }
}
