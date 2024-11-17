package com.example.todolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.ItemTodoBinding

class TodoFetch (
    private val onTodoCheckedChanged: (Todo, Boolean) -> Unit
) : RecyclerView.Adapter<TodoFetch.TodoViewHolder>() {

    inner class TodoViewHolder(val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(todo: Todo) {
            binding.apply {
                task.text = todo.title
                done.isChecked = todo.completed

                // Reset the listener to avoid triggering it when binding
                done.setOnCheckedChangeListener(null)
                done.isChecked = todo.completed

                // Set up new listener
                done.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked != todo.completed) {
                        onTodoCheckedChanged(todo, isChecked)
                    }
                }

                // Make the whole container clickable
                container.setOnClickListener {
                    done.isChecked = !done.isChecked
                }
            }
        }
    }

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Todo, newItem: Todo) =
            oldItem == newItem
    })

    fun submitList(list: List<Todo>) {
        differ.submitList(list)
    }

    override fun getItemCount() = differ.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TodoViewHolder(
            ItemTodoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }
}