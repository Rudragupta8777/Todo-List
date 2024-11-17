package com.example.todolist

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var todoAdapter: TodoFetch
    private var isLoading = false
    private val todos = mutableListOf<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSwipeRefresh()
        loadTodos()
    }

    private fun setupRecyclerView() {
        todoAdapter = TodoFetch { todo, isChecked ->
            handleTodoCheck(todo, isChecked)
        }

        binding.recyclerViews.apply {
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadTodos()
        }
    }

    private fun loadTodos() {
        if (isLoading) return

        lifecycleScope.launch {
            isLoading = true
            showLoading(true)

            try {
                val response = TodoInstance.api.getTodos()
                if (response.isSuccessful && response.body() != null) {
                    todos.clear()
                    todos.addAll(response.body()!!)
                    todoAdapter.submitList(todos.toList())
                } else {
                    showError("Failed to load todos")
                }
            } catch (e: IOException) {
                showError("Please check your internet connection")
            } catch (e: HttpException) {
                showError("Server error occurred")
            } finally {
                isLoading = false
                showLoading(false)
            }
        }
    }

    private fun handleTodoCheck(todo: Todo, isChecked: Boolean) {
        if (isChecked) {
            // Remove the todo from the list with animation
            val position = todos.indexOf(todo)
            if (position != -1) {
                todos.removeAt(position)
                todoAdapter.submitList(todos.toList())
                // Show a brief message using the correct context
                Toast.makeText(
                    this@MainActivity,
                    "Task completed and removed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.refresh.visibility = if (show) View.VISIBLE else View.GONE
        binding.swipeRefresh.isRefreshing = show
    }

    private fun showError(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
    }
}
